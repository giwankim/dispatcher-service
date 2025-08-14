package com.polarbookshop.dispatcherservice;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.TestConstructor;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
record FunctionsStreamIT(
    InputDestination input, OutputDestination output, ObjectMapper objectMapper) {

  @Test
  void orderAcceptedThenDispatched() throws IOException {
    long orderId = 121L;
    Message<OrderAcceptedMessage> inputMessage =
        MessageBuilder.withPayload(new OrderAcceptedMessage(orderId)).build();
    Message<OrderDispatchedMessage> expectedOutputMessage =
        MessageBuilder.withPayload(new OrderDispatchedMessage(orderId)).build();

    input.send(inputMessage);

    OrderDispatchedMessage outputMessage = objectMapper.readValue(
        output.receive().getPayload(), OrderDispatchedMessage.class);

    assertThat(outputMessage).isEqualTo(expectedOutputMessage.getPayload());
  }
}
