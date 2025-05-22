package com.wob.poc.order.core;

import com.wob.poc.order.core.port.in.ProcessOrderCommand;
import com.wob.poc.order.core.port.in.ProcessOrderUseCase;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
final class DocumentProcessorService implements ProcessOrderUseCase {

    @Override
    public Mono<Void> processOrder(@NotNull final List<ProcessOrderCommand> processOrderCommand) {
        log.debug("Processing order: {}", processOrderCommand);
        return Mono.empty();
    }

}
