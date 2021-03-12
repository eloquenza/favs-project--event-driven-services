package edu.hsh.favs.project.escqrs.services.businessintelligenceservice;

import edu.hsh.favs.project.escqrs.services.commons.eventprocessing.KafkaRebalanceListener;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.schema.client.EnableSchemaRegistryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableSchemaRegistryClient
public class BusinessIntelligenceServiceApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(BusinessIntelligenceServiceApplication.class)
        .web(WebApplicationType.REACTIVE)
        .run(args);
  }

  @Bean
  public KafkaRebalanceListener kafkaRebalanceListener() {
    return new KafkaRebalanceListener();
  }
}
