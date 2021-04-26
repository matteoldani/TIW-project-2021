package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.beans.Appello;
import it.polimi.tiw.beans.Corso;

public class CorsiDAO {
	
	private Connection connection;
	
	public CorsiDAO(Connection connection) {
		this.connection = connection;
	}
	
	//return the list of exams scheduled for a course
	public ArrayList<Appello> getAppelliCorso(int id_corso){
		
		ArrayList<Appello> list = new ArrayList<>();
		String query = "SELECT * FROM appelli JOIN corsi ON appelli.id_corso = corsi.id_corso WHERE corsi.id_corso = ?  ORDER BY data DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		
		int id_appello;
		Date data; 
		
		
		try {
			
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, String.valueOf(id_corso));
	
			result = pstatement.executeQuery();
			
			while(result.next()) {
				id_appello = result.getInt("id_appello");
				data = result.getDate("data");
				Appello app = new Appello(id_appello, data, id_corso);
				list.add(app);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return list;
	}
	
	public Corso getCorsoFromId(int id_corso) {
		
		Corso corso = null;
		
		String queryCorso = "SELECT * FROM corsi WHERE id_corso = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		
		//eseguo query per prendere l'appello giusto
		try {
			pstatement = connection.prepareStatement(queryCorso);
			pstatement.setString(1, String.valueOf(id_corso));
	
			result = pstatement.executeQuery();
			while(result.next()) {
				corso = new Corso(id_corso, result.getString("nome"), result.getString("descrizione"),  result.getInt("id_docente"));
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		
		return corso;
		
		
	}

}
