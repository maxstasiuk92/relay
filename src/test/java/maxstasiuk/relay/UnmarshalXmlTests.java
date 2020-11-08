package maxstasiuk.relay;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import maxstasiuk.relay.data.Transaction;
import maxstasiuk.relay.xml.TransactionReader;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Optional;

@ActiveProfiles("test")
@SpringBootTest
public class UnmarshalXmlTests {
	
	@Test
	public void checkUnmarshaledTransactionNumber() throws Exception {
		int count = 0;
		try (InputStream fileStream = new FileInputStream("src/test/resources/3transactions.xml");
				Reader fileReader = new InputStreamReader(fileStream);
				TransactionReader transactionReader = new TransactionReader(fileReader)) {
			
			while (!transactionReader.endOfTransactions()) {
				Optional<Transaction> ot = transactionReader.retrieveTransaction();
				if (ot.isPresent()) {
					count++;
				}
			}
		}
		assertEquals(3, count);
	}
}
