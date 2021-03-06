package awsm.api

import com.icegreen.greenmail.spring.GreenMailBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration;

@Configuration
class MockMailConfiguration {

    @Bean
    GreenMailBean greenMailBean() {
        def bean = new GreenMailBean()
        bean
    }

}
