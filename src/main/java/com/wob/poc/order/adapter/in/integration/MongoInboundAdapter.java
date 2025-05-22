package com.wob.poc.order.adapter.in.integration;

import com.wob.poc.order.adapter.out.mongo.OrderDocument;
import com.wob.poc.order.core.port.in.ProcessOrderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.mongodb.inbound.MongoDbMessageSource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
final class MongoInboundAdapter {

    private final MongoTemplate mongoDatabaseFactory;
    private final ProcessOrderUseCase processOrderUseCase;
    private final ProcessOrderCommandMapper processOrderCommandMapper;

    @Bean
    public IntegrationFlow mongoInboundFlow() {
        return IntegrationFlow.from(
                        mongoMessageSource(),
                        p -> p.poller(Pollers.fixedDelay(1000)
                                .maxMessagesPerPoll(15)
                                .advice(new ClaimingMessageSourceAdvice(mongoDatabaseFactory))
                                .transactional()
                        ))
                .log()
                .handleReactive(
                        m -> {
                            log.debug("Handling order message from Mongo: {}", m);
                            final var processOrderCommand = processOrderCommandMapper.map((List<OrderDocument>) m.getPayload());
                            return processOrderUseCase.processOrder(processOrderCommand)
                                    .doOnError(e -> log.error("Error processing order: {}", processOrderCommand, e))
                                    .then();
                        }
                );
    }


    public MongoDbMessageSource mongoMessageSource() {
        final var source = new MongoDbMessageSource(mongoDatabaseFactory, new LiteralExpression("{'orderStatus': 'NEW'}"));
        source.setEntityClass(OrderDocument.class);
        source.setCollectionNameExpression(new LiteralExpression("order"));
        source.afterPropertiesSet();
        return source;
    }

}
