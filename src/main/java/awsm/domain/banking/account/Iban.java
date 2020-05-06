package awsm.domain.banking.account;

import com.github.javafaker.Faker;

class Iban {

  private final String iban;

  Iban() {
    this(new Faker().finance().iban());
  }

  Iban(String iban) {
    this.iban = iban;
  }

  @Override
  public String toString() {
    return iban;
  }


}
