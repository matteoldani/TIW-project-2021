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
	
	public DocentiDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Docente checkCredential(String username, String password) {
		
		String query = "SELECT * FROM docenti WHERE username = ? AND password = ?";
		Docente docente = null;
		ResultSet result = null;
		PreparedStatement pstatement = null;
		String tempUser, tempPass;
		
		try {
			
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return docente;
	}
	
	public ArrayList<Corso> getCourseList(int id_docente){
		
		String query = "SELECT * FROM corsi WHERE id_docente = ? ORDER BY nome DESC";
		Corso c = null;
		ArrayList<Corso> listaCorsi = null;
		ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
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
			
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listaCorsi;
		
	}
}
