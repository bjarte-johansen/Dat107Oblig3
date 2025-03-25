package Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="Avdeling", schema = "public")
public class Avdeling {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // or AUTO
	private Integer id;
	
	private String navn;
	
	private Integer lederId;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the navn
	 */
	public String getNavn() {
		return navn;
	}

	/**
	 * @param navn the navn to set
	 */
	public void setNavn(String navn) {
		this.navn = navn;
	}

	/**
	 * @return the lederId
	 */
	public Integer getLederId() {
		return lederId;
	}

	/**
	 * @param lederId the lederId to set
	 */
	public void setLederId(Integer lederId) {
		this.lederId = lederId;
	}
	
	/**
	 * 
	 */
	public String toString() {
		return "Avdeling [navn=" + navn + "]";
	}
}
