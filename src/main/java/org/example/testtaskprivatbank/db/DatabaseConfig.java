package org.example.testtaskprivatbank.db;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableRetry // Дозволяє використання анотацій Retry у Spring
@EnableTransactionManagement // Вмикає управління транзакціями у Spring
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String mainDbUrl;

    @Value("${spring.datasource.username}")
    private String mainDbUsername;

    @Value("${spring.datasource.password}")
    private String mainDbPassword;

    @Value("${spring.second-datasource.url}")
    private String secondaryDbUrl;

    @Value("${spring.second-datasource.username}")
    private String secondaryDbUsername;

    @Value("${spring.second-datasource.password}")
    private String secondaryDbPassword;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    @Primary
    @Retryable(include = { DataAccessResourceFailureException.class, GenericJDBCException.class }, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public DataSource mainDataSource() {
        logger.info("Connecting to main database with URL: {}", mainDbUrl);
        return DataSourceBuilder.create()
                .url(mainDbUrl)
                .username(mainDbUsername)
                .password(mainDbPassword)
                .build();
    }

    @Bean
    public DataSource secondaryDataSource() {
        logger.info("Connecting to secondary database with URL: {}", secondaryDbUrl);
        return DataSourceBuilder.create()
                .url(secondaryDbUrl)
                .username(secondaryDbUsername)
                .password(secondaryDbPassword)
                .build();
    }

    @Bean(name = "mainDatabaseJdbcTemplate")
    public JdbcTemplate mainDatabaseJdbcTemplate(@Qualifier("mainDataSource") DataSource mainDataSource) {
        return new JdbcTemplate(mainDataSource);
    }

    @Bean(name = "reservedDatabaseJdbcTemplate")
    public JdbcTemplate reservedDatabaseJdbcTemplate(@Qualifier("secondaryDataSource") DataSource secondaryDataSource) {
        return new JdbcTemplate(secondaryDataSource);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("mainDataSource") DataSource mainDataSource) {
        logger.info("Configuring EntityManagerFactory with main database (PostgreSQL).");
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(mainDataSource);
        em.setPackagesToScan("org.example.testtaskprivatbank");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", PostgreSQLDialect.class.getName());
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        em.setJpaProperties(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory mainEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(mainEntityManagerFactory);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}

