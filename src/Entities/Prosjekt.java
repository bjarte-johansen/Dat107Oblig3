package Entities;

import java.util.List;

import DAO.ProsjektDAO;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import main.StaticEMF;

@Entity
@Table(schema = "public")
public class Prosjekt {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // or AUTO
	private Integer id;
	
	private String navn;
	private String beskrivelse;
	
	@OneToMany(mappedBy = "prosjekt", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<AnsattProsjektPivot> deltagere;
	
	public List<AnsattProsjektPivot> getDeltagere(){
		return deltagere;
		//return ProsjektDAO.findParticipants(this.id);
	};
	
	
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
	 * @return the beskrivelse
	 */
	public String getBeskrivelse() {
		return beskrivelse;
	}
	/**
	 * @param beskrivelse the beskrivelse to set
	 */
	public void setBeskrivelse(String beskrivelse) {
		this.beskrivelse = beskrivelse;
	}
	
	/**
	 * 
	 */
	
	public String toString() {
		return "Prosjekt [id=" + id + ", navn=" + navn + ", beskrivelse=" + beskrivelse + "]";
	}
}
