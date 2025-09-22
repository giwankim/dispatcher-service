package com.polarbookshop.dispatcherservice

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.cloud.function.context.FunctionCatalog
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest
import org.springframework.messaging.support.GenericMessage
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.util.function.Function

@FunctionalSpringBootTest
class DispatchingFunctionsTest(
    val catalog: FunctionCatalog,
    val objectMapper: ObjectMapper,
) {
    @Test
    fun `pack order`() {
        val pack =
            catalog.lookup<Function<OrderAcceptedMessage, Long>>(Function::class.java, "pack")
        val orderId = 121L
        assertThat(pack.apply(OrderAcceptedMessage(orderId))).isEqualTo(orderId)
    }

    @Test
    fun `label order`() {
        val label =
            catalog.lookup<Function<Flux<Long>, Flux<OrderDispatchedMessage>>>(
                Function::class.java,
                "label",
            )
        val orderId = Flux.just(121L)

        StepVerifier
            .create(label.apply(orderId))
            .expectNext(OrderDispatchedMessage(121L))
            .verifyComplete()
    }

    @Test
    fun `pack and label order`() {
        val packAndLabel =
            catalog.lookup<Function<OrderAcceptedMessage, Flux<*>>>(
                Function::class.java,
                "pack|label",
            )
        val orderId = 121L

        StepVerifier
            .create(packAndLabel.apply(OrderAcceptedMessage(orderId)))
            .assertNext {
                val genericMessage = it as GenericMessage<*>
                val jsonBytes = genericMessage.payload as ByteArray
                val result =
                    objectMapper.readValue(jsonBytes, OrderDispatchedMessage::class.java)
                assertThat(result).isEqualTo(OrderDispatchedMessage(orderId))
            }.verifyComplete()
    }
}
