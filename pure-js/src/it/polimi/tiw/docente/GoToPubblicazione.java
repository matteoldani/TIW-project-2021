package it.polimi.tiw.docente;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.Appello;
import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Docente;
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.dao.AppelliDAO;
import it.polimi.tiw.dao.DocentiDAO;

/**
 * Servlet implementation class GoToPubblicazione
 */
@WebServlet("/Pubblicazione")
@MultipartConfig
public class GoToPubblicazione extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null; //required connection to db
       
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToPubblicazione() {
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
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//in questa servlet vengono pubblicati i voti che sono nello stato inserito 
		
		String id_appello_string = request.getParameter("id");
		Integer id_appello = null;
		Message errorMessage = new Message();
		Message successMessage = new Message();
		Docente docente = (Docente) request.getSession().getAttribute("docente");
		AppelliDAO appelliDao;
		DocentiDAO docentiDao;
		
		errorMessage.setMessage("");
		try {
			appelliDao = new AppelliDAO(connection);
			docentiDao = new DocentiDAO(connection);
		}catch(SQLException e) {
			//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
			//in una pagine di errore generica (scleta migliore esteticamente) 
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
			errorMessage = new Message();
			errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
			response.getWriter().println("Errore interno al database. Riprova piu' tardi");
			return;
		}
		
		//controllo che sia arriavto un id appello corretto
		if(id_appello_string == null || id_appello_string.equals("")) {
			errorMessage.setMessage("Impossible processare i dati inseriti. Matricola, id appello e voto inseriti potrebbero non essere validi");
		}else {
			try {
				id_appello = Integer.parseInt(id_appello_string);
			}catch(NumberFormatException e) {
				id_appello = null;
				errorMessage.setMessage("Impossible processare i dati inseriti. Matricola, id appello e voto inseriti potrebbero non essere validi");
			}
		}
		
		//se l'appello ?? corretto verifico che sia del docente giusto
		
		if(id_appello != null) {

			//devo controlalre che effettivamente l'id appartenga a un corso che ?? tenuto dal professore 
			docente = (Docente) request.getSession(false).getAttribute("docente");
			Appello appello = null;
			ArrayList<Corso> corsiDocente = null;
			try {
				appello = appelliDao.getAppelloFromID(id_appello);
				corsiDocente = docentiDao.getListaCorsi(docente.getId_docente());
			} catch (SQLException e) {
				//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
				//in una pagine di errore generica (scleta migliore esteticamente) 
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
				errorMessage = new Message();
				errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
				response.getWriter().println("Errore interno al database. Riprova piu' tardi");
				return;
			}
			boolean controllo = false;
			if(appello == null) {
				errorMessage.setMessage("appello non trovato nel database");
			}else{
				
				for(Corso c : corsiDocente) {
					if(c.getId_corso() == appello.getId_corso()) {
						controllo = true;
					}
				}
			}
			
			
			
			if(!controllo) {
				errorMessage.setMessage("Non puoi accedere ai dati di questo appello");
			}

		}
		
		//se non ci sono errori allora pubblico 
		if(errorMessage.getMessage().equals("")) {
			boolean ris;
			try {
				ris = appelliDao.pubblicaVoti(id_appello);
			} catch (SQLException e) {
				//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
				//in una pagine di errore generica (scleta migliore esteticamente) 
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
				errorMessage = new Message();
				errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
				response.getWriter().println("Errore interno al database. Riprova piu' tardi");
				return;
			}
			
			response.getWriter().println("Pubblicazione effettuata con successo");
			
		}else {
			//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
			//in una pagine di errore generica (scleta migliore esteticamente) 
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
			errorMessage = new Message();
			errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
			response.getWriter().println("Errore interno al database. Riprova piu' tardi");
			return;
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
