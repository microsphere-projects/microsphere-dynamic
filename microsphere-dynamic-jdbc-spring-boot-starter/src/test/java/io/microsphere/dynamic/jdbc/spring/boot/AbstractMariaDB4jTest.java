/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.microsphere.dynamic.jdbc.spring.boot;

import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * TODO
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see TODO
 * @since 1.0.0
 */
public class AbstractMariaDB4jTest {

    @Test
    public void test() throws Exception {
        DBConfigurationBuilder configBuilder = DBConfigurationBuilder.newBuilder();
        configBuilder.setPort(3306);
        DB db = DB.newEmbeddedDB(configBuilder.build());
        db.start();
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test", "root", "");

    }
}
