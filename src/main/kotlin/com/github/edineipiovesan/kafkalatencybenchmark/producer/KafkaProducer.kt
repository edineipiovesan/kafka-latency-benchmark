package com.github.edineipiovesan.kafkalatencybenchmark.producer

import com.github.edineipiovesan.messages.MyAvroEvent
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter.ISO_INSTANT
import java.time.temporal.ChronoUnit.SECONDS
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Component
class KafkaProducer(private val kafkaTemplate: KafkaTemplate<String, MyAvroEvent>) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val productionRate = ConcurrentHashMap<String, Long>()

    /***
     * Calculate fixedRate value dividing 1000 by desired TPS.
     * eg:
     * - 50 TPS -> 1000/50=20 -> fixedRate=20
     * - 100 TPS -> 1000/100=10 -> fixedRate=10
     * - 120 TPS -> 1000/120=8,33 -> fixedRate=8
     */
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MILLISECONDS)
    private fun generateMessages() {
        val id = UUID.randomUUID().toString()
        val message = MyAvroEvent.newBuilder()
            .setId(id)
            .setMessage("This message was produced at ${OffsetDateTime.now()} with id $id")
            .build()
        val producerRecord = ProducerRecord("benchmark-topic", id, message)
        kafkaTemplate.send(producerRecord)
            .addCallback({
                val now = now()
                var counter = productionRate[now] ?: 0
                productionRate[now] = ++counter
            }, {
                logger.error("An error occurred while producing message; " +
                        "key=$id; id=${message.id}; message=${message.message}", it)
            })
    }

    /**
     * Print statistics on console with 3 seconds delay
     * ensuring all producer data was flushed
     * - Produced amount by time
     */
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    private fun printStatistics() {
        val now = now(minusSeconds = 3)
        val producedMessages = productionRate[now] ?: 0

        logger.info("[Production statistics]:\t$producedMessages produced at $now")
    }

    /**
     * Print statistics on console before application stops.
     * - Produced amount
     * - Start time
     * - End time
     */
    @EventListener
    fun printAllProducedMessages(contextClosedEvent: ContextClosedEvent) {
        val amount = productionRate.values.reduce { acc, current -> acc+current }
        val started = productionRate.keys.first()
        val ended = productionRate.keys.last()

        logger.info("[Final production statistics]:\t $amount was produced between $started and $ended")
    }

    private fun now(minusSeconds: Long = 0): String {
        return OffsetDateTime.now().minusSeconds(minusSeconds).truncatedTo(SECONDS).format(ISO_INSTANT).toString()
    }
}