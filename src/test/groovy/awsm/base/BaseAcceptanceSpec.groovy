package awsm.base

import awsm.traits.WithBlacklist
import awsm.traits.WithFaker
import awsm.traits.WithMockMvc
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class BaseAcceptanceSpec extends Specification implements
        WithMockMvc,
        WithBlacklist,
        WithFaker {
}
