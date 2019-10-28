package awsm.infrastructure.hashing;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Optional;

public abstract class Id<T extends Id, H extends HashId<T>> {

  private final Optional<Long> id;

  public Id(long id) {
    this.id = Optional.of(id);
  }

  public Id() {
    this.id = Optional.empty();
  }

  public String hashIdString() {
    return hashId().toString();
  }

  private H hashId() {
    return hashIdInstance(
        HashidsHolder.get().encode(id.orElseThrow())
    );
  }

  protected abstract H hashIdInstance(String hashId);

  @JsonValue
  public long asLong() {
    return id.orElseThrow();
  }

}
