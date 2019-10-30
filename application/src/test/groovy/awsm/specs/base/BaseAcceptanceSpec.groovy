package awsm.specs.base

import awsm.specs.traits.WithBlacklist
import awsm.specs.traits.WithFaker
import awsm.specs.traits.WithMockMvc
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class BaseAcceptanceSpec extends Specification implements
        WithMockMvc,
        WithBlacklist,
        WithFaker {
}
