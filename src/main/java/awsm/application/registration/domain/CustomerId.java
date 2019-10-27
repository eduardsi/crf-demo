package awsm.application.registration.domain;

import awsm.infrastructure.hashing.Id;

public class CustomerId extends Id<CustomerId, CustomerHashId> {

  CustomerId(long id) {
    super(id);
  }

  CustomerId() {
    super();
  }

  @Override
  protected CustomerHashId hash(String hashId) {
    return new CustomerHashId(hashId);
  }

}
