package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DocentiDAO {
	
	private Connection connection;
	private String username, password;
	
	public DocentiDAO(Connection connection) {
		this.connection = connection;
	}
	
	public boolean checkCredential(String username, String password) {
		
		String query = "SELECT mail,password FROM docenti WHERE mail = ? AND password = ?";

		ResultSet result = null;
		PreparedStatement pstatement = null;
		String tempUser, tempPass;
		
		try {
			
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			result = pstatement.executeQuery();
			
			while(result.next()) {
				tempUser = result.getString("mail");
				tempPass = result.getString("password");
				if(username.equals(tempUser) && password.equals(tempPass)) {
					
					//here i can create the bean to have all the information about the docente but now 
					//is not clear if it could be useful, probably not.
					return true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return false;
	}
}
