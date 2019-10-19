package awsm.infrastructure.hashing;

public class UnhashId {

  private final long unhashed;

  public UnhashId(String hashed) {
    var decode = HashidsHolder.get().decode(hashed);
    this.unhashed = decode[0];
  }

  public long asLong() {
    return unhashed;
  }

}
