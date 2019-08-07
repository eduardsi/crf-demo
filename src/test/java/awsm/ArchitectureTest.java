package awsm;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noCodeUnits;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import an.awesome.pipelinr.Command;
import awsm.infra.modeling.Event;
import awsm.infra.pipeline.tx.Tx;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaAnnotation;
import com.tngtech.archunit.core.domain.JavaEnumConstant;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@AnalyzeClasses(packages = "awsm")
class ArchitectureTest {

  private static final String DOMAIN = "..domain..";
  private static final String APPLICATION = "..application..";
  private static final String INFRASTRUCTURE = "..infra..";

  private static DescribedPredicate<JavaAnnotation> transactionalWithMandatoryPropagation() {
    return new DescribedPredicate<>("Transactional with mandatory propagation") {
      @Override
      public boolean apply(JavaAnnotation annotation) {
        var rawType = annotation.getRawType();
        var hasTxAnnotation = rawType.isAssignableFrom(Transactional.class);
        var propagation = (JavaEnumConstant) annotation.getProperties().get("propagation");
        return hasTxAnnotation && propagation != null && propagation.name().equals(Propagation.MANDATORY.name());
      }
    };
  }

  @ArchTest
  static ArchRule no_cycles_in_application =
          slices().matching("awsm.application.(*)..").should().beFreeOfCycles();

  @ArchTest
  static ArchRule no_cycles_in_domain =
          slices().matching("awsm.domain.(*)..").should().beFreeOfCycles();

  @ArchTest
  static ArchRule no_cycles_in_infrastructure =
          slices().matching("awsm.infra.(*)..").should().beFreeOfCycles();

  @ArchTest
  static ArchRule repositories_require_a_mandatory_tx_propagation =
          classes()
                  .that().areAssignableTo(Repository.class)
                  .should().beAnnotatedWith(transactionalWithMandatoryPropagation())
                  .because("Transaction boundaries are set using " + Tx.class.getSimpleName() + "command ");

  @ArchTest
  static ArchRule handler_names_should_end_with_Handler =
          classes()
                  .that().implement(Command.Handler.class)
                  .should().haveSimpleNameEndingWith("Handler");

  @ArchTest
  static ArchRule no_autowire_annotation_anywhere =
          noCodeUnits()
                  .should().beAnnotatedWith(Autowired.class)
                  .because("Spring uses constructor injection by default");

  @ArchTest
  static ArchRule no_tx_annotation_on_classes_and_interfaces =
          noClasses()
                  .that().areNotAssignableTo(Repository.class)
                  .should().beAnnotatedWith(Transactional.class)
                  .orShould().beAnnotatedWith(javax.transaction.Transactional.class)
                  .because("Transaction boundaries are set using " + Tx.class.getSimpleName() + "command ");


  @ArchTest
  static ArchRule no_tx_annotation_on_methods =
          noMethods()
                  .that().areDeclaredInClassesThat().areNotAssignableTo(Repository.class)
                  .should().beAnnotatedWith(Transactional.class)
                  .orShould().beAnnotatedWith(javax.transaction.Transactional.class)
                  .because("Transaction boundaries are set using " + Tx.class.getSimpleName() + "command ");

  @ArchTest
  static ArchRule infra_does_not_depend_on_domain;

  static {
    infra_does_not_depend_on_domain = noClasses()
            .that().resideInAnyPackage(INFRASTRUCTURE)
            .should().accessClassesThat().resideInAPackage(DOMAIN);
  }

  @ArchTest
  static ArchRule infra_does_not_depend_on_application =
          noClasses()
                  .that().resideInAnyPackage(INFRASTRUCTURE)
                  .should().accessClassesThat().resideInAPackage(APPLICATION);

  @ArchTest
  static ArchRule events_do_not_depend_on_domain =
          noClasses().that().implement(Event.class)
                  .should().dependOnClassesThat().resideInAnyPackage(DOMAIN);

  @ArchTest
  static ArchRule commands_do_not_depend_on_domain =
          noClasses().that().implement(Command.class)
                  .should().dependOnClassesThat().resideInAnyPackage(DOMAIN);

}
