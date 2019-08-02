package lightweight4j;

import an.awesome.pipelinr.Command;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaAnnotation;
import com.tngtech.archunit.core.domain.JavaEnumConstant;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import lightweight4j.lib.pipeline.Tx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "lightweight4j")
class ArchitectureTest {

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
    static ArchRule repositoriesRequireTransaction =
            classes()
                    .that().areAssignableTo(Repository.class)
                    .should().beAnnotatedWith(transactionalWithMandatoryPropagation())
                    .because("Transaction boundaries are set using " + Tx.class.getSimpleName() + "command ");

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
    static ArchRule transactionalAnnotationIsNotUsedInAClass =
            noClasses()
                    .that().areNotAssignableTo(Repository.class)
                    .should().beAnnotatedWith(Transactional.class)
                    .orShould().beAnnotatedWith(javax.transaction.Transactional.class)
                    .because("Transaction boundaries are set using " + Tx.class.getSimpleName() + "command ");


    @ArchTest
    static ArchRule transactionalAnnotationIsNotUsedOnMethods =
            noMethods()
                    .that().areDeclaredInClassesThat().areNotAssignableTo(Repository.class)
                    .should().beAnnotatedWith(Transactional.class)
                    .orShould().beAnnotatedWith(javax.transaction.Transactional.class)
                    .because("Transaction boundaries are set using " + Tx.class.getSimpleName() + "command ");

    @ArchTest
    static ArchRule libIsIndependentFromFeatures =
            noClasses()
                .that().resideInAnyPackage("..lib..")
                .should().accessClassesThat().resideInAPackage("..features..");
}
