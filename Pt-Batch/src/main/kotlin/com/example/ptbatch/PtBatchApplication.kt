package com.example.ptbatch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class PtBatchApplication

fun main(args: Array<String>) {
    runApplication<PtBatchApplication>(*args)
}
