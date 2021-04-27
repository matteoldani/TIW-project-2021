package it.polimi.tiw.dao;

import java.sql.Connection;
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
	public ArrayList<IscrittiAppello> getIscrittiAppello(int id_appello, String tag, String order){
		
		Appello app = null;
		ArrayList<IscrittiAppello> list = new ArrayList<>();
		String query = "SELECT * FROM iscritti_appello JOIN studenti ON iscritti_appello.matricola = studenti.matricola WHERE iscritti_appello.id_appello = ? ORDER BY studenti.matricola ASC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		
		
		//eseguo query per prendere l'appello giusto
		app = getAppelloFromID(id_appello);
		
		
		//eseguo query che costruisce la lista di iscritti all'appello
		try {
			switch(tag) {
			case "matricola" : 
				if(order.equals("ASC")) {
					query = "SELECT * FROM iscritti_appello JOIN studenti ON iscritti_appello.matricola = studenti.matricola WHERE iscritti_appello.id_appello = ? ORDER BY studenti.matricola ASC";
				}else {
					query = "SELECT * FROM iscritti_appello JOIN studenti ON iscritti_appello.matricola = studenti.matricola WHERE iscritti_appello.id_appello = ? ORDER BY studenti.matricola DESC";
				}
				break;
			case "cognome" : 
				if(order.equals("ASC")) {
					query = "SELECT * FROM iscritti_appello JOIN studenti ON iscritti_appello.matricola = studenti.matricola WHERE iscritti_appello.id_appello = ? ORDER BY studenti.cognome ASC";
				}else {
					query = "SELECT * FROM iscritti_appello JOIN studenti ON iscritti_appello.matricola = studenti.matricola WHERE iscritti_appello.id_appello = ? ORDER BY studenti.cognome DESC";
				}
				break;
			case "nome" : 
				if(order.equals("ASC")) {
					query = "SELECT * FROM iscritti_appello JOIN studenti ON iscritti_appello.matricola = studenti.matricola WHERE iscritti_appello.id_appello = ? ORDER BY studenti.nome ASC";
				}else {
					query = "SELECT * FROM iscritti_appello JOIN studenti ON iscritti_appello.matricola = studenti.matricola WHERE iscritti_appello.id_appello = ? ORDER BY studenti.nome DESC";
				}
				break;
			case "email" : 
				if(order.equals("ASC")) {
					query = "SELECT * FROM iscritti_appello JOIN studenti ON iscritti_appello.matricola = studenti.matricola WHERE iscritti_appello.id_appello = ? ORDER BY studenti.email ASC";
				}else {
					query = "SELECT * FROM iscritti_appello JOIN studenti ON iscritti_appello.matricola = studenti.matricola WHERE iscritti_appello.id_appello = ? ORDER BY studenti.email DESC";
				}
				break;
			case "corso_laurea" : 
				if(order.equals("ASC")) {
					query = "SELECT * FROM iscritti_appello JOIN studenti ON iscritti_appello.matricola = studenti.matricola WHERE iscritti_appello.id_appello = ? ORDER BY studenti.corso_laurea ASC";
				}else {
					query = "SELECT * FROM iscritti_appello JOIN studenti ON iscritti_appello.matricola = studenti.matricola WHERE iscritti_appello.id_appello = ? ORDER BY studenti.corso_laurea DESC";
				}
				break;
			case "voto" : 
				if(order.equals("ASC")) {
					query = "SELECT * FROM iscritti_appello JOIN studenti ON iscritti_appello.matricola = studenti.matricola WHERE iscritti_appello.id_appello = ? ORDER BY iscritti_appello.voto ASC";
				}else {
					query = "SELECT * FROM iscritti_appello JOIN studenti ON iscritti_appello.matricola = studenti.matricola WHERE iscritti_appello.id_appello = ? ORDER BY iscritti_appello.voto DESC";
				}
				break;
			case "stato" : 
				if(order.equals("ASC")) {
					query = "SELECT * FROM iscritti_appello JOIN studenti ON iscritti_appello.matricola = studenti.matricola WHERE iscritti_appello.id_appello = ? ORDER BY iscritti_appello.stato ASC";
				}else {
					query = "SELECT * FROM iscritti_appello JOIN studenti ON iscritti_appello.matricola = studenti.matricola WHERE iscritti_appello.id_appello = ? ORDER BY iscritti_appello.stato DESC";
				}
				break;
				
			
			}
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, String.valueOf(id_appello));
			
			result = pstatement.executeQuery();
			System.out.println(result);
			
			Studente studente;
			IscrittiAppello iscrittiAppello;
			String votoDefinitivo;
			int votoLetto;
			
			while(result.next()) {
				studente = new Studente(result.getInt("matricola"),
										result.getString("nome"),
										result.getString("cognome"),
										result.getString("corso_laurea"),
										result.getString("email"));
				
				votoLetto = result.getInt("voto");
				switch(votoLetto) { //vuoto = -15, assente = -10, riprovato = -5, rimandato = 0, normali, lode = 33
					case -15: votoDefinitivo = "-"; break;
					case -10: votoDefinitivo = "Assente"; break;
					case -5: votoDefinitivo = "Riprovato"; break;
					case 0: votoDefinitivo = "Rimandato"; break;
					case 33: votoDefinitivo = "30 e Lode"; break;
					default: 
						if(votoLetto >= 18 && votoLetto<= 30) {
							votoDefinitivo = String.valueOf(votoLetto);
						}else {
							votoDefinitivo = "Errore voto";
						}
						break;
				}
				iscrittiAppello = new IscrittiAppello(studente, 
													votoDefinitivo,
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
	
	public IscrittiAppello getIscrittoAppello(int id_appello, int matricola) {
		IscrittiAppello ia = null;
		Appello app = null;
		
		String query = "SELECT * FROM iscritti_appello JOIN studenti ON iscritti_appello.matricola = studenti.matricola WHERE iscritti_appello.id_appello = ? AND iscritti_appello.matricola = ? ";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		
		//eseguo query per prendere l'appello giusto
		app = getAppelloFromID(id_appello);
		

		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, id_appello);
			pstatement.setInt(2, matricola);
			result = pstatement.executeQuery();
			
			Studente studente;
			IscrittiAppello iscrittiAppello;
			String votoDefinitivo;
			int votoLetto;
			
			while(result.next()) {
				studente = new Studente(result.getInt("matricola"),
										result.getString("nome"),
										result.getString("cognome"),
										result.getString("corso_laurea"),
										result.getString("email"));
				
				votoLetto = result.getInt("voto");
				switch(votoLetto) { //vuoto = -15, assente = -10, riprovato = -5, rimandato = 0, normali, lode = 33
					case -15: votoDefinitivo = "-"; break;
					case -10: votoDefinitivo = "Assente"; break;
					case -5: votoDefinitivo = "Riprovato"; break;
					case 0: votoDefinitivo = "Rimandato"; break;
					case 33: votoDefinitivo = "30 e Lode"; break;
					default: 
						if(votoLetto >= 18 && votoLetto<= 30) {
							votoDefinitivo = String.valueOf(votoLetto);
						}else {
							votoDefinitivo = "Errore voto";
						}
						break;
				}
				ia = new IscrittiAppello(studente, 
													votoDefinitivo,
													result.getString("stato"),
													app);
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ia;
	}

	public boolean updateVoto(Integer matricola, Integer id_appello, String voto) {
		// TODO Auto-generated method stub
		
		String query = "UPDATE iscritti_appello SET voto = ?, stato = 'inserito' WHERE matricola = ? AND id_appello = ? ";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			switch(voto) {
				case "assente": pstatement.setInt(1, -10); break;
				case "rimandato": pstatement.setInt(1, 0); break;
				case "riprovato": pstatement.setInt(1, -5); break;
				case "lode": pstatement.setInt(1, 33); break;
				default: 
					pstatement.setInt(1, Integer.parseInt(voto)); break;

			}
			pstatement.setInt(2, matricola);
			pstatement.setInt(3, id_appello);
			System.out.println(pstatement);
			pstatement.executeUpdate();
			return true;
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public boolean pubblicaVoti(Integer id_appello) {
		
		String query = "UPDATE iscritti_appello SET stato = 'pubblicato' WHERE stato = 'inserito' AND id_appello = ? ";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, id_appello);
			System.out.println(pstatement);
			pstatement.executeUpdate();
			return true;
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
