package awsm.application.registration.domain;

interface EmailBlacklist {

  boolean allows(Email email);

}
