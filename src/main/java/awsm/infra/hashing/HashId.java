package awsm.infra.hashing;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;

public class HashId implements CharSequence {

  private final String hashed;

  public HashId(Long raw) {
    var hashids = HashidsHolder.get();
    this.hashed = hashids.encode(requireNonNull(raw, "Cannot hash null value"));
  }

  @Override
  public int length() {
    return this.hashed.length();
  }

  @Override
  public char charAt(int index) {
    return this.hashed.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return this.hashed.subSequence(start, end);
  }

  @Nonnull
  @Override
  public String toString() {
    return this.hashed;
  }
}
