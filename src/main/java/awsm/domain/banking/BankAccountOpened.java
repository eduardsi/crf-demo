package awsm.domain.banking;

import awsm.domain.core.DomainEvent;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(fluent = true)
public class BankAccountOpened implements DomainEvent {

  private final String iban;

  private final LocalDate date;
}
