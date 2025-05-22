package com.wob.poc.order.adapter.in.integration;

import com.wob.poc.order.adapter.out.mongo.OrderDocument;
import com.wob.poc.order.core.port.in.ProcessOrderCommand;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProcessOrderCommandMapper {

    List<ProcessOrderCommand> map(final List<OrderDocument> orderDocument);

}
