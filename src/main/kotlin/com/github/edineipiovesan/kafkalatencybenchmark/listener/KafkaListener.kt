package com.github.edineipiovesan.kafkalatencybenchmark.listener

import com.github.edineipiovesan.messages.MyAvroEvent
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class KafkaListener {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(containerFactory = "batchContainerFactory", topics = ["benchmark-topic"])
    fun onMessage(
        data: List<ConsumerRecord<String, MyAvroEvent>>,
        acknowledgment: Acknowledgment?,
        consumer: Consumer<String, MyAvroEvent>
    ) {
        val now = Instant.now().toEpochMilli()
        val maxDelay = data
            .map { now - it.timestamp() }
            .toLongArray().maxOrNull()
        val metrics = consumer.metrics()
            .filter { "lag" in it.key.name() }
            .map { it.key.name() to it.value.metricValue() }

        logger.info(
            "Batch size=${data.size}; " +
                    "Partition=${data.firstOrNull()?.partition()}; " +
                    "Delay=${maxDelay}; " +
                    "Metrics=${metrics}"
        )
        Thread.sleep(100)

        acknowledgment?.acknowledge()
    }

}