package org.example.testtaskprivatbank.db;

import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.jdbc.datasource.AbstractDataSource;

@Component
public class RoutingDataSource extends AbstractDataSource {

    private DataSource mainDataSource;
    private DataSource secondaryDataSource;
    private boolean useSecondary = false;

    public void setMainDataSource(DataSource mainDataSource) {
        this.mainDataSource = mainDataSource;
    }

    public void setSecondaryDataSource(DataSource secondaryDataSource) {
        this.secondaryDataSource = secondaryDataSource;
    }

    public void switchToSecondary() {
        this.useSecondary = true;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (useSecondary) {
            return secondaryDataSource.getConnection();
        } else {
            return mainDataSource.getConnection();
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        if (useSecondary) {
            return secondaryDataSource.getConnection(username, password);
        } else {
            return mainDataSource.getConnection(username, password);
        }
    }
}
