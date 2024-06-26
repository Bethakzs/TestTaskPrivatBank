package org.example.testtaskprivatbank.db;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class DatabaseErrorHandler {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private LocalContainerEntityManagerFactoryBean entityManagerFactoryBean;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @ExceptionHandler(PSQLException.class)
    public void handlePSQLException(PSQLException ex, WebRequest request) {
        System.err.println("Помилка у підключенні до бази даних PostgreSQL. Перемикаюся на H2:");

        // Логіка для переключення на використання H2 бази даних
        switchToH2();

        ex.printStackTrace();
    }

    private void switchToH2() {
        DataSource secondaryDataSource = applicationContext.getBean("secondaryDataSource", DataSource.class);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

        entityManagerFactoryBean.setDataSource(secondaryDataSource);
        entityManagerFactoryBean.setJpaPropertyMap(properties);
        entityManagerFactoryBean.afterPropertiesSet();

        ((JpaTransactionManager) transactionManager).setEntityManagerFactory(entityManagerFactoryBean.getObject());
    }
}
