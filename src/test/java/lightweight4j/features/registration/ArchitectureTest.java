package lightweight4j.features.registration;

import an.awesome.pipelinr.Command;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.beans.factory.annotation.Autowired;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "lightweight4j")
class ArchitectureTest {

    @ArchTest
    static ArchRule noCyclesBetweenFeatures =
            slices().matching("lightweight4j.features.(*)..").should().beFreeOfCycles();

    @ArchTest
    static ArchRule noCyclesBetweenLibPackages =
            slices().matching("lightweight4j.lib.(*)..").should().beFreeOfCycles();

    @ArchTest
    static ArchRule implementationIsHidden =
            classes()
                .that().resideInAPackage("..impl..")
                .should().bePackagePrivate()
                .because("Access to code is provided through the API");

    @ArchTest
    static ArchRule handlersAreNamedProperly =
            classes()
                    .that().implement(Command.Handler.class)
                    .should().haveSimpleNameEndingWith("Handler");

    @ArchTest
    static ArchRule autowiredAnnotationIsNotUsed =
            noCodeUnits()
                .should().beAnnotatedWith(Autowired.class)
                .because("Spring uses constructor injection by default");

    @ArchTest
    static ArchRule libIsIndependentFromFeatures =
            noClasses()
                .that().resideInAnyPackage("..lib..")
                .should().accessClassesThat().resideInAPackage("..features..");
}
