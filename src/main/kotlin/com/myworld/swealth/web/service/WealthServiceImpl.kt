package com.myworld.swealth.web.service

import com.myworld.swealth.data.repository.ManualRepository
import com.myworld.swealth.data.repository.WealthRepository
import com.myworld.swealth.common.ApiResult
import com.myworld.swealth.common.ResultUtil
import com.myworld.swealth.common.StringUtil
import com.myworld.swealth.data.entity.Text
import com.myworld.swealth.data.entity.Tweet
import com.myworld.swealth.data.entity.Wealth
import com.myworld.swealth.data.model.TextOutline
import com.myworld.swealth.data.model.WealthOutline
import com.myworld.swealth.data.repository.TextRepository
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*
import javax.servlet.http.HttpServletResponse
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Service
class WealthServiceImpl : WealthService {
    private val log = LogManager.getRootLogger()

    @Autowired
    private lateinit var wealthRepository: WealthRepository
    @Autowired
    private lateinit var manualRepository: ManualRepository
    @Autowired
    private lateinit var textRepository: TextRepository
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    @Autowired
    private lateinit var textService: TextService

    override fun getWealthInfo(id: String): ApiResult<Any?> {
        val wealth = wealthRepository.findById(id)
        return if (wealth.isPresent) {
            ResultUtil.success(data=wealth)
        } else {
            ResultUtil.failure(code = -2, msg = "没有找到数据")
        }
    }

    @Throws(IOException::class)
    override fun getWealthManual(id: String, response: HttpServletResponse) {
        if (StringUtil.isInteger(id)) {
            val manual = manualRepository.findById(id)
            if (manual.isPresent) {
                IOUtils.copy(ByteArrayInputStream(manual.get().content), response.outputStream)
                val contentType = StringUtil.getFileMimeType(manual.get().fileSuffix!!)
                response.contentType = contentType
            }
        } else {
            val manual = manualRepository.findByUkey(id)
            if (manual != null) {
                IOUtils.copy(ByteArrayInputStream(manual.content), response.outputStream)
                val contentType = StringUtil.getFileMimeType(manual.fileSuffix!!)
                response.contentType = contentType
            }
        }
    }

    override fun wealthsToWealthOutlines(wealths: List<Wealth>): MutableList<WealthOutline> {
        val wealthOutlineList: MutableList<WealthOutline> = ArrayList()
        for (wealth in wealths) {
            val wealthOutline = WealthOutline(
                id = wealth.id,
                name = wealth.name,
                bankName = wealth.bankName,
                rateMax = wealth.rateMax,
                rateMin = wealth.rateMin,
                amountBuyMin = wealth.amountBuyMin,
                term = wealth.term,
                risk = wealth.risk
            )
            wealthOutlineList.add(wealthOutline)
        }
        return wealthOutlineList
    }


    override fun textAndWealth(bankName: String): ApiResult<Any?> {
        val pageIndex = 0
        val pageSize = 20
        val pageable: Pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.DESC, "createTime")

        val wealthsOption = wealthRepository.findByBankName(bankName, pageable)
        val wealths = wealthsOption.content.shuffled().take(5)

        val textsFinanceOption = textRepository.findByBankNameAndTypeMainAndTypeNextAndTypeOne(bankName = bankName, typeMain = "业务", typeNext = "公司金融", typeOne = "小微金融", pageable = pageable)
        val textsFinance = textsFinanceOption!!.content.shuffled().take(5)
        val textsNewsOption = textRepository.findByBankNameAndTypeMainAndTypeNextContaining(bankName = bankName, typeMain = "新闻", typeNext = "本行", pageable = pageable)
        val textsNews = textsNewsOption.content.shuffled().take(5)
        val textsHROption = textRepository.findByBankNameAndTypeMainAndTypeNextContaining(bankName = bankName, typeMain = "公告", typeNext = "招聘", pageable = pageable)
        val textsHR = textsHROption.content.shuffled().take(5)
        val textsBuyOption = textRepository.findByBankNameAndTypeMainAndTypeNextContaining(bankName = bankName, typeMain = "公告", typeNext = "采购", pageable = pageable)
        val textsBuy = textsBuyOption.content.shuffled().take(5)

        val sql = "SELECT * FROM text WHERE (bank_name = '$bankName') AND (type_main = '公告') AND (NOT type_next LIKE '%招聘%') AND (NOT type_next LIKE '%采购%') ORDER BY create_time DESC limit $pageIndex,$pageSize"
        val textImportantPaged: MutableList<Text> = jdbcTemplate.query(sql, BeanPropertyRowMapper(Text::class.java))
        val textImportant = textImportantPaged.shuffled().take(5)

        val wealthsList = wealthsToWealthOutlines(wealths=wealths)
        val textFinanceList = textService.textsToTextOutlines(texts = textsFinance)
        val textNewsList = textService.textsToTextOutlines(texts = textsNews)
        val textHRList = textService.textsToTextOutlines(texts = textsHR)
        val textBuyList = textService.textsToTextOutlines(texts = textsBuy)
        val textImportantList = textService.textsToTextOutlines(texts = textImportant)

        val map: MutableMap<String, Any> = HashMap()
        map["wealths"] = wealthsList
        map["textsFinance"] = textFinanceList
        map["textsNews"] = textNewsList
        map["textsHR"] = textHRList
        map["textsBuy"] = textBuyList
        map["textsImportant"] = textImportantList
        return ResultUtil.success(data = map)
    }

    override fun wealthMore(bankName: String?, pageSize: Int, pageIndex: Int): ApiResult<*>? {
        val pageable: Pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.DESC, "createTime")
        val wealthPaged = (if (bankName.isNullOrEmpty()) {
            wealthRepository.findAll(pageable)
        } else {
            wealthRepository.findByBankName(bankName, pageable)
        }) ?: return ResultUtil.failure(-2, "没有数据")
        val wealths = wealthPaged.content
        val wealthsList = wealthsToWealthOutlines(wealths=wealths)
        return ResultUtil.success(num = wealthPaged.totalElements, data = wealthsList)
    }


    override fun overallOutline(): ApiResult<*> {
        val pageableWealth: Pageable = PageRequest.of(0, 20, Sort.Direction.DESC, "rateMax")
        val wealths = wealthRepository.findAll(pageableWealth)
        val wealthsNeed = wealths.content.shuffled().take(5)

        val pageable: Pageable = PageRequest.of(0, 20, Sort.Direction.DESC, "createTime")
        val textsFinanceOption = textRepository.findByTypeMainAndTypeNextAndTypeOne(typeMain = "业务", typeNext = "公司金融", typeOne = "小微金融", pageable = pageable)
        val textsFinance = textsFinanceOption.content.shuffled().take(5)
        val textsNewsOption = textRepository.findByTypeMainAndTypeNextContaining(typeMain = "新闻", typeNext = "本行", pageable = pageable)
        val textsNews = textsNewsOption.content.shuffled().take(5)
        val textsHROption = textRepository.findByTypeMainAndTypeNextContaining(typeMain = "公告", typeNext = "招聘", pageable = pageable)
        val textsHR = textsHROption.content.shuffled().take(5)
        val textsBuyOption = textRepository.findByTypeMainAndTypeNextContaining(typeMain = "公告", typeNext = "采购", pageable = pageable)
        val textsBuy = textsBuyOption.content.shuffled().take(5)

        val start = 0
        val pageSize = 20
        val sql = "SELECT * FROM text WHERE (type_main = '公告') AND (NOT type_next LIKE '%招聘%') AND (NOT type_next LIKE '%采购%') ORDER BY create_time DESC limit $start,$pageSize"
        val textImportantPaged: MutableList<Text> = jdbcTemplate.query(sql, BeanPropertyRowMapper(Text::class.java))
        val textImportant = textImportantPaged.shuffled().take(5)

        val textFinanceList = textService.textsToTextOutlines(texts = textsFinance)
        val textNewsList = textService.textsToTextOutlines(texts = textsNews)
        val textHRList = textService.textsToTextOutlines(texts = textsHR)
        val textBuyList = textService.textsToTextOutlines(texts = textsBuy)
        val textImportantList = textService.textsToTextOutlines(texts = textImportant)

        val map: MutableMap<String, Any> = HashMap()
        map["wealths"] = wealthsNeed
        map["textsFinance"] = textFinanceList
        map["textsNews"] = textNewsList
        map["textsHR"] = textHRList
        map["textsBuy"] = textBuyList
        map["textsImportant"] = textImportantList
        return ResultUtil.success(data = map)
    }
}
