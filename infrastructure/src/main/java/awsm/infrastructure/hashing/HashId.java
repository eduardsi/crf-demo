package awsm.infrastructure.hashing;

import javax.annotation.Nonnull;

public abstract class HashId<I extends Id>  {

  private final String hashId;

  protected HashId(String hashId) {
    this.hashId = hashId;
  }

  protected abstract I idInstance(long id);

  public I idInstance() {
    var decode = HashidsHolder.get().decode(hashId);
    return idInstance(decode[0]);
  }

  @Override
  @Nonnull
  public String toString() {
    return this.hashId;
  }
}
