package awsm.banking;

public interface UnsatisfiedObligations {

  UnsatisfiedObligations NONE = () -> false;

  boolean exist();

}
