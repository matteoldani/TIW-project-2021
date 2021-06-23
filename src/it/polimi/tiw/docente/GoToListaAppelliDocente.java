package it.polimi.tiw.docente;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.tiw.beans.Appello;
import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Docente;
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.dao.CorsiDAO;
import it.polimi.tiw.dao.DocentiDAO;

/**
 * Servlet implementation class GoToListaAppelli
 */
@WebServlet("/ListaAppelliDocente")
public class GoToListaAppelliDocente extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null; //required connection to db
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToListaAppelliDocente() {
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
		Docente docente = (Docente) request.getSession(false).getAttribute("docente");
		DocentiDAO docentiDao;
		CorsiDAO corsiDao;
		ArrayList<Corso> corsiDocente;
		ArrayList<Appello> appelliCorso = null;
		Corso c = null;
		//if the request has an id i have to get also the date of the exams for that course 
		String id = request.getParameter("id");
		Integer selectedCourse;
		Message errorMessage = null;
		
		try {
			corsiDao = new CorsiDAO(connection);
			docentiDao = new DocentiDAO(connection);
			corsiDocente = docentiDao.getListaCorsi(docente.getId_docente());
		} catch (SQLException e1) {
			//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
			//in una pagine di errore generica (scleta migliore esteticamente) 
			e1.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
			errorMessage = new Message();
			errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
			response.getWriter().println("Errore interno al database. Riprova piu' tardi");
			return;
		}
		
		
		if(id != null && !id.equals("")) {
			try {
				selectedCourse = Integer.parseInt(id);
			}catch(NumberFormatException e) {
				selectedCourse = null;
				errorMessage = new Message();
				errorMessage.setMessage("Il corso selezionato non e' disponibile");
			}
			
		}else{
			selectedCourse = null;
			if(id != null && id.equals("")) {
				errorMessage = new Message();
				errorMessage.setMessage("Il corso selezionato non e' disponibile");
			}
		}
		
		if(selectedCourse != null) {
			System.out.println("letto l'id in mdo corretto");
			//i need to get the dates 
			for(Corso corso : corsiDocente) {
				if(corso.getId_corso() == selectedCourse) {
					c = corso;
				}
			}
			if(c != null) {
				//vedre se si può prendere l'id in modo più comodo
				try {
					appelliCorso = corsiDao.getAppelliCorso(c.getId_corso());
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
			}else {
				errorMessage = new Message();
				errorMessage.setMessage("Il corso selezionato non e' disponibile");
			}
			
			if(errorMessage == null) {
				String appelliJson = new Gson().toJson(appelliCorso);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(appelliJson);
			}else {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println(errorMessage.getMessage());	
			}
			
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
