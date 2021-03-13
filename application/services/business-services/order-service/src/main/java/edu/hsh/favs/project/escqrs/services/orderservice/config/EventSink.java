package edu.hsh.favs.project.escqrs.services.orderservice.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface EventSink {
  final String CUSTOMER_INPUT = "customer";
  final String PRODUCT_INPUT = "product";

  @Input(EventSink.CUSTOMER_INPUT)
  SubscribableChannel customer();

  @Input(EventSink.PRODUCT_INPUT)
  SubscribableChannel product();
}
