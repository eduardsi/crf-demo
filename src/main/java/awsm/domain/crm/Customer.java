package awsm.domain.crm;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

  @Id private String personalId;

  private String firstName;

  private String lastName;

  private String email;
}
