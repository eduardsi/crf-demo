package awsm.application.banking.impl;

interface UnsatisfiedObligations {

  UnsatisfiedObligations NONE = () -> false;

  boolean exist();

}
