package awsm.domain.registration;

import static com.google.common.base.Preconditions.checkArgument;

import awsm.infra.hibernate.HibernateConstructor;
import javax.persistence.Embeddable;

@Embeddable
public class Email {

  private String email;

  public Email(String email) {
    var isNotBlank = email != null && !email.isBlank();
    checkArgument(isNotBlank, "Email %s must not be blank", email);
    this.email = email;
  }

  @HibernateConstructor
  private Email() {
  }

  @Override
  public String toString() {
    return email;
  }

}
