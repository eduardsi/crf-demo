package awsm.domain.crm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

  private final CustomerRepository customerRepo;

  public UniqueEmailValidator(CustomerRepository customerRepo) {
    this.customerRepo = customerRepo;
  }

  public void initialize(UniqueEmail constraint) {}

  public boolean isValid(String email, ConstraintValidatorContext context) {
    return !customerRepo.existsByEmail(email);
  }
}
