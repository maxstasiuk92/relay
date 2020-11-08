package maxstasiuk.relay;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import maxstasiuk.relay.data.Transaction;
import maxstasiuk.relay.db.TransactionRepository;
import maxstasiuk.relay.xml.TransactionReader;
import maxstasiuk.relay.xml.XmlProcessingException;

@Component
@Profile("!test")
public class XmlToRepositoryRunner implements CommandLineRunner {
	protected TransactionRepository repository;
	protected String inputXmlPath;
	protected String inputXmlEncoding;
	
	public XmlToRepositoryRunner(@Value("${inputXmlPath}") String inputXmlPath,
			@Value("${inputXmlEncoding}") String inputXmlEncoding,
			@Autowired TransactionRepository repository) {
		this.inputXmlPath = inputXmlPath;
		this.inputXmlEncoding = inputXmlEncoding;
		this.repository = repository;
	}
	
	@Override
	public void run(String... args) {
		try(FileInputStream fileStream = new FileInputStream(inputXmlPath);
				Reader fileReader = new InputStreamReader(fileStream, Charset.forName(inputXmlEncoding));
				TransactionReader transactionReader = new TransactionReader(fileReader)) {
			
			while (!transactionReader.endOfTransactions()) {
				List<Transaction> t = loadFromXml(transactionReader, 1);
				saveToRepository(t);
			}
		} catch (XmlProcessingException e) {
			System.out.println("Error in xml-file");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Application error");
			e.printStackTrace();
		}
	}

	protected List<Transaction> loadFromXml(TransactionReader transactionReader, int maxCount) {
		int count = 0;
		boolean hasMore;
		ArrayList<Transaction> transactionBuffer = new ArrayList<>(maxCount);
		do {
			Optional<Transaction> ot = transactionReader.retrieveTransaction();
			if (hasMore = ot.isPresent()) {
				transactionBuffer.add(ot.get());
				count++;
			} 
		} while(hasMore && count < maxCount);
		
		return transactionBuffer;
	}

	@Transactional
	protected void saveToRepository(List<Transaction> transactions) {
		for (var t : transactions) {
			repository.save(t);
		}
	}
}
