package com.github.l1an.variflight2steplife

import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.text.DecimalFormat
import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)

    println("请选择操作:")
    println("1. 转换 csv 文件")
    println("2. 检测 output 和手动 output 文件")

    when (scanner.nextLine().trim()) {
        "1" -> convertCSV()
        "2" -> checkCSV()
        else -> println("无效选择, 请输入1 或 2")
    }
}

fun convertCSV() {
    val inputFilePath = "src/main/resources/input.csv"
    val outputFilePath = "src/main/resources/output.csv"
    val decimalFormat = DecimalFormat("#.#######")

    try {
        CSVReader(FileReader(inputFilePath)).use {
            val header = it.readNext()
            // 确认 input 文件是否为飞常准文件
            if (
                header == null ||
                header.size < 9 ||
                !header.contentEquals(
                    arrayOf(
                        "Time",
                        "UTC TIME",
                        "Anum",
                        "Fnum",
                        "Height",
                        "Speed",
                        "Angle",
                        "Longitude",
                        "Latitude"
                    )
                )
            ) {
                println("input 文件不是飞常准文件")
                return
            }

            // 获取 input 文件的总行数
            val totalLines = it.readAll().size

            // 重新读取文件，因为上面的readAll会把整个文件读取到内存中
            CSVReader(FileReader(inputFilePath)).use { reader ->
                // 跳过 header 行
                reader.readNext()

                // 创建新的 CSV 文件
                CSVWriter(FileWriter(outputFilePath)).use { writer ->
                    // 写入 header 行
                    writer.writeNext(
                        arrayOf(
                            "dataTime",
                            "locType",
                            "longitude",
                            "latitude",
                            "heading",
                            "accuracy",
                            "speed",
                            "distance",
                            "isBackForeground",
                            "stepType",
                            "altitude"
                        )
                    )

                    var nextLine: Array<String>?

                    while (reader.readNext().also { nextLine = it } != null) {
                        // 处理数据
                        val dataTime = nextLine!![0]
                        val longitude = decimalFormat.format(nextLine!![7].toDouble())
                        val latitude = decimalFormat.format(nextLine!![8].toDouble())
                        val speed = decimalFormat.format(nextLine!![5].toDouble())
                        val altitude = decimalFormat.format(nextLine!![4].toDouble())
                        val outputLine = arrayOf(
                            dataTime, "1", longitude, latitude, "0", "14", speed, "0", "0", "0", altitude
                        )
                        writer.writeNext(outputLine)
                    }
                }
            }
            println("文件处理完成，共处理 $totalLines 行数据")
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun checkCSV() {
    val manualFilePath = "src/main/resources/manual_output.csv"
    val programFilePath = "src/main/resources/output.csv"

    try {
        CSVReader(FileReader(manualFilePath)).use { manualReader ->
            CSVReader(FileReader(programFilePath)).use { programReader ->
                var manualLine: Array<String>?
                var programLine: Array<String>?

                var lineNumber = 0
                var discrepanciesFound = false

                while (manualReader.readNext().also { manualLine = it } != null) {
                    lineNumber++
                    programLine = programReader.readNext()

                    if (programLine == null || !manualLine.contentEquals(programLine)) {
                        println("Discrepancy found at line $lineNumber")
                        println("Manual:  ${manualLine?.joinToString()}")
                        println("Program: ${programLine?.joinToString()}")
                        discrepanciesFound = true
                    }
                }

                if (!discrepanciesFound) {
                    println("Files match perfectly!")
                } else {
                    println("Discrepancies found in the files.")
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
