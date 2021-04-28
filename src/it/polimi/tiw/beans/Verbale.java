package it.polimi.tiw.beans;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Verbale {
	
	private int id_verbale;
	private LocalDate data;
	private LocalTime ora;
	private int id_appello;
	private ArrayList<IscrittiAppello> iscrittiVerbalizati;
	
	public Verbale(int id, LocalDate data, LocalTime ora, int id_appello, ArrayList<IscrittiAppello> iscrittiVerbalizzati) {
		this.id_verbale = id;
		this.data = data;
		this.ora = ora;
		this.id_appello = id_appello;
		this.iscrittiVerbalizati = iscrittiVerbalizzati;
	}

	public int getId_verbale() {
		return id_verbale;
	}

	public LocalDate getData() {
		return data;
	}

	public LocalTime getOra() {
		return ora;
	}

	public int getId_appello() {
		return id_appello;
	}

	public ArrayList<IscrittiAppello> getIscrittiVerbalizati() {
		return iscrittiVerbalizati;
	}
	
	
	
	
	

}
