package Entities;

import jakarta.persistence.Embeddable;
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
public class AnsattProsjektPivot {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // or AUTO
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="ansattId")
	private Ansatt ansatt;
	
	@OneToOne
	@JoinColumn(name="prosjektId")
	private Prosjekt prosjekt;
	
	private String rolle;	
	private Integer antallTimer;
	
	/**
	 * @return the rolle
	 */
	public String getRolle() {
		return rolle;
	}

	/**
	 * @param rolle the rolle to set
	 */
	public void setRolle(String rolle) {
		this.rolle = rolle;
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
	 * @return the ansatt
	 */
	public Ansatt getAnsatt() {
		return ansatt;
	}

	/**
	 * @param ansatt the ansatt to set
	 */
	public void setAnsatt(Ansatt ansatt) {
		this.ansatt = ansatt;
	}

	/** 
	 * @return the prosjekt
	 */
	public Prosjekt getProsjekt() {
		return prosjekt;
	}

	/**
	 * @param prosjekt the prosjekt to set
	 */
	public void setProsjekt(Prosjekt prosjekt) {
		this.prosjekt = prosjekt;
	}

	/**
	 * @return the antallTimer
	 */
	public Integer getAntallTimer() {
		return antallTimer;
	}

	/**
	 * @param antallTimer the antallTimer to set
	 */
	public void setAntallTimer(Integer antallTimer) {
		this.antallTimer = antallTimer;
	}
	
	/**
	 * 
	 */
	public String toString() {
		return "AnsattProsjektPivot [" 
				+ "\nid=" + id 
				+ "\n, (" + ansatt + ")"
				+ "\n, (" + prosjekt + ")"
				+ "\n, rolle=" + rolle
				+ "\n, antallTimer=" + antallTimer 
				+ "\n]";
	}
}
