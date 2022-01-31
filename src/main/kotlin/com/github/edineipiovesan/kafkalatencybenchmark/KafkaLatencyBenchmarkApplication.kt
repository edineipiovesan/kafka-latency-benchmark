package com.github.edineipiovesan.kafkalatencybenchmark

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class KafkaLatencyBenchmarkApplication

fun main(args: Array<String>) {
    runApplication<KafkaLatencyBenchmarkApplication>(*args)
}
