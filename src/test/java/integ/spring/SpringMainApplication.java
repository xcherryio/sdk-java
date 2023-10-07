package integ.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "integ.spring", "integ.basic" })
public class SpringMainApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SpringMainApplication.class, args);
    }
}
