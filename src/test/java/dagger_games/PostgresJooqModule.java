package dagger_games;

import dagger.Module;
import dagger.Provides;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.ThreadLocalTransactionProvider;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.inject.Singleton;

@Module
public class PostgresJooqModule {

    @Provides
    @Singleton
    static DSLContext providesDSLContext() {
        var dataSource = new DriverManagerDataSource("jdbc:postgresql://some.real.ip/app");
        var txProvider = new ThreadLocalTransactionProvider(new DataSourceConnectionProvider(dataSource));
        var dsl = DSL.using(dataSource, SQLDialect.POSTGRES);
        dsl.configuration().set(txProvider);
        return dsl;
    }

}
