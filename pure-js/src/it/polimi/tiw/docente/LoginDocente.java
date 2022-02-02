package it.polimi.tiw.docente;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.Docente;
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.dao.DocentiDAO;

/**
 * Servlet implementation class login
 */
@WebServlet("/LoginDocente")
@MultipartConfig
public class LoginDocente extends HttpServlet {
	private static final long serialVersionUID = 2L;
	private Connection connection = null; //required connection to db
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginDocente() {
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
		 * -set the response to 403 
		 */
		
		String username = null;
		String password = null;
		DocentiDAO docenteDAO;
		Message error = new Message();
		Docente docente = null;
		
		//get the param from the request
		username = request.getParameter("username");
		password = request.getParameter("password");
		
		try {
			docenteDAO = new DocentiDAO(this.connection);
			docente = docenteDAO.checkCredentials(username, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//modificare il tipo di errore per migliore customizzazione e andare a modificare lo switch dentro login.js
			//TODO
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
			return;
		}
		if(docente != null) {
			//login succeed
			if(request.getSession().getAttribute("studente") != null) {
				//se c'è un docente lo rimuovo forzando il logout
				request.getSession().removeAttribute("studente");
			}
			request.getSession().setAttribute("docente", docente);
			response.getWriter().println(docente.getCognome());
			response.setStatus(200);
		}else {
			//login failed
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
		}
	
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
