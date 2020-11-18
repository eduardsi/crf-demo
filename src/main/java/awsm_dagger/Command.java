package awsm_dagger;

public interface Command<R> {

    R execute();

}
