package awsm.domain.banking;

interface UnsatisfiedObligations {

  UnsatisfiedObligations NONE = () -> false;

  boolean exist();

}
