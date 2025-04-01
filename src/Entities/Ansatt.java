package Entities;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import testing.EntityFormatter;

@Entity
public class Ansatt {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // or AUTO
	private Integer id;
	
	private String brukernavn;
	private String fornavn;
	private String etternavn;
	
	private LocalDateTime ansettelsedato;
	private String stilling;
	private Float loennPerMaaned;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "avdelingId")
	private Avdeling avdeling;
	
	@OneToOne(mappedBy = "leder")
	private Avdeling leder;
	
	@OneToMany(mappedBy = "ansatt", cascade = CascadeType.ALL)
	private List<AnsattProsjektPivot> prosjekter = new ArrayList<>();	
	
	
	public List<AnsattProsjektPivot> getProsjekter(){
		return prosjekter;
	}
	
	/**
	 * @return the ansettelsedato
	 */
	public LocalDateTime getAnsettelsedato() {
		return ansettelsedato;
	}

	/**
	 * @param ansettelsedato the ansettelsedato to set
	 */
	public void setAnsettelsedato(LocalDateTime ansettelsedato) {
		this.ansettelsedato = ansettelsedato;
	}

	
	/**
	 * @return om ansatt er leder
	 */
	public boolean erAvdelingsLeder() {
		return avdeling.getLeder().equals(this);
	}
	
	
	/**
	 * @return the leder
	 */
	public Avdeling getLeder() {
		return leder;
	}

	/**
	 * @param leder the leder to set
	 */
	public void setLeder(Avdeling leder) {
		this.leder = leder;
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
		Ansatt other = (Ansatt) obj;
		return Objects.equals(id, other.id);
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
	 * @return the brukernavn
	 */
	public String getBrukernavn() {
		return brukernavn;
	}

	/**
	 * @param brukernavn the brukernavn to set
	 */
	public void setBrukernavn(String brukernavn) {
		this.brukernavn = brukernavn;
	}

	public String getFullName() {
		return getEtternavn() + ", " + getFornavn();
	}
	
	/**
	 * @return the fornavn
	 */
	public String getFornavn() {
		return fornavn;
	}

	/**
	 * @param fornavn the fornavn to set
	 */
	public void setFornavn(String fornavn) {
		this.fornavn = fornavn;
	}

	/**
	 * @return the etternavn
	 */
	public String getEtternavn() {
		return etternavn;
	}

	/**
	 * @param etternavn the etternavn to set
	 */
	public void setEtternavn(String etternavn) {
		this.etternavn = etternavn;
	}

	/**
	 * @return the ansettelsesdato
	 */
	public LocalDateTime getAnsettelseDato() {
		return ansettelsedato;
	}

	/**
	 * @param ansettelsesdato the ansettelsesdato to set
	 */
	public void setAnsettelseDato(LocalDateTime ansettelsedato) {
		this.ansettelsedato = ansettelsedato;
	}

	/**
	 * @return the stilling
	 */
	public String getStilling() {
		return stilling;
	}

	/**
	 * @param stilling the stilling to set
	 */
	public void setStilling(String stilling) {
		this.stilling = stilling;
	}

	/**
	 * @return the loennPerMaaned
	 */
	public Float getLoennPerMaaned() {
		return loennPerMaaned;
	}

	/**
	 * @param loennPerMaaned the loennPerMaaned to set
	 */
	public void setLoennPerMaaned(Float loennPerMaaned) {
		this.loennPerMaaned = loennPerMaaned;
	}

	/**
	 * @return the avdeling
	 */
	public Avdeling getAvdeling() {
		return avdeling;
	}

	/**
	 * @param avdeling the avdeling to set
	 */
	public void setAvdeling(Avdeling avdeling) {
		this.avdeling = avdeling;
	}
	
	/**
	 * @param Prosjekt navn som string array
	 * @return String[] of prosjektnavn
	 */
	
	public String[] getProsjektNavnAsArray() {
		ArrayList<String> result = new ArrayList<>(16);
		for (AnsattProsjektPivot piv : prosjekter) {
			result.add(piv.getProsjekt().getNavn());
		}
		return result.toArray(new String[0]);
	}
	
	/**
	 * @return descriptive string
	 */

	@Override
    public String toString() {    	
    	// simplified output
		String tmpStilling = (stilling != null) ? stilling : "ukjent stilling";
		return "Ansatt [" 
			+ "id=" + id 
			+ ", \"" + getEtternavn() 
			+ ", " + getFornavn() 
			+ "\" (u:" + brukernavn + ")" 
			+ ", " + tmpStilling + " v/" + avdeling.getNavn() 
			+ ", ansatt=" + ansettelsedato.toLocalDate() 
			+ ", lønn=" + loennPerMaaned  
			+ "/month, prosjekter: " + Arrays.toString(getProsjektNavnAsArray())
			+ "]";
    }
}
