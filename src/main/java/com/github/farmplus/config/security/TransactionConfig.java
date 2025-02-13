package com.github.farmplus.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import javax.sql.DataSource;

//@Configuration
//public class TransactionConfig {
//
//        private final DataSource dataSource;
//
//        public TransactionConfig(DataSource dataSource) {
//            this.dataSource = dataSource;
//        }
//
//        @Bean
//        public DataSourceTransactionManager transactionManager() {
//            return new DataSourceTransactionManager(dataSource);
//        }
//    }