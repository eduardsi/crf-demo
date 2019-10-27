package awsm;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noCodeUnits;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import awsm.infrastructure.middleware.Command;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.beans.factory.annotation.Autowired;

@AnalyzeClasses(packages = "awsm")
class ArchitectureTest {

  @ArchTest
  static ArchRule no_cycles =
          slices().matching("..(*)..").should().beFreeOfCycles();

  @ArchTest
  static ArchRule no_autowire_annotation_anywhere =
          noCodeUnits()
                  .should().beAnnotatedWith(Autowired.class)
                  .because("Spring uses constructor injection by default");

  @ArchTest
  static ArchRule jooq_only_in_repositories_or_in_a_designated_infra_package =
      noClasses()
          .that()
          .doNotHaveSimpleName("Repository")
          .and()
          .resideOutsideOfPackage("*.infrastructure.jooq..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("..jooq..");

}
