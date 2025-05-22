package com.wob.poc.order.core.port.in;

import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProcessOrderUseCase {

    Mono<Void> processOrder(@NotNull final List<ProcessOrderCommand> processOrderCommand);

}
