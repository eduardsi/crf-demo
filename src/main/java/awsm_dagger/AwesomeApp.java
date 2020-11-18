package awsm_dagger;


import dagger.Component;
import io.javalin.Javalin;

@Component(modules = AwesomeAppModule.class)
public interface AwesomeApp {

    Javalin app();

    static void main(String[] args) {
        DaggerAwesomeApp
                .builder()
                .build()
                .app()
                .start();
    }

}
