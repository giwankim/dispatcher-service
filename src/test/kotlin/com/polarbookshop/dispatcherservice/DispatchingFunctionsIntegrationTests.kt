package com.polarbookshop.dispatcherservice

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.OutputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.integration.support.MessageBuilder

@SpringBootTest
@Import(TestChannelBinderConfiguration::class)
class DispatchingFunctionsIntegrationTests(
    val input: InputDestination,
    val output: OutputDestination,
    val objectMapper: ObjectMapper,
) {
    @Test
    fun `when order accepted then dispatched`() {
        val orderId = 121L
        val inputMessage = MessageBuilder.withPayload(OrderAcceptedMessage(orderId)).build()

        input.send(inputMessage)

        val receivedMessage =
            objectMapper.readValue(
                output.receive().payload,
                OrderDispatchedMessage::class.java,
            )
        assertThat(receivedMessage).isEqualTo(OrderDispatchedMessage(orderId))
    }
}
