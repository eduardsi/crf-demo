package awsm.infrastructure.hashing;

import javax.annotation.Nonnull;

public abstract class Hash<T> implements CharSequence {

  private final String hash;

  protected Hash(String hash) {
    this.hash = hash;
  }

  public abstract T unhash();

  @Override
  public int length() {
    return hash.length();
  }

  @Override
  public char charAt(int index) {
    return hash.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return hash.subSequence(start, end);
  }

  @Override
  @Nonnull
  public String toString() {
    return this.hash;
  }
}
