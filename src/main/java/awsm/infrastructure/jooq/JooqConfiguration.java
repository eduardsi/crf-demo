package awsm.infrastructure.jooq;

import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JooqConfiguration {

  @Bean
  DSLContext dsl(DataSource dataSource) {
    return DSL.using(dataSource, SQLDialect.POSTGRES);
  }
}
