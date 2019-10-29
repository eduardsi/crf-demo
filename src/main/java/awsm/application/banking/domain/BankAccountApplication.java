package awsm.application.banking.domain;

import static java.time.ZoneOffset.UTC;

import java.time.LocalDateTime;
import javax.annotation.Nullable;

class BankAccountApplication {

  public enum Status {
    NEW, APPROVED, REFUSED
  }

  @Nullable
  private Long id;

  @SuppressWarnings("unused")
  private Status status = Status.NEW;

  @SuppressWarnings("unused")
  private BankAccountType accountType;

  private LocalDateTime date;

  public BankAccountApplication(BankAccountType accountType) {
    this.accountType = accountType;
    this.date = LocalDateTime.now(UTC);
  }


}
