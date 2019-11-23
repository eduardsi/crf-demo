package awsm.base

import org.flywaydb.core.Flyway
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DefaultDSLContext
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.SimpleDriverDataSource
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.testcontainers.jdbc.ContainerDatabaseDriver
import spock.lang.Specification

import javax.sql.DataSource

@SpringBootTest(classes = IntegrationConfig)
class BaseIntegrationSpec extends Specification {

    @Configuration
    static class IntegrationConfig {

        @Bean
        PlatformTransactionManager transactionManager(DataSource dataSource) {
            new DataSourceTransactionManager(dataSource)
        }

        @Bean
        DSLContext dsl(DataSource dataSource) {
            DSL.using(dataSource, SQLDialect.POSTGRES)
        }

        @Bean
        DataSource dataSource() {
            def driver = new ContainerDatabaseDriver()
            new SimpleDriverDataSource(driver, "jdbc:tc:postgresql:11.5://any:any/any?TC_TMPFS=/testtmpfs:rw")
        }

    }

}