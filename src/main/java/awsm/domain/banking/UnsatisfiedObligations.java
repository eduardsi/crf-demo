package awsm.domain.banking;

public interface UnsatisfiedObligations {

  UnsatisfiedObligations NONE = () -> false;

  boolean exist();

}
