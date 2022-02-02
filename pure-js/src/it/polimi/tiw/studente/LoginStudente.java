package it.polimi.tiw.studente;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.Message;
import it.polimi.tiw.beans.Studente;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.dao.StudentiDAO;

/**
 * Servlet implementation class login
 */
@WebServlet("/LoginStudente")
@MultipartConfig
public class LoginStudente extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null; //required connection to db
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginStudente() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
    	//Credo la connessione con il database
    	connection = ConnectionHandler.getConnection(getServletContext());

    }


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		/*create the dao and check the credential, if credentials are accepted the corresponded bean is returned:
		 * -add info about the user
		 * -go to the home page 
		 * 
		 * if null is returned 
		 * -set the error message
		 * -reload the login page with the error message (with the redirect to the get)
		 */
		
		String matricola = null;
		String password = null;
		StudentiDAO studentiDao;
		Message errorMessage = new Message();
		Studente studente = null;
		
		//get the param from the request
		matricola = request.getParameter("username");
		password = request.getParameter("password");
		try {
			studentiDao = new StudentiDAO(this.connection);
			studente = studentiDao.checkCredentials(matricola, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
			//in una pagine di errore generica (scleta migliore esteticamente) 
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
			return;
		}
		if(studente != null) {
			//login succeed
			if(request.getSession().getAttribute("docente") != null) {
				//se c'è un docente lo rimuovo forzando il logout
				request.getSession().removeAttribute("docente");
			}
			request.getSession().setAttribute("studente", studente);
			response.getWriter().println(studente.getCognome());
			response.setStatus(200);
		}else {
			//login failed
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	

		}
	}

}
