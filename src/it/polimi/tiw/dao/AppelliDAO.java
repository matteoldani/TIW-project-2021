package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.beans.Appello;
import it.polimi.tiw.beans.IscrittiAppello;
import it.polimi.tiw.beans.Studente;

public class AppelliDAO {
	
private Connection connection;
	
	public AppelliDAO(Connection connection) {
		this.connection = connection;
	}
	
	//return the list of exams scheduled for a course
	public ArrayList<IscrittiAppello> getIscrittiAppello(int id_appello){
		
		Appello app = null;
		ArrayList<IscrittiAppello> list = new ArrayList<>();
		String query = "SELECT * FROM iscritti_appello JOIN studenti ON iscritti_appello.matricola = studenti.matricola WHERE iscritti_appello.id_appello = ? ";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		
		//eseguo query per prendere l'appello giusto
		app = getAppelloFromID(id_appello);
		
		
		//eseguo query che costruisce la lista di iscritti all'appello
		try {
			
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, String.valueOf(id_appello));
	
			result = pstatement.executeQuery();
			Studente studente;
			IscrittiAppello iscrittiAppello;
			while(result.next()) {
				studente = new Studente(result.getInt("matricola"),
										result.getString("nome"),
										result.getString("cognome"),
										result.getString("corso_laurea"));
				iscrittiAppello = new IscrittiAppello(studente, 
													result.getInt("voto"),
													result.getString("stato"),
													app);
				
				
				list.add(iscrittiAppello);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return list;
	}

	public Appello getAppelloFromID(int id_appello) {
		
		Appello appello = null;
		
		String queryAppello = "SELECT * FROM appelli WHERE id_appello = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		
		//eseguo query per prendere l'appello giusto
		try {
			pstatement = connection.prepareStatement(queryAppello);
			pstatement.setString(1, String.valueOf(id_appello));
	
			result = pstatement.executeQuery();
			
			while(result.next()) {
				appello = new Appello(id_appello, result.getDate("data"), result.getInt("id_corso"));
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		
		return appello;
	}
}
