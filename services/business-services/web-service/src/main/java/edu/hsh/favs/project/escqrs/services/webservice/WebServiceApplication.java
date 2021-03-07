package edu.hsh.favs.project.escqrs.services.webservice;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class WebServiceApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(WebServiceApplication.class)
        .web(WebApplicationType.REACTIVE)
        .run(args);
  }

  @Configuration
  @EnableBinding(Source.class)
  static class StreamConfig {}
}
