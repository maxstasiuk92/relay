package maxstasiuk.relay.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "Transactions")
@Access(AccessType.PROPERTY)
@XmlSeeAlso({Client.class})
@XmlRootElement(name = "transaction")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Transaction implements Serializable {
	private static final long serialVersionUID = 1L;
		
	protected long id;
	protected String place;
	protected BigDecimal amount;
	protected String currency;
	protected String card;
	protected Client client;
	
	protected Transaction() {}
	
	public Transaction(String place, BigDecimal amount, String currency, String card, Client client) {
		this.place = Objects.requireNonNull(place);
		this.amount = Objects.requireNonNull(amount);
		this.currency = Objects.requireNonNull(currency);
		this.card = Objects.requireNonNull(card);
		this.client = Objects.requireNonNull(client);
	}
	
	@Id
	@GeneratedValue
	protected long getId() {
		return id;
	}
	
	@Basic
	public String getPlace() {
		return place;
	}
	
	@Basic
	public BigDecimal getAmount() {
		return amount;
	}
	
	@Basic
	public String getCurrency() {
		return currency;
	}

	@Basic
	public String getCard() {
		return card;
	}

	@Embedded
	public Client getClient() {
		return client;
	}
	
	@Override
	public int hashCode() {
		//just some implementation
		return client.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Transaction)) {
			return false;
		}
		Transaction other = (Transaction)obj;
		return this.place.equalsIgnoreCase(other.place)
				&& this.amount.equals(other.amount)
				&& this.currency.equalsIgnoreCase(other.currency)
				&& this.card.equals(other.card)
				&& this.client.equals(other.client);
	}
	
	@Override
	public String toString() {
		return "place=" + place + "\n"
				+ "amount=" + amount + "\n"
				+ "currency=" + currency + "\n"
				+ "card=" + card + "\n"
				+ "client={" + client + "}";
	}
	
	@XmlTransient
	protected void setId(long id) {
		this.id = id;
	}
	
	@XmlElement(name = "place")	
	protected void setPlace(String place) {
		this.place = place;
	}
	
	@XmlElement(name = "amount")
	protected void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
		
	@XmlElement(name = "currency")
	protected void setCurrency(String currency) {
		this.currency = currency;
	}
	
	@XmlElement(name = "card")
	protected void setCard(String card) {
		this.card = card;
	}
	
	@XmlElementRef
	protected void setClient(Client client) {
		this.client = client;
	}
	
	void afterUnmarshal(Unmarshaller um, Object parent) {
		Objects.requireNonNull(place, "place is null");
		Objects.requireNonNull(amount, "amount is null");
		Objects.requireNonNull(currency, "currency is null");
		Objects.requireNonNull(card, "card is null");
		Objects.requireNonNull(client, "client is null");
	}
}
