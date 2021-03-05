package awsm.infrastructure.mail;

import org.simplejavamail.springsupport.SimpleJavaMailSpringSupport;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SimpleJavaMailSpringSupport.class)
class MailConfiguration {}
