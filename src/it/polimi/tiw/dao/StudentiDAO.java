package it.polimi.tiw.dao;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Studente;

public class StudentiDAO {
	
	private Connection connection;
	
	public StudentiDAO(Connection connection) throws SQLException {
		this.connection = connection;
		if(connection == null) {
			throw new SQLException();
		}
	}
	
	public Studente checkCredentials(String matricola, String password) throws SQLException {
		Studente studente = null;
		
		String query = "SELECT * FROM studenti WHERE matricola = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		String tempMatricola, tempPass, salt;
		
		String base64Hash = null;
		byte[] hashByte, saltByte;

	
		pstatement = connection.prepareStatement(query);
		pstatement.setString(1, matricola);
		
		result = pstatement.executeQuery();
		
		while(result.next()) {
			tempMatricola = result.getString("matricola");
			tempPass = result.getString("password");
			salt = result.getString("salt");
			
			KeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), 65536, 256);
			try {
				SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
				//generats the hash
				hashByte = factory.generateSecret(spec).getEncoded();
				
				//Need to transform it to a readable string to be saved in my db 
				base64Hash = Base64.getEncoder().encodeToString(hashByte);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(matricola.equals(tempMatricola) && base64Hash.equals(tempPass)) {
				//create the beans related to a docente 
				studente = new Studente(result.getInt("matricola"), 
										result.getString("nome"),
										result.getString("cognome"), 
										result.getString("corso_laurea"),
										result.getString("email"));
			}
		}

		return studente;
	}

	public ArrayList<Corso> getListaCorsi(int matricola) throws SQLException {
		//per ogni corso a cui è iscritto lo stuente creo un Bean corso e lo aggiungo a una lista che return 
		ArrayList<Corso> listaCorsi = new ArrayList<>();
		String query = "SELECT * FROM iscritti_corso JOIN corsi ON iscritti_corso.id_corso = corsi.id_corso WHERE iscritti_corso.matricola = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		Corso c = null;
		

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
		

		
		return listaCorsi;
	}

}
