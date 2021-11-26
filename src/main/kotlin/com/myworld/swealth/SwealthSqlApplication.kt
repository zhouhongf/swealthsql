package com.myworld.swealth

import org.springframework.boot.SpringApplication
import org.springframework.cloud.client.SpringCloudApplication
import org.springframework.cloud.openfeign.EnableFeignClients


@EnableFeignClients
@SpringCloudApplication
open class SwealthSqlApplication

fun main(args: Array<String>) {
    SpringApplication.run(SwealthSqlApplication::class.java, *args)
}
