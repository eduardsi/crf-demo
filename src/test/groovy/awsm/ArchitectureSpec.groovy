package awsm

import com.tngtech.archunit.core.importer.ClassFileImporter
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices

class ArchitectureSpec extends Specification {

  static final allClasses() {
    new ClassFileImporter().importPackages("awsm")
  }

  def "no package cycles allowed"() {
    given:
      final rule = slices().matching("..(*)..").should().beFreeOfCycles()
    expect:
      rule.check(allClasses())
  }

  def "no @Autowired annotation allowed"() {
    given:
      final rule = noCodeUnits()
              .should()
              .beAnnotatedWith(Autowired)
              .because("Spring uses constructor injection by default and such annotation are redundant")
    expect:
      rule.check(allClasses())
  }

}
