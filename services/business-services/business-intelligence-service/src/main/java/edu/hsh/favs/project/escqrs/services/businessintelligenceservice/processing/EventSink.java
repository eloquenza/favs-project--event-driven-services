package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.processing;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface EventSink {
  final String CUSTOMER_INPUT = "customer";
  final String ORDER_INPUT = "order";

  @Input(EventSink.CUSTOMER_INPUT)
  SubscribableChannel customer();

  @Input(EventSink.ORDER_INPUT)
  SubscribableChannel order();
}
