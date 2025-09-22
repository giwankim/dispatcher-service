package com.polarbookshop.dispatcherservice

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import java.util.function.Function

private val logger = KotlinLogging.logger {}

@Configuration
class DispatchingFunctions {
    @Bean
    fun pack(): Function<OrderAcceptedMessage, Long> =
        Function { orderAcceptedMessage ->
            orderAcceptedMessage.orderId
                .also {
                    logger.info { "The order with id $it is packed." }
                }
        }

    @Bean
    fun label(): Function<Flux<Long>, Flux<OrderDispatchedMessage>> =
        Function { orderIds ->
            orderIds
                .doOnNext { logger.info { "The order with id $it is label." } }
                .map(::OrderDispatchedMessage)
        }
}
