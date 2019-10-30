package awsm.banking;

import java.util.Objects;

class Currency {

  private final String code;

  Currency(String code) {
    this.code = code;
  }

  @Override
  public String toString() {
    return code;
  }

  @Override
  public boolean equals(Object that) {
    if (!(that instanceof Currency)) {
      return false;
    }
    var currency = (Currency) that;
    return Objects.equals(code, currency.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }
}
