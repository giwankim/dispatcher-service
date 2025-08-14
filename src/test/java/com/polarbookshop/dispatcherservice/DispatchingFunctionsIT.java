package com.polarbookshop.dispatcherservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import org.springframework.test.context.TestConstructor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@FunctionalSpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
record DispatchingFunctionsIT(FunctionCatalog catalog) {

  @Test
  void pack() {
    Function<OrderAcceptedMessage, Long> pack = catalog.lookup(Function.class, "pack");
    long orderId = 121L;
    assertThat(pack.apply(new OrderAcceptedMessage(orderId))).isEqualTo(orderId);
  }

  @Test
  void label() {
    Function<Flux<Long>, Flux<OrderDispatchedMessage>> label =
        catalog.lookup(Function.class, "label");
    Flux<Long> orderId = Flux.just(121L);

    StepVerifier.create(label.apply(orderId))
        .expectNextMatches(
            dispatchedMessage -> dispatchedMessage.equals(new OrderDispatchedMessage(121L)))
        .verifyComplete();
  }

  @Test
  void packAndLabel() {
    Function<OrderAcceptedMessage, Flux<OrderDispatchedMessage>> packAndLabel =
        catalog.lookup(Function.class, "pack|label");
    long orderId = 121L;

    StepVerifier.create(packAndLabel.apply(new OrderAcceptedMessage(orderId)))
        .expectNextMatches(
            dispatchedMessage -> dispatchedMessage.equals(new OrderDispatchedMessage(121L)))
        .verifyComplete();
  }
}
