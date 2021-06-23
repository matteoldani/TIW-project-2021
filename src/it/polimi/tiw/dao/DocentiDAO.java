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
import it.polimi.tiw.beans.Docente;

public class DocentiDAO {
	
	private Connection connection;
	
	public DocentiDAO(Connection connection) throws SQLException {
		this.connection = connection;
		if(connection == null) {
			throw new SQLException();
		}
	}
	
	public Docente checkCredentials(String username, String password) throws SQLException {
		
		String query = "SELECT * FROM docenti WHERE username = ?";
		Docente docente = null;
		ResultSet result = null;
		PreparedStatement pstatement = null;
		String tempUser, tempPass, salt;
		String base64Hash = null;
		byte[] hashByte, saltByte;
		
			
		pstatement = connection.prepareStatement(query);
		pstatement.setString(1, username);
		result = pstatement.executeQuery();
		
		
		while(result.next()) {
			tempUser = result.getString("username");
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
			
			if(username.equals(tempUser) && base64Hash.equals(tempPass)) {
				//create the beans related to a docente 
				docente = new Docente(result.getInt("id_docente"), 
										result.getString("nome"),
										result.getString("cognome"), 
										tempUser);
			}
		}


		return docente;
	}
	
	public ArrayList<Corso> getListaCorsi(int id_docente) throws SQLException{
		
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
