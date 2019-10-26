package awsm.application.registration.domain;

interface EmailBlacklist {

  EmailBlacklist ALWAYS_ALLOWS = email -> true;

  boolean allows(String email);

}
