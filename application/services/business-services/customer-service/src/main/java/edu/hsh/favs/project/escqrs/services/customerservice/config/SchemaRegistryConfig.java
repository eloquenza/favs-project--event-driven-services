package edu.hsh.favs.project.escqrs.services.customerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cloud.stream.schema.client.ConfluentSchemaRegistryClient;
import org.springframework.cloud.stream.schema.client.SchemaRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchemaRegistryConfig {

  @Bean
  public SchemaRegistryClient schemaRegistryClient(
      @Value("${spring.cloud.stream.kafka.binder.producer-properties.schema.registry.url}")
          String endPoint) {
    ConfluentSchemaRegistryClient client = new ConfluentSchemaRegistryClient();
    client.setEndpoint(endPoint);
    return client;
  }

  @Bean
  @ConditionalOnMissingBean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager();
  }
}
