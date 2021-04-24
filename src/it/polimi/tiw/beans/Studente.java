package it.polimi.tiw.beans;

public class Studente {
	
	private int matricola;
	private String nome;
	private String cognome;
	private String corso_laurea;
	
	public Studente(int matricola, String nome, String cognome, String corso_laurea) {
		
		this.matricola = matricola;
		this.nome = nome;
		this.cognome = cognome;
		this.corso_laurea = corso_laurea;
	}

	public int getMatricola() {
		return matricola;
	}

	public String getNome() {
		return nome;
	}

	public String getCognome() {
		return cognome;
	}

	public String getCorso_laurea() {
		return corso_laurea;
	}
	
	
	
	

}
