package awsm.crm;

import awsm.infrastructure.hashing.Id;

public class CustomerId extends Id<CustomerId, CustomerHashId> {

  CustomerId(long id) {
    super(id);
  }

  CustomerId() {
    super();
  }

  @Override
  protected CustomerHashId hashIdInstance(String hashId) {
    return new CustomerHashId(hashId);
  }

}
