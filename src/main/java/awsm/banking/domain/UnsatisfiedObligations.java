package awsm.banking.domain;

public interface UnsatisfiedObligations {

  UnsatisfiedObligations NONE = () -> false;

  boolean exist();

}
