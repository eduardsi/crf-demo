package awsm;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {JacksonAutoConfiguration.class})
@EnableSpringConfigured
@EnableScheduling
public class AwesomeApp {

  static {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
  }

  public static void main(String[] args) {
    SpringApplication.run(AwesomeApp.class, args);
  }
}
