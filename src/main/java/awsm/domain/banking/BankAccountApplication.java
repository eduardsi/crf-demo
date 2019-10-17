package awsm.domain.banking;

import static java.time.ZoneOffset.UTC;

import awsm.domain.registration.Customer;
import awsm.infra.hibernate.HibernateConstructor;
import java.time.LocalDateTime;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BANK_ACCOUNT_APPLICATIONS")
public class BankAccountApplication {

  public enum Status {
    NEW, APPROVED, REFUSED
  }



  @Id
  @GeneratedValue
  @Nullable
  private Long id;

  @SuppressWarnings("unused")
  private long customerId;

  @SuppressWarnings("unused")
  @Enumerated(EnumType.STRING)
  private Status status = Status.NEW;

  @SuppressWarnings("unused")
  @Enumerated(EnumType.STRING)
  private BankAccount.Type accountType;

  @Column
  private LocalDateTime date = LocalDateTime.now(UTC);

  @HibernateConstructor
  private BankAccountApplication() {
  }

  public BankAccountApplication(Customer customer, BankAccount.Type accountType) {
    this.customerId = customer.id();
    this.accountType = accountType;
  }


}
