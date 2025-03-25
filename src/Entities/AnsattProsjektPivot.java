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
@Table(schema = "public")
public class AnsattProsjektPivot {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // or AUTO
	private Integer id;
	
	private String rolle;
	
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

	@OneToOne
	@JoinColumn(name="ansattId", foreignKey=@ForeignKey(name="FK_ansattId"))
	private Ansatt ansatt;
	
	@OneToOne
	@JoinColumn(name="prosjektId", foreignKey=@ForeignKey(name="FK_prosjektId"))
	private Prosjekt prosjekt;
	
	private Integer antallTimer;

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
		return "AnsattProsjektPivot [id=" + id 
				+ "\n, ansatt=" + ansatt 
				+ "\n, prosjekt=" + prosjekt 
				+ "\n, rolle=" + rolle
				+ "\n, antallTimer=" + antallTimer + "]";
	}
}
