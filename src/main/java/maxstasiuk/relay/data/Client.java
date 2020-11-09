package maxstasiuk.relay.data;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Embeddable
@Access(AccessType.FIELD)
@XmlRootElement(name = "client")
@XmlAccessorType(XmlAccessType.FIELD)
public class Client implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@XmlElement(name = "firstName", required = true)
	String firstName;
	@XmlElement(name = "lastName", required = true)
	String lastName;
	@XmlElement(name = "middleName", required = true)
	String middleName;
	@XmlElement(name = "inn", required = true)
	Long inn;
	
	protected Client() {}
	
	public Client(String firstName, String middleName, String lastName, long inn) {
		this.firstName = Objects.requireNonNull(firstName);
		this.middleName = Objects.requireNonNull(middleName);
		this.lastName = Objects.requireNonNull(lastName);
		this.inn = inn;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Client)) {
			return false;
		}
		Client other = (Client)obj;
		if (!this.firstName.equalsIgnoreCase(other.firstName) 
				|| !this.middleName.equalsIgnoreCase(other.middleName) 
				|| !this.lastName.equalsIgnoreCase(other.lastName) 
				|| !this.inn.equals(other.inn)) {
			return false;
		} else {
			return true;			
		}
	}
	
	@Override
	public int hashCode() {
		return inn.hashCode();
	}
	
	@Override
	public String toString() {
		return "firstName=" + firstName + "\n"
				+ "lastName=" + lastName + "\n"
				+ "middleName=" + middleName + "\n"
				+ "inn=" + inn;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public long getInn() {
		return inn;
	}
	
	void afterUnmarshal(Unmarshaller um, Object parent) {
		Objects.requireNonNull(firstName, "firstName is null");
		Objects.requireNonNull(middleName, "middleName is null");
		Objects.requireNonNull(lastName, "lastName is null");
		Objects.requireNonNull(inn, "inn is null");
	}
}
