package com.wob.poc.order.core.port.in;

public record ProcessOrderCommand(
        String orderId,
        String orderStatus
) {
}
