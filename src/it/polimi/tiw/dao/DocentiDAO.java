package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Docente;

public class DocentiDAO {
	
	private Connection connection;
	
	public DocentiDAO(Connection connection) throws SQLException {
		this.connection = connection;
		if(connection == null) {
			throw new SQLException();
		}
	}
	
	public Docente checkCredential(String username, String password) throws SQLException {
		
		String query = "SELECT * FROM docenti WHERE username = ? AND password = ?";
		Docente docente = null;
		ResultSet result = null;
		PreparedStatement pstatement = null;
		String tempUser, tempPass, salt;
		
		byte[] hashByte, saltByte;
		
			
		pstatement = connection.prepareStatement(query);
		pstatement.setString(1, username);
		pstatement.setString(2, password);
		result = pstatement.executeQuery();
		
		while(result.next()) {
			tempUser = result.getString("username");
			tempPass = result.getString("password");
			
			if(username.equals(tempUser) && password.equals(tempPass)) {
				//create the beans related to a docente 
				docente = new Docente(result.getInt("id_docente"), result.getString("nome"), result.getString("cognome"), tempUser);
			}
		}


		return docente;
	}
	
	public ArrayList<Corso> getCourseList(int id_docente) throws SQLException{
		
		String query = "SELECT * FROM corsi WHERE id_docente = ? ORDER BY nome DESC";
		Corso c = null;
		ArrayList<Corso> listaCorsi = null;
		ResultSet result = null;
		PreparedStatement pstatement = null;
		
		
		pstatement = connection.prepareStatement(query);
		pstatement.setInt(1, id_docente);
		result = pstatement.executeQuery();
		if(result != null) {
			//otherwise i have to return null
			listaCorsi = new ArrayList<>();
		}
		
		while(result.next()) {
			c = new Corso(result.getInt("id_corso"), result.getString("nome"), result.getString("descrizione"), result.getInt("id_docente"));
			listaCorsi.add(c);
		}
		
		
		
		return listaCorsi;
		
	}
}
