package lightweight4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
class Lightweight4j implements CommandLineRunner {

    @Override
    public void run(String... args) {

    }

    public static void main(String[] args) {
        SpringApplication.run(Lightweight4j.class, args);
    }
}
