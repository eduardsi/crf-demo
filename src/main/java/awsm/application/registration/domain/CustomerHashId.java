package awsm.application.registration.domain;

import awsm.infrastructure.hashing.HashId;

public class CustomerHashId extends HashId<CustomerId> {

  public CustomerHashId(String hashId) {
    super(hashId);
  }

  @Override
  protected CustomerId newInstance(long id) {
    return new CustomerId(id);
  }
}
