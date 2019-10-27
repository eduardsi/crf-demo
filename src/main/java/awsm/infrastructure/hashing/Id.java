package awsm.infrastructure.hashing;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Optional;

public abstract class Id<T> {

  private final Optional<Long> id;

  public Id(Hash<T> id) {
    var decode = HashidsHolder.get().decode(id.toString());
    this.id = Optional.of(decode[0]);
  }

  public Id(long id) {
    this.id = Optional.of(id);
  }

  public Id() {
    this.id = Optional.empty();
  }

  public Hash<T> hash() {
    var hashids = HashidsHolder.get();
    var hash = hashids.encode(id.orElseThrow());
    return hashOf(hash);
  }

  protected abstract Hash<T> hashOf(String hash);

  @JsonValue
  public long asLong() {
    return id.orElseThrow();
  }

}
