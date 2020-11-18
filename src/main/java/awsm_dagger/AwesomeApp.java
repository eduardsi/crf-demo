package awsm_dagger;

public class AwesomeApp {

    public static void main(String[] args) {
        DaggerAwesomeAppComponent.builder().build().app().start();
    }

}
