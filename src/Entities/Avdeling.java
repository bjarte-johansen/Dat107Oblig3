package Entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import DAO.AnsattDAO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.TypedQuery;
import main.StaticEMF;


@Entity
public class Avdeling {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // or AUTO
	private Integer id;
	

	private String navn;

	@OneToOne
	@JoinColumn(name = "lederId")
	private Ansatt leder;

	@OneToMany(mappedBy = "avdeling", fetch = FetchType.EAGER)
	public List<Ansatt> ansatte = new ArrayList<>();	
	
	
	/**
	 * @return the ansatte
	 */
	public List<Ansatt > getAnsatte(){
		return ansatte;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		Avdeling other = (Avdeling) obj;
		return Objects.equals(id, other.id);
	}	
	
	
	/**
	 * @return the leder
	 */
	public Ansatt getLeder() {
		return leder;
	}

	/**
	 * @param leder the leder to set
	 */
	public void setLeder(Ansatt leder) {
		this.leder = leder;
	}

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
	 * @param add ansatt
	 * @return void
	 */
	public void addAnsatt(Ansatt ansatt) {
		ansatte.add(ansatt);
		ansatt.setAvdeling(this);
	}

	/**
	 * 
	 */
	public String toString() {
		return 
			"Avdeling ["
			+ "id=" + id
			+ ", navn=" + navn
			+ ", lederId=" + ((leder != null) ? leder.getId() : "null")
			+ ", leder=" + leder.getFullName() 
			+ "]";
	}
}
