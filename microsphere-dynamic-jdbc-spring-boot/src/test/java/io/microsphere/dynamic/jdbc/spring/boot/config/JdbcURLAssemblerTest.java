package io.microsphere.dynamic.jdbc.spring.boot.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import io.microsphere.dynamic.jdbc.spring.boot.datasource.config.JdbcURLAssembler;

/**
 * {@link JdbcURLAssembler} Test
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {JdbcURLAssembler.class, JdbcURLAssemblerTest.class})
@TestPropertySource(locations = {"classpath:/META-INF/dynamic-jdbc/default.properties"})
public class JdbcURLAssemblerTest {

    @Autowired
    private JdbcURLAssembler jdbcURLAssembler;

    @Test
    public void testAssemble() {
        String rawJdbcURL = "jdbc:mysql://127.0.0.1:3306/demo_ds";
        String jdbcURL = jdbcURLAssembler.assemble(rawJdbcURL);
        assertEquals("jdbc:mysql://127.0.0.1:3306/demo_ds?useSSL=false&useUnicode=true&characterEncoding=utf-8", jdbcURL);
    }

    @Test
    public void testAssembleWithoutScheme() {
        String rawJdbcURL = "127.0.0.1:3306/demo_ds?characterEncoding=UTF-8&ttl=false&useUnicode=false";
        String jdbcURL = jdbcURLAssembler.assemble(rawJdbcURL);
        assertEquals("jdbc:mysql://127.0.0.1:3306/demo_ds?characterEncoding=UTF-8&ttl=false&useUnicode=false&useSSL=false",
                jdbcURL);
    }
}
