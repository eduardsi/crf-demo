package awsm.application.banking.impl;

class Iban {

  private String iban;


  Iban(String iban) {
    this.iban = iban;
  }

  static Iban newlyGenerated() {
    return new Iban(org.iban4j.Iban.random().toString());
  }

  public String toString() {
    return iban;
  }
}
