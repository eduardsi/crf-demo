package awsm_dagger;

import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.Pipelinr;
import awsm.application.RegisterNow;
import awsm.application.RegisterNowFactory;
import awsm.domain.crm.Uniqueness;
import dagger.Module;
import dagger.Provides;
import io.javalin.Javalin;
import org.jasypt.util.text.BasicTextEncryptor;
import org.jasypt.util.text.TextEncryptor;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import static jooq.Tables.CUSTOMER;

@Module
class AwesomeAppModule {

    @Provides
    Pipeline providePipeline() {
        return new Pipelinr();
    }

    @Provides
    DSLContext providesDSLContext() {
        var ds = new DriverManagerDataSource("jdbc:h2:mem:test");
        return DSL.using(ds, SQLDialect.POSTGRES);
    }

    @Provides
    Uniqueness providesUniqueness(DSLContext dsl) {
        return email -> dsl.fetchExists(dsl.selectFrom(CUSTOMER).where(CUSTOMER.EMAIL.eq(email)));
    }

    @Provides
    TextEncryptor provideTextEncryptor() {
        return new BasicTextEncryptor();
    }

    @Provides
    Javalin provideApp(RegisterNowFactory registerNow, DSLContext dsl) {
        Javalin app = Javalin.create();
        app.post("/registrations", web -> {
            var cmd = registerNow
                    .create(
                            web.formParam("firstName"),
                            web.formParam("lastName"),
                            web.formParam("personalId"),
                            web.formParam("email"));
            web.json(
                    new Transactional<>(dsl, cmd).execute()
            );
        });
        return app;
    }


}