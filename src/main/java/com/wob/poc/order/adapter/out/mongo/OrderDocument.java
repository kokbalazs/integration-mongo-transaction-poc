package com.wob.poc.order.adapter.out.mongo;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document(collection = "order")
public class OrderDocument {
    @Id
    private String id;
    private String orderId;
    private String orderStatus;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant lastModifiedAt;
    @Version
    private Long version;

}
