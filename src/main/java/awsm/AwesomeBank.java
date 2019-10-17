package awsm;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
class AwesomeBank implements CommandLineRunner {

  @Override
  public void run(String... args) {
  }

  public static void main(String[] args) {
    SpringApplication.run(AwesomeBank.class, args);
  }
}
