package it.polimi.tiw.beans;

public class IscrittiAppello {
	
	private Studente studente;
	private String voto;
	private String stato;
	private Appello appello;
	
	public IscrittiAppello(Studente studente, String voto, String stato, Appello appello) {
		
		this.studente = studente;
		this.voto = voto;
		this.stato = stato;
		this.appello = appello;
	}

	public Studente getStudente() {
		return studente;
	}

	public String getVoto() {
		return voto;
	}

	public String getStato() {
		return stato;
	}

	public Appello getAppello() {
		return appello;
	}
	
	public void setStato(String stato) {
		this.stato = stato;
	}
	
	
	
	

}
