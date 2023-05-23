package com.example.ptbatch.util

import com.opencsv.CSVWriter
import org.slf4j.LoggerFactory
import java.io.FileWriter


class CustomCSVWriter {


    companion object {
        val log = LoggerFactory.getLogger(this::javaClass.name)

        fun write(fileName: String?, data: MutableList<Array<String>>): Int {
            var rows = 0
            try {
                CSVWriter(FileWriter(fileName)).use { writer ->
                    writer.writeAll(data)
                    rows = data.size
                }
            } catch (e: Exception) {
                log.error("CustomCSVWriter - write: CSV 파일 생성 실패, fileName: {}", fileName)
            }
            return rows
        }
    }
}