package awsm.application.banking.domain;

class Iban {

  private String iban;


  Iban(String iban) {
    this.iban = iban;
  }

  static Iban newlyGenerated() {
    return new Iban(org.iban4j.Iban.random().toString());
  }

  @Override
  public String toString() {
    return iban;
  }
}
