package com.myworld.swealth.web.service

import com.myworld.swealth.common.ApiResult
import com.myworld.swealth.common.ResultUtil
import com.myworld.swealth.data.entity.Text
import com.myworld.swealth.data.model.TextOutline
import com.myworld.swealth.data.repository.TextRepository
import com.myworld.swealth.data.repository.WordcloudRepository
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
import javax.servlet.http.HttpServletResponse

@Service
class TextServiceImpl : TextService {

    private val log = LogManager.getRootLogger()

    @Autowired
    private lateinit var textRepository: TextRepository
    @Autowired
    private lateinit var wordcloudRepository: WordcloudRepository
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    override fun detail(id: String): ApiResult<Any?> {
        val text = textRepository.findById(id)
        return if (text.isPresent) {
            ResultUtil.success(data = text)
        } else {
            ResultUtil.failure(code = -2, msg = "没有找到数据")
        }
    }

    @Throws(IOException::class)
    override fun wordcloud(id: String, response: HttpServletResponse) {
        val file = wordcloudRepository.findById(id)
        if (file.isPresent) {
            IOUtils.copy(ByteArrayInputStream(file.get().image), response.outputStream)
            response.contentType = "image/png"
        }
    }

    // 作为textCategory的补充，使用jdbcTemplate查询
    override fun textMore(bankName: String?, pageSize: Int, pageIndex: Int): ApiResult<*>? {
        val start = pageIndex * pageSize
        val sqlCount = if (bankName.isNullOrEmpty()) {
            "SELECT count(*) FROM text WHERE (type_main = '公告') AND (NOT type_next LIKE '%招聘%') AND (NOT type_next LIKE '%采购%')"
        } else {
            "SELECT count(*) FROM text WHERE (bank_name = '$bankName') AND (type_main = '公告') AND (NOT type_next LIKE '%招聘%') AND (NOT type_next LIKE '%采购%')"
        }
        var totalCount = 0
        if (pageIndex == 0) {
            totalCount = jdbcTemplate.queryForObject(sqlCount, Integer::class.java) as Int
            if (totalCount == 0) {
                return ResultUtil.failure(-2, "没有数据")
            }
        }

        val sql = if (bankName.isNullOrEmpty()) {
            "SELECT * FROM text WHERE (type_main = '公告') AND (NOT type_next LIKE '%招聘%') AND (NOT type_next LIKE '%采购%') ORDER BY create_time DESC limit $start,$pageSize"
        } else {
            "SELECT * FROM text WHERE (bank_name = '$bankName') AND (type_main = '公告') AND (NOT type_next LIKE '%招聘%') AND (NOT type_next LIKE '%采购%') ORDER BY create_time DESC limit $start,$pageSize"
        }
        val textImportantPaged: MutableList<Text> = jdbcTemplate.query(sql, BeanPropertyRowMapper(Text::class.java))

        val textsList = textsToTextOutlines(textImportantPaged)
        return ResultUtil.success(num = totalCount.toLong(), data = textsList)
    }

    override fun textCategory(bankName: String?, keyone: String, keytwo: String?, pageSize: Int, pageIndex: Int): ApiResult<*>? {
        if (keyone == "公告" && keytwo == "其他") {
            return textMore(bankName = bankName, pageIndex = pageIndex, pageSize = pageSize)
        }

        val pageable: Pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.DESC, "createTime")
        if (keytwo.isNullOrEmpty()) {
            val textPaged = if (bankName.isNullOrEmpty()) {
                textRepository.findByTypeMain(typeMain = keyone, pageable = pageable)
            } else {
                textRepository.findByBankNameAndTypeMain(bankName = bankName, typeMain = keyone, pageable = pageable)
            }
            val textsList = textsToTextOutlines(textPaged.content)
            return ResultUtil.success(num = textPaged.totalElements, data = textsList)
        }

        val textPaged = if (bankName.isNullOrEmpty()) {
            textRepository.findByTypeMainAndTypeNextContaining(typeMain = keyone, typeNext = keytwo, pageable = pageable)
        } else {
            textRepository.findByBankNameAndTypeMainAndTypeNextContaining(bankName = bankName, typeMain = keyone, typeNext = keytwo, pageable = pageable)
        }
        val textsList = textsToTextOutlines(textPaged.content)
        return ResultUtil.success(num = textPaged.totalElements, data = textsList)
    }

    override fun textFinance(bankName: String?, keyone: String, keytwo: String, pageSize: Int, pageIndex: Int): ApiResult<*>? {
        val pageable: Pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.DESC, "createTime")
        val textPaged = if (bankName.isNullOrEmpty()) {
            textRepository.findByTypeMainAndTypeNextAndTypeOne(typeMain = "业务", typeNext = keyone, typeOne = keytwo, pageable = pageable)
        } else {
            textRepository.findByBankNameAndTypeMainAndTypeNextAndTypeOne(bankName = bankName, typeMain = "业务", typeNext = keyone, typeOne = keytwo, pageable = pageable)
        } ?: return ResultUtil.failure(-2, "没有数据")
        val textsList = textsToTextOutlines(textPaged.content)
        return ResultUtil.success(num = textPaged.totalElements, data = textsList)
    }


    override fun textsToTextOutlines(texts: List<Text>): MutableList<TextOutline> {
        val textOutlineList: MutableList<TextOutline> = ArrayList()
        for (text in texts) {
            val textOutline = TextOutline(id = text.id, name = text.name, bankName = text.bankName, date = text.date, typeMain = text.typeMain, typeNext = text.typeNext, photos = text.photos)
            textOutlineList.add(textOutline)
        }
        return textOutlineList
    }
}
