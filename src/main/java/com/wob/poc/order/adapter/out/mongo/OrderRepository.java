package com.wob.poc.order.adapter.out.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface OrderRepository extends ReactiveMongoRepository<OrderDocument, String> {



}
