package awsm_dagger;

import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.Pipelinr;
import awsm.application.RegistrationFacade;
import awsm.domain.crm.Uniqueness;
import dagger.Module;
import dagger.Provides;
import io.javalin.Javalin;
import org.jasypt.util.text.BasicTextEncryptor;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import static jooq.Tables.CUSTOMER;

@Module
class AwesomeAppModule {

    @Provides
    static Pipeline providePipeline() {
        return new Pipelinr();
    }

    @Provides
    static DSLContext providesDSLContext() {
        var ds = new DriverManagerDataSource("jdbc:h2:mem:test");
        return DSL.using(ds, SQLDialect.POSTGRES);
    }

    @Provides
    static Uniqueness providesUniqueness(DSLContext dsl) {
        return email -> dsl.fetchExists(dsl.selectFrom(CUSTOMER).where(CUSTOMER.EMAIL.eq(email)));
    }

    @Provides
    static Javalin provideApp(Uniqueness uniqueness) {
        Javalin app = Javalin.create();
        app.post("/registrations", web -> {
            var encryptor = new BasicTextEncryptor();
            web.json(
                    new RegistrationFacade(uniqueness, encryptor)
                            .register(
                                    web.formParam("firstName"),
                                    web.formParam("lastName"),
                                    web.formParam("personalId"),
                                    web.formParam("email"))
            );
        });
        return app;
    }

}