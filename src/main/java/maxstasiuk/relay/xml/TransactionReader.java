package maxstasiuk.relay.xml;

import java.io.Reader;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import maxstasiuk.relay.data.Transaction;

public class TransactionReader implements AutoCloseable {
	private XMLStreamReader xmlStreamReader;
	private Unmarshaller unmarshaller;
	private boolean insideTransactions;
	private boolean endOfTransactions;
	
	public TransactionReader(Reader source) throws NullPointerException, XmlProcessingException {
		if (source == null) {
			throw new NullPointerException("source is null");
		}
		insideTransactions = false;
		endOfTransactions = false;
		try {
			xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(source);
			unmarshaller = JAXBContext.newInstance(Transaction.class).createUnmarshaller();
		} catch (XMLStreamException | FactoryConfigurationError | JAXBException e1) {
			try {
				if (xmlStreamReader != null) {
					xmlStreamReader.close(); //source remains open
				}
			} catch (XMLStreamException e2) {
				//nothing to do
			}			
			throw new XmlProcessingException(e1);
		} 
	}
	
	public boolean endOfTransactions() {
		return endOfTransactions;
	}
	
	public Optional<Transaction> retrieveTransaction() throws XmlProcessingException {
		if (endOfTransactions) {
			return Optional.empty();
		}
		if (!insideTransactions) {
			gotoFirstTransaction();
			insideTransactions = true;
		}
		Optional<Transaction> optTransaction = unmarshalTransaction();
		if (optTransaction.isPresent()) {
			return optTransaction;
		} else {
			endOfTransactions = true;
			return Optional.empty();
		}
	}

	@Override
	public void close() throws Exception {
		xmlStreamReader.close(); //source remains open
	}
	
	protected void gotoFirstTransaction() throws XmlProcessingException {
		boolean atTransaction = false;
		try {
			while (xmlStreamReader.hasNext()) {
				if (xmlStreamReader.getEventType() == XMLStreamReader.START_ELEMENT && "transaction".equals(xmlStreamReader.getLocalName())) {
					atTransaction = true;
					break;
				} else {
					xmlStreamReader.next();
				}
			}
		} catch (XMLStreamException e) {
			throw new XmlProcessingException(e);
		}
		if (!atTransaction) {
			throw new XmlProcessingException("no <transaction> found");
		}
	}
	
	protected Optional<Transaction> unmarshalTransaction() {
		//skip white spaces before tag
		int event = xmlStreamReader.getEventType();
		if (!(event == XMLStreamReader.START_ELEMENT || event == XMLStreamReader.END_ELEMENT)) {
			try {
				event = xmlStreamReader.nextTag();
			} catch(XMLStreamException e) {
				throw new XmlProcessingException(e);
			}
		}
		//analyze tag
		if (event == XMLStreamReader.START_ELEMENT) {
			Transaction t;
			try {
				t = unmarshaller.unmarshal(xmlStreamReader, Transaction.class).getValue();
			} catch (JAXBException e) {
				throw new XmlProcessingException(e);
			}
			return Optional.of(t);
		} else if (event == XMLStreamReader.END_ELEMENT) {
			return Optional.empty();
		} else {
			throw new XmlProcessingException("expected START_ELEMENT or END_ELEMENT, but found " + event);
		}
	}
}
