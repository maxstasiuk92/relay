package maxstasiuk.relay;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import maxstasiuk.relay.data.Client;
import maxstasiuk.relay.data.Transaction;
import maxstasiuk.relay.xml.TransactionReader;
import maxstasiuk.relay.xml.XmlProcessingException;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class BindXmlTests {
	
	@Test
	public void correctTransaction() throws Exception {
		//instantiate transaction
		Client c = new Client("Ivan", "Sidoroff", "Sidoroff", 1234567892);
		BigDecimal a = new BigDecimal("12.01");
		Transaction refTransaction = new Transaction("A PLACE 3", a, "USD", "123456****1234", c);
		Transaction resTransaction;
		
		//marshal
		ByteArrayOutputStream xmlOutStream = new ByteArrayOutputStream();
		Marshaller m = JAXBContext.newInstance(Transaction.class).createMarshaller();
		m.marshal(refTransaction, xmlOutStream);
		try (InputStream xmlInStream = new ByteArrayInputStream(xmlOutStream.toByteArray());
				Reader xmlReader = new InputStreamReader(xmlInStream);
				TransactionReader transactionReader = new TransactionReader(xmlReader)) {
			
			//unmarshal
			resTransaction = transactionReader.next();
		}
		//compare
		assertEquals(refTransaction, resTransaction);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {noPlace, noLastName, incorrectAmount})
	public void incorrectTransaction(String input) throws Exception {
		try (InputStream xmlInStream = new ByteArrayInputStream(input.getBytes());
				Reader xmlReader = new InputStreamReader(xmlInStream);
				TransactionReader transactionReader = new TransactionReader(xmlReader)) {
			
			assertThrows(XmlProcessingException.class, ()->transactionReader.next());
		}
	}
	
	public static final String noPlace =
			"<transactions>\n" + 
			"	<transaction>\n" + 
			"							\n" + 
			"		<amount>12.01</amount>\n" + 
			"		<currency>USD</currency>\n" + 
			"		<card>123456****1234</card>\n" + 
			"		<client>\n" + 
			"			<firstName>Ivan</firstName>\n" + 
			"			<lastName>Sidoroff</lastName>\n" + 
			"			<middleName>Sidoroff</middleName>\n" + 
			"			<inn>1234567892</inn>\n" + 
			"		</client>\n" + 
			"	</transaction>\n" + 
			"</transactions>";
	
	public static final String noLastName =
			"<transactions>\n" + 
			"	<transaction>\n" + 
			"		<place>A PLACE 3</place>\n" + 
			"		<amount>12.01</amount>\n" + 
			"		<currency>USD</currency>\n" + 
			"		<card>123456****1234</card>\n" + 
			"		<client>\n" + 
			"			<firstName>Ivan</firstName>\n" + 
			"										\n" + 
			"			<middleName>Sidoroff</middleName>\n" + 
			"			<inn>1234567892</inn>\n" + 
			"		</client>\n" + 
			"	</transaction>\n" + 
			"</transactions>";
	
	public static final String incorrectAmount =
			"<transactions>\n" + 
			"	<transaction>\n" + 
			"		<place>A PLACE 3</place>\n" + 
			"		<amount>12.01.01</amount>\n" + 
			"		<currency>USD</currency>\n" + 
			"		<card>123456****1234</card>\n" + 
			"		<client>\n" + 
			"			<firstName>Ivan</firstName>\n" + 
			"			<lastName>Sidoroff</lastName>\n" + 
			"			<middleName>Sidoroff</middleName>\n" + 
			"			<inn>1234567892</inn>\n" + 
			"		</client>\n" + 
			"	</transaction>\n" + 
			"</transactions>";
}
