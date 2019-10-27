package awsm.application.registration.domain;

import awsm.infrastructure.hashing.HashId;
import awsm.infrastructure.hashing.Id;

public class CustomerId extends Id<CustomerId> {

  CustomerId(long id) {
    super(id);
  }

  CustomerId() {
    super();
  }

  private CustomerId(HashId<CustomerId> hashId) {
    super(hashId);
  }

  @Override
  protected HashId<CustomerId> hashId(String hashId) {
    return new HashId<>(hashId) {
      @Override
      public CustomerId unhash() {
        return new CustomerId(this);
      }
    };
  }

}
