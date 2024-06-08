package com.github.l1an.variflight2steplife

import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

fun main() {
    val inputFilePath = "src/main/resources/input.csv"
    val outputFilePath = "src/main/resources/output.csv"

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
                        val longitude = nextLine!![7]
                        val latitude = nextLine!![8]
                        val speed = nextLine!![5]
                        val altitude = nextLine!![4]
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