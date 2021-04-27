package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Studente;

public class StudentiDAO {
	
	private Connection connection;
	
	public StudentiDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Studente checkCredentials(String matricola, String password) {
		Studente studente = null;
		
		String query = "SELECT * FROM studenti WHERE matricola = ? AND password = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		String tempMatricola, tempPass;
		
		try {
			
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, matricola);
			pstatement.setString(2, password);
			result = pstatement.executeQuery();
			
			while(result.next()) {
				tempMatricola = result.getString("matricola");
				tempPass = result.getString("password");
				if(matricola.equals(tempMatricola) && password.equals(tempPass)) {
					//create the beans related to a docente 
					studente = new Studente(result.getInt("matricola"), 
											result.getString("nome"),
											result.getString("cognome"), 
											result.getString("corso_laurea"),
											result.getString("email"));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return studente;
	}

	public ArrayList<Corso> getCourseList(int matricola) {
		//per ogni corso a cui è iscritto lo stuente creo un Bean corso e lo aggiungo a una lista che return 
		ArrayList<Corso> listaCorsi = new ArrayList<>();
		String query = "SELECT * FROM iscritti_corso JOIN corsi ON iscritti_corso.id_corso = corsi.id_corso WHERE iscritti_corso.matricola = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		Corso c = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, matricola);
			result = pstatement.executeQuery();
			if(result != null) {
				//otherwise i have to return null
				listaCorsi = new ArrayList<>();
			}
			
			while(result.next()) {
				c = new Corso(result.getInt("id_corso"), result.getString("nome"), result.getString("descrizione"), result.getInt("id_docente"));
				listaCorsi.add(c);
			}
			
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listaCorsi;
	}

}
