package awsm_dagger;

import dagger.Module;
import dagger.Provides;
import io.javalin.Javalin;

@Module
class AwesomeAppModule {

  @Provides
  static Javalin provideApp() {
    Javalin app = Javalin.create();
    return app;
  }

}