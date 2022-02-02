package it.polimi.tiw.beans;

public class Corso {
	
	private int id_corso;
	private String nome;
	private String descrizione;
	private int id_docente;
	
	public Corso(int id_corso, String nome, String descrizione, int id_docente) {
		this.id_corso = id_corso;
		this.nome = nome;
		this.descrizione = descrizione; 
		this.id_docente = id_docente;
	}

	public int getId_corso() {
		return id_corso;
	}

	public String getNome() {
		return nome;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public int getId_docente() {
		return id_docente;
	}
	
	

}
