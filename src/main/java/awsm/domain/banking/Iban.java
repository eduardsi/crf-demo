package awsm.domain.banking;

import awsm.infra.hibernate.HibernateConstructor;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
class Iban {

  @Column(unique = true)
  private String iban;

  @HibernateConstructor
  private Iban() {
  }

  private Iban(String iban) {
    this.iban = iban;
  }

  static Iban newlyGenerated() {
    return new Iban(org.iban4j.Iban.random().toString());
  }
}
