package edu.hsh.favs.project.escqrs.services.businessintelligenceservice;

import edu.hsh.favs.project.escqrs.services.businessintelligenceservice.processing.JsonToCustomerCreatedEventConverter;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MessageConverter;

@SpringBootApplication
public class BusinessIntelligenceServiceApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(BusinessIntelligenceServiceApplication.class)
        .web(WebApplicationType.REACTIVE)
        .run(args);
  }

  @Bean
  public MessageConverter customMessageConverter() {
    return new JsonToCustomerCreatedEventConverter();
  }
}
