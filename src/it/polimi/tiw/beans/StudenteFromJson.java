package it.polimi.tiw.beans;

public class StudenteFromJson {
	
	Integer matricola;
	String voto;
	
	public StudenteFromJson(Integer matricola, String voto) {
		this.voto = voto;
		this.matricola = matricola;
	}

	public Integer getMatricola() {
		return matricola;
	}

	public void setMatricola(Integer matricola) {
		this.matricola = matricola;
	}

	public String getVoto() {
		return voto;
	}

	public void setVoto(String voto) {
		this.voto = voto;
	}
	
	

}
