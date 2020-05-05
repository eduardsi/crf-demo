package awsm.domain.banking.customer.blacklist;

import awsm.domain.banking.customer.Email;

public interface Blacklist {

  boolean permits(Email email);

}
