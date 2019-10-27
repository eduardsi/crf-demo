package awsm.infrastructure.hashing;

import javax.annotation.Nonnull;

public abstract class HashId<I extends Id>  {

  private final String hashId;

  protected HashId(String hashId) {
    this.hashId = hashId;
  }

  protected abstract I newInstance(long id);

  public I unhash() {
    var decode = HashidsHolder.get().decode(hashId);
    return newInstance(decode[0]);
  }

  @Override
  @Nonnull
  public String toString() {
    return this.hashId;
  }
}
