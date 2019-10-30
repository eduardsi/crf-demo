package awsm.crm;

interface EmailBlacklist {

  boolean allows(Email email);

}
