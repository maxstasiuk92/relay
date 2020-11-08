package maxstasiuk.relay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class RelayApplication  {
	
	public static void main(String[] args) {
		SpringApplication.run(RelayApplication.class, args);
	}
	
}