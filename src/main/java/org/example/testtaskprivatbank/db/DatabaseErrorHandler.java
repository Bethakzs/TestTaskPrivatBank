package org.example.testtaskprivatbank.db;

import lombok.RequiredArgsConstructor;
import org.example.testtaskprivatbank.service.impl.TaskServiceImpl;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class DatabaseErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final RoutingDataSource routingDataSource;
    private final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean;
    private final PlatformTransactionManager transactionManager;

    @ExceptionHandler({PSQLException.class, SQLException.class})
    public void handleDatabaseException(Exception ex, WebRequest request) {
        System.err.println("Error connecting to the database. Switching to PostgreSQL.");
        logger.error("Error connecting to the database. Switching to PostgreSQL.");
//        switchToPostgreSQL();
        switchToH2();
    }

    private void switchToPostgreSQL() {
        routingDataSource.switchToSecondary();

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        entityManagerFactoryBean.setDataSource(routingDataSource);
        entityManagerFactoryBean.setJpaPropertyMap(properties);
        entityManagerFactoryBean.afterPropertiesSet();

        ((JpaTransactionManager) transactionManager).setEntityManagerFactory(entityManagerFactoryBean.getObject());
        logger.info("Switched to PostgreSQL.");
    }

    private void switchToH2() {
        routingDataSource.switchToSecondary();

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

        entityManagerFactoryBean.setDataSource(routingDataSource);
        entityManagerFactoryBean.setJpaPropertyMap(properties);
        entityManagerFactoryBean.afterPropertiesSet();

        ((JpaTransactionManager) transactionManager).setEntityManagerFactory(entityManagerFactoryBean.getObject());
        logger.info("Switched to H2.");
    }
}
