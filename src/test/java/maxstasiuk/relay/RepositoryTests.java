package maxstasiuk.relay;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import maxstasiuk.relay.data.Client;
import maxstasiuk.relay.data.Transaction;
import maxstasiuk.relay.db.TransactionRepository;

import static org.springframework.test.jdbc.JdbcTestUtils.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@TestPropertySource(properties = 
	{"spring.datasource.url=jdbc:derby:memory:testdb;create=true",
	"spring.jpa.show-sql=true"})
public class RepositoryTests {
	@Autowired
	TransactionRepository repo;
	
	JdbcTemplate testJdbcTempl;
	
	@BeforeEach
	public void prepareTestJdbsTemplate(
			@Value("${spring.datasource.url}") String url,
			@Value("${spring.datasource.username}") String user,
			@Value("${spring.datasource.password}") String password) {
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setUrl(url);
		ds.setUsername(user);
		ds.setPassword(password);
		testJdbcTempl = new JdbcTemplate(ds);
	}
	
	@Test
	public void saveAndGetTest() {
		Client c = new Client("Ivan", "Sidoroff", "Sidoroff", 1234567892);
		BigDecimal a = new BigDecimal("12.01");
		Transaction refTransaction = new Transaction("A PLACE 3", a, "USD", "123456****1234", c);
		repo.save(refTransaction);
		
		assertEquals(1, countRowsInTable(this.testJdbcTempl, "Transactions"));
		
		Transaction resTransaction = repo.findAll().get(0);
		assertEquals(refTransaction, resTransaction);
		
		deleteFromTables(this.testJdbcTempl, "Transactions");
	}
}