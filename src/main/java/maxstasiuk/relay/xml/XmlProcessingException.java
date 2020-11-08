package maxstasiuk.relay.xml;

public class XmlProcessingException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public XmlProcessingException() {
		super();
	}
	
	public XmlProcessingException(Throwable cause) {
		super(cause);
	}
	
	public XmlProcessingException(String message) {
		super(message);
	}
	
	public XmlProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
