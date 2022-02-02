package it.polimi.tiw.beans;

public class Docente {
	
	private int id_docente;
	private String nome;
	private String cognome;
	private String username;
	
	public Docente(int id, String nome, String cognome, String username) {
		this.id_docente = id;
		this.cognome = cognome; 
		this.nome = nome; 
		this.username = username;
	}
	
	public int getId_docente() {
		return id_docente;
	}
	public String getNome() {
		return nome;
	}
	public String getCognome() {
		return cognome;
	}
	public String getUsername() {
		return username;
	}
	
	

}
