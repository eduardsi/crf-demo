package awsm.crm;

import awsm.infrastructure.hashing.HashId;

public class CustomerHashId extends HashId<CustomerId> {

  public CustomerHashId(String hashId) {
    super(hashId);
  }

  @Override
  protected CustomerId idInstance(long id) {
    return new CustomerId(id);
  }
}
