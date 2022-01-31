package com.github.edineipiovesan.kafkalatencybenchmark.listener

import com.github.edineipiovesan.messages.MyAvroEvent
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.random.Random

@Component
class KafkaListener : BatchAcknowledgingMessageListener<String, MyAvroEvent> {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(containerFactory = "batchContainerFactory", topics = ["benchmark-topic"])
    override fun onMessage(data: List<ConsumerRecord<String, MyAvroEvent>>, acknowledgment: Acknowledgment?) {
        val earliestMessageTimestamp = data.minByOrNull { it.timestamp() }?.timestamp() ?: 0
        logger.info("Batch size=${data.size}; Delay=${Instant.now().toEpochMilli() - earliestMessageTimestamp}")
        Thread.sleep(Random.nextLong(0, 100))
        acknowledgment?.acknowledge()
    }

}