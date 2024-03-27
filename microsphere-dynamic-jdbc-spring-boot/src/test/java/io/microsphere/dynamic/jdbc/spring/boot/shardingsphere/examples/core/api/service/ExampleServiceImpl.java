package io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.examples.core.api.service;

import java.sql.SQLException;

import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class ExampleServiceImpl implements ExampleService {

    @Override
    public void initEnvironment() throws SQLException {

    }

    @Override
    public void cleanEnvironment() throws SQLException {

    }

    @Override
    @Transactional
    public void processSuccess() throws SQLException {

    }

    @Override
    public void processFailure() throws SQLException {

    }

    @Override
    public void printData() throws SQLException {

    }
}
