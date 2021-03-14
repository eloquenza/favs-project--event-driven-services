package edu.hsh.favs.project.escqrs.services.productcommandservice.config;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

/** This class configures the PostgreSQL database access through R2DBC */
@Configuration
@EnableR2dbcRepositories(
    basePackages = "edu.hsh.favs.project.escqrs.services.productcommandservice")
@Profile({"docker", "development"})
public class DataSourceConfiguration extends AbstractR2dbcConfiguration {

  @Value("${postgres.host}")
  private String postgresHost;

  @Value("${postgres.port}")
  private Integer postgresPort;

  @Value("${postgres.database-name}")
  private String databaseName;

  @Value("${spring.application.name}")
  private String applicationName;

  private DataSourceProperties dataSourceProperties;

  DataSourceConfiguration(DataSourceProperties dataSourceProperties) {
    this.dataSourceProperties = dataSourceProperties;
  }

  @Bean
  @Override
  public ConnectionFactory connectionFactory() {
    return getPostgresqlConnectionFactory();
  }

  @NotNull
  private ConnectionFactory getPostgresqlConnectionFactory() {
    return new PostgresqlConnectionFactory(
        PostgresqlConnectionConfiguration.builder()
            .applicationName(applicationName)
            .database(databaseName)
            .host(postgresHost)
            .port(postgresPort)
            .username(dataSourceProperties.getDataUsername())
            .password(dataSourceProperties.getDataPassword())
            .build());
  }

  @Bean
  public TransactionalOperator transactionalOperator() {
    return TransactionalOperator.create(new R2dbcTransactionManager(this.connectionFactory()));
  }

  @Bean
  public R2dbcEntityTemplate r2dbcEntityTemplate() {
    return new R2dbcEntityTemplate(this.connectionFactory());
  }

  @Bean
  @ConfigurationProperties("spring.datasource")
  @LiquibaseDataSource
  public DataSource dataSource(DataSourceProperties properties) {
    return new SimpleDriverDataSource(
        new org.postgresql.Driver(),
        properties.getUrl(),
        properties.getDataUsername(),
        properties.getDataPassword());
  }
}
