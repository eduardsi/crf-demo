package awsm.infrastructure.hashing;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Optional;

public abstract class Id<T> {

  private final Optional<Long> id;

  public Id(HashId<T> hashId) {
    var decode = HashidsHolder.get().decode(hashId.toString());
    this.id = Optional.of(decode[0]);
  }

  public Id(long id) {
    this.id = Optional.of(id);
  }

  public Id() {
    this.id = Optional.empty();
  }

  public String hashIdString() {
    return hashId().toString();
  }

  private HashId<T> hashId() {
    return hashId(
        HashidsHolder.get().encode(id.orElseThrow())
    );
  }

  protected abstract HashId<T> hashId(String hashId);

  @JsonValue
  public long asLong() {
    return id.orElseThrow();
  }

}
