package maxstasiuk.relay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTable;
import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.TestPropertySource;

//NOTE: active profile is not test
@SpringBootTest
@TestPropertySource(properties = 
	{"spring.datasource.url=jdbc:derby:memory:testdb;create=true",
	"spring.jpa.show-sql=true",
	"inputXmlPath = src/test/resources/3transactions.xml",
	"inputXmlEncoding = UTF-8"})
public class XmlToRepositoryTest {
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
	public void checkSavedTransactionNumber() {
		//data should already be in repository
		assertEquals(3, countRowsInTable(this.testJdbcTempl, "Transactions"));
		
		deleteFromTables(this.testJdbcTempl, "Transactions");
	}

}
