package it.polimi.tiw.beans;

import java.sql.Date;

public class Appello {
	
	private int id_appello;
	private Date data;
	private int id_corso;
	
	public Appello(int id_appello, Date data, int id_corso) {
		this.id_appello = id_appello;
		this.data = data;
		this.id_corso = id_corso;
	}

	public int getId_appello() {
		return id_appello;
	}

	public Date getData() {
		return data;
	}

	public int getId_corso() {
		return id_corso;
	}
	
	
	
	
}
