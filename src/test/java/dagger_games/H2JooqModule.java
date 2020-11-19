package dagger_games;

import dagger.Module;
import dagger.Provides;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.ThreadLocalTransactionProvider;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.inject.Singleton;
import javax.sql.DataSource;

@Module
public class H2JooqModule {

    @Provides
    @Singleton
    DataSource providesDataSource() {
        return new DriverManagerDataSource("jdbc:h2:mem:~/test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
    }

    @Provides
    @Singleton
    DSLContext providesDSLContext(DataSource dataSource) {
        flywayMigrate(dataSource);
        var txProvider = new ThreadLocalTransactionProvider(new DataSourceConnectionProvider(dataSource));
        var dsl = DSL.using(dataSource, SQLDialect.H2);
        dsl.configuration().set(txProvider);
        return dsl;
    }

    private void flywayMigrate(DataSource dataSource) {
        var flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.migrate();
    }

}
