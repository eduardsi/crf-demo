package awsm.domain.banking;

import awsm.domain.core.DomainEvent;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class BankAccountOpened implements DomainEvent {

  private final String iban;

  private final LocalDate date;
}
