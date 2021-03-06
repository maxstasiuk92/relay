package maxstasiuk.relay.xml;

import java.io.Reader;
import java.util.NoSuchElementException;

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
	private Transaction nextTransaction;
	private State state;
	
	enum State {NEW, EMPTY_NEXT, FULL_NEXT, OVER};
	
	public TransactionReader(Reader source) throws NullPointerException, XmlProcessingException {
		if (source == null) {
			throw new NullPointerException("source is null");
		}
		nextTransaction = null;
		state = State.NEW;
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
	
	public boolean hasNext() {
		if (state != State.FULL_NEXT) {
			unmarshalIfPossible();
		}
		return state == State.FULL_NEXT;
	}
	
	public Transaction next() throws NoSuchElementException {
		if (state != State.FULL_NEXT) {
			unmarshalIfPossible();
		} 
		if (state != State.FULL_NEXT) {
			throw new NoSuchElementException();
		}
		state = State.EMPTY_NEXT;
		return nextTransaction;
	}
	
	@Override
	public void close() throws Exception {
		xmlStreamReader.close(); //source remains open
	}
	
	protected void unmarshalIfPossible() {
		gotoFirstIfNeeded();
		if (state != State.EMPTY_NEXT && state != State.FULL_NEXT) {
			return;
		}
		try {
			//skip white spaces before tag
			int event = xmlStreamReader.getEventType();
			if (event != XMLStreamReader.START_ELEMENT && event != XMLStreamReader.END_ELEMENT) {
				event = xmlStreamReader.nextTag();
			}
			//analyze tag
			if (event == XMLStreamReader.START_ELEMENT) {
				nextTransaction = unmarshaller.unmarshal(xmlStreamReader, Transaction.class).getValue();
				state = State.FULL_NEXT;
			} else if (event == XMLStreamReader.END_ELEMENT) {
				state = State.OVER;
			} else {
				state = State.OVER;
				throw new XmlProcessingException("expected START_ELEMENT or END_ELEMENT, but found " + event);
			}
		} catch(JAXBException | XMLStreamException e) {
			state = State.OVER;
			throw new XmlProcessingException(e);
		} //XmlProcessingException should not be caught here
	}
	
	protected void gotoFirstIfNeeded() throws XmlProcessingException {
		if (state != State.NEW) {
			return;
		}
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
			state = State.OVER;
			throw new XmlProcessingException(e);
		}
		if (!atTransaction) {
			state = State.OVER;
			throw new XmlProcessingException("no <transaction> found");
		}
		state = State.EMPTY_NEXT;
	}
	
	
}
