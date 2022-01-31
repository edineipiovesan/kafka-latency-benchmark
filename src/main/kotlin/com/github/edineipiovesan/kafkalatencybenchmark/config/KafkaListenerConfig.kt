package com.github.edineipiovesan.kafkalatencybenchmark.config

import com.github.edineipiovesan.messages.MyAvroEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties


@Configuration
class KafkaListenerConfig(private val kafkaProperties: KafkaProperties) {

    @Bean
    fun consumerFactory(): ConsumerFactory<String, MyAvroEvent> {
        val configs = kafkaProperties.buildConsumerProperties()
        return DefaultKafkaConsumerFactory(configs)
    }

    @Bean
    fun batchContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, MyAvroEvent> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, MyAvroEvent>()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
        factory.consumerFactory = consumerFactory()
        factory.isBatchListener = true

        return factory
    }
}