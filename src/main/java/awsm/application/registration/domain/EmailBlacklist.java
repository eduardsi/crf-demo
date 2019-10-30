package awsm.application.registration.domain;

interface EmailBlacklist {

  boolean allows(String email);

}
