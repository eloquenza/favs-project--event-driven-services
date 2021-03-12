package edu.hsh.favs.project.escqrs.services.commons.eventprocessing;

import java.util.Collection;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.cloud.stream.binder.kafka.KafkaBindingRebalanceListener;

/*
 * https://docs.spring.io/spring-cloud-stream-binder-kafka/docs/3.0.10.RELEASE/reference/html/spring-cloud-stream-binder-kafka.html#rebalance-listener
 * */
public class KafkaRebalanceListener implements KafkaBindingRebalanceListener {

  /**
   * Invoked when partitions are initially assigned or after a rebalance. Applications might only
   * want to perform seek operations on an initial assignment.
   *
   * @param bindingName the name of the binding.
   * @param consumer the consumer.
   * @param partitions the partitions.
   * @param initial true if this is the initial assignment.
   */
  public void onPartitionsAssigned(
      String bindingName,
      Consumer<?, ?> consumer,
      Collection<TopicPartition> partitions,
      boolean initial) {
    if (initial) {
      consumer.seekToBeginning(partitions);
      consumer.resume(partitions);
    }
  }
}
