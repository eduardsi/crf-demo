package awsm_dagger;


import dagger.Component;
import io.javalin.Javalin;

@Component(modules = AwesomeAppModule.class)
public interface AwesomeAppComponent {

    Javalin app();

}
