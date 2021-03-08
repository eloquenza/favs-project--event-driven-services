package edu.hsh.favs.project.escqrs.services.businessintelligenceservice;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class BusinessIntelligenceServiceApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(BusinessIntelligenceServiceApplication.class)
        .web(WebApplicationType.REACTIVE)
        .run(args);
  }
}
