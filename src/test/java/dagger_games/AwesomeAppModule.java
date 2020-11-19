package dagger_games;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dagger.Module;
import dagger.Provides;
import io.javalin.Javalin;
import org.jasypt.util.text.StrongTextEncryptor;
import org.jasypt.util.text.TextEncryptor;

import javax.inject.Singleton;

@Module
class AwesomeAppModule {

    @Provides
    @Singleton
    Config provideConfig() {
        return ConfigFactory.load();
    }

    @Provides
    @Singleton
    TextEncryptor provideTextEncryptor(Config config) {
        var encryptor = new StrongTextEncryptor();
        var encryptionPassword = config.getString("encryptionPassword");
        encryptor.setPassword(encryptionPassword);
        return encryptor;
    }

    @Provides
    @Singleton
    Javalin provideApp(RegisterNowFactory registerNow, TransactionalFactory transactional) {
        Javalin app = Javalin.create();
        app.post("/registrations", web -> {
            var cmd = registerNow
                    .create(
                            web.formParam("firstName"),
                            web.formParam("lastName"),
                            web.formParam("personalId"),
                            web.formParam("email"));
            web.json(
                    transactional.create(cmd).execute()
            );
        });
        return app;
    }


}