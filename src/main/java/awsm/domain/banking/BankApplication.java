package awsm.domain.banking;

import awsm.domain.banking.customer.Customer;
import awsm.domain.banking.customer.Email;
import awsm.domain.banking.customer.Name;
import jooq.tables.records.BankApplicationRecord;
import org.jooq.DSLContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import static awsm.infrastructure.time.TimeMachine.clock;
import static java.util.stream.Collectors.toSet;
import static jooq.Tables.BANK_APPLICATION;

public class BankApplication {

  private Email email;

  private Name name;

  private String countryOfResidence;

  private LocalDate dateOfBirth;

  private LocalDateTime submissionDate;

  private Optional<Long> id = Optional.empty();

  private Set<BankService> services;

  public BankApplication(Email email, Name name, String countryOfResidence, LocalDate dateOfBirth) {
    this.email = email;
    this.name = name;
    this.countryOfResidence = countryOfResidence;
    this.dateOfBirth = dateOfBirth;
    this.services = new HashSet<>();
    this.submissionDate = LocalDateTime.now(clock());
  }

  private BankApplication() {
  }

  public void optIn(Collection<BankService> services) {
    this.services.addAll(services);
  }

  public void saveNew(DSLContext db) {
    this.id = Optional.of(db
            .insertInto(BANK_APPLICATION)
            .set(BANK_APPLICATION.APPLICANT_COUNTRY_OF_RESIDENCE, this.countryOfResidence)
            .set(BANK_APPLICATION.APPLICANT_DATE_OF_BIRTH, this.dateOfBirth)
            .set(BANK_APPLICATION.APPLICANT_EMAIL, this.email + "")
            .set(BANK_APPLICATION.APPLICANT_FIRST_NAME, this.name.firstName)
            .set(BANK_APPLICATION.APPLICANT_LAST_NAME, this.name.lastName)
            .set(BANK_APPLICATION.SUBMISSION_DATE, this.submissionDate)
            .set(BANK_APPLICATION.SERVICES, this.services.stream().map(Enum::name).toArray(String[]::new))
            .returning(BANK_APPLICATION.ID)
            .fetchOne()
            .getId());
  }

  private void delete(DSLContext db) {
    db.deleteFrom(BANK_APPLICATION).where(BANK_APPLICATION.ID.eq(id()));
  }

  public long id() {
    return id.orElseThrow();
  }

  public void approve(DSLContext db) {
    this.delete(db);

    var customer = new Customer(this.name, this.email, this.dateOfBirth, this.countryOfResidence);
    customer.saveNew(db);

    services.forEach(service -> service.approve(customer));
  }

  public static class Repo {

    private final DSLContext dsl;

    public Repo(DSLContext dsl) {
      this.dsl = dsl;
    }

    public BankApplication findOne(long id) {
      return dsl
              .selectFrom(BANK_APPLICATION)
              .where(BANK_APPLICATION.ID.equal(id))
              .fetchOptional()
              .map(fromJooq())
              .orElseThrow();
    }

    private Function<BankApplicationRecord, BankApplication> fromJooq() {
      return jooq -> {
        var application = new BankApplication();
        application.dateOfBirth = jooq.getApplicantDateOfBirth();
        application.countryOfResidence = jooq.getApplicantCountryOfResidence();
        application.email = new Email(jooq.getApplicantEmail());
        application.name = new Name(jooq.getApplicantFirstName(), jooq.getApplicantLastName());
        application.services = Arrays.stream(jooq.getServices()).map(BankService::valueOf).collect(toSet());
        return application;
      };
    }
  }

}
