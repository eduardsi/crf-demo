package awsm;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noCodeUnits;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import awsm.infra.middleware.Command;
import awsm.infra.middleware.impl.react.Reaction;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.beans.factory.annotation.Autowired;

@AnalyzeClasses(packages = "awsm")
class ArchitectureTest {

  private static final String DOMAIN = "*.domain..";
  private static final String APPLICATION = "*.application..";
  private static final String INFRASTRUCTURE = "*.infra..";

  private static DescribedPredicate<JavaMethodCall> commandExecute() {
    return new DescribedPredicate<>("command is executed") {
      @Override
      public boolean apply(JavaMethodCall input) {
        var isACommand = input.getTargetOwner().isAssignableTo(Command.class);
        var executeMethod = input.getName().matches("execute");
        return isACommand && executeMethod;
      }
    };
  }

  @ArchTest
  static ArchRule no_cycles =
          slices().matching("..(*)..").should().beFreeOfCycles();

  @ArchTest
  static ArchRule reaction_names_should_be_called_Re =
          classes()
                  .that().implement(Reaction.class)
                  .should().haveSimpleName("Re");

  @ArchTest
  static ArchRule no_autowire_annotation_anywhere =
          noCodeUnits()
                  .should().beAnnotatedWith(Autowired.class)
                  .because("Spring uses constructor injection by default");

  @ArchTest
  static ArchRule infra_does_not_depend_on_domain = noClasses()
            .that().resideInAnyPackage(INFRASTRUCTURE)
            .should().dependOnClassesThat().resideInAPackage(DOMAIN);

  @ArchTest
  static ArchRule infra_does_not_depend_on_application =
          noClasses()
                  .that().resideInAnyPackage(INFRASTRUCTURE)
                  .should().dependOnClassesThat().resideInAPackage(APPLICATION);

  @ArchTest
  static ArchRule commands_do_not_depend_on_domain_and_infra =
          noClasses().that().implement(Command.class)
                  .should().dependOnClassesThat().resideInAnyPackage(DOMAIN)
                  .andShould().dependOnClassesThat().resideInAnyPackage(INFRASTRUCTURE);

  @ArchTest
  static ArchRule reactions_cannot_execute_commands =
      noClasses().that().implement(Reaction.class)
          .should().callMethodWhere(commandExecute())
          .because("they must talk to domain model directly");


}
