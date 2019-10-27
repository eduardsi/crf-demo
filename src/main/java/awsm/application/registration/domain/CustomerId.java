package awsm.application.registration.domain;

import awsm.infrastructure.hashing.Hash;
import awsm.infrastructure.hashing.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public final class CustomerId extends Id<CustomerId> {

//  @JsonCreator
  public CustomerId(long id) {
    super(id);
  }

  public CustomerId(@JsonProperty("id") int id) {
    super(id);
  }

  CustomerId() {
    super();
  }

  private CustomerId(Hash<CustomerId> id) {
    super(id);
  }

  @Override
  @JsonValue
  public long asLong() {
    return super.asLong();
  }

  @Override
  protected Hash<CustomerId> hashOf(String hash) {
    return new Hash<>(hash) {
      @Override
      public CustomerId unhash() {
        return new CustomerId(this);
      }
    };
  }

}
