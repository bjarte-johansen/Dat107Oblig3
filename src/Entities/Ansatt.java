package Entities;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(schema = "public")
public class Ansatt {
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
	 * @return descriptive string
	 */
	
	public static <T> String formatField(String key, T value) {
	    return key + ": " + value;
	}
	
    public String toString() {
    	/*
    	int n = 8;
    	List<String> fields = new ArrayList<String>();
    	fields.add(formatField("id", id));
    	fields.add(formatField("brukernavn", brukernavn));
    	fields.add(formatField("fornavn", fornavn));
    	fields.add(formatField("etternavn", etternavn));
    	fields.add(formatField("ansettelsedato", ansettelsedato));
    	fields.add(formatField("stilling", stilling));
    	fields.add(formatField("loennPerMaaned", loennPerMaaned));
    	fields.add(formatField("avdeling", avdeling));
    	String tmp = "";
    	for (int i = 0; i < n; i++) {
    		tmp += fields.get(i) + ", ";
    	}    	
    	return "Ansatt [\n" + tmp + "]";
    	*/ 
    	return "Ansatt [id=" + id + ", brukernavn=" + brukernavn + ", fornavn=" + fornavn + ", etternavn=" + etternavn + ", ansettelsedato=" + ansettelsedato + ", stilling=" + stilling + ", loennPerMaaned=" + loennPerMaaned + ", avdeling=" + avdeling + "]";
    }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // or AUTO
	private Integer id;
	
	private String brukernavn;
	private String fornavn;
	private String etternavn;
	
	private LocalDateTime ansettelsedato;
	private String stilling;
	private Float loennPerMaaned;
	
	@OneToOne
	@JoinColumn(name = "AvdelingId", foreignKey = @ForeignKey(name = "avdeling_id_fk"))
	private Avdeling avdeling;
}
