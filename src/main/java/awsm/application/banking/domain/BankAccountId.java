package awsm.application.banking.domain;

import awsm.infrastructure.hashing.Id;

class BankAccountId extends Id<BankAccountId, BankAccountHashId> {

  BankAccountId(long id) {
    super(id);
  }

  BankAccountId() {
  }

  @Override
  protected BankAccountHashId hashIdInstance(String hashId) {
    return new BankAccountHashId(hashId);
  }
}
