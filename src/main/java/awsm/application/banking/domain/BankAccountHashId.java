package awsm.application.banking.domain;

import awsm.infrastructure.hashing.HashId;

class BankAccountHashId extends HashId<BankAccountId> {

  BankAccountHashId(String hashId) {
    super(hashId);
  }

  @Override
  protected BankAccountId idInstance(long id) {
    return new BankAccountId(id);
  }
}
