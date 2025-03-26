package Entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import main.ProsjektDAO;
import main.StaticEMF;

@Entity
@Table(schema = "public")
public class Prosjekt {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // or AUTO
	private Integer id;
	
	private String navn;
	private String beskrivelse;
	
	
	public List<AnsattProsjektPivot> getDeltagere(){
		return ProsjektDAO.findParticipants(this.id);
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
