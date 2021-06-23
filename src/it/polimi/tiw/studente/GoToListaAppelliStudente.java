package it.polimi.tiw.studente;

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
import it.polimi.tiw.beans.IscrittiAppello;
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.beans.Studente;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.dao.AppelliDAO;
import it.polimi.tiw.dao.CorsiDAO;
import it.polimi.tiw.dao.StudentiDAO;

/**
 * Servlet implementation class GoToListaAppelliStudente
 */
@WebServlet("/ListaAppelliStudente")
public class GoToListaAppelliStudente extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null; //required connection to db
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToListaAppelliStudente() {
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
		Studente studente = (Studente) request.getSession(false).getAttribute("studente");
		StudentiDAO studentiDao;
		CorsiDAO corsiDao;
		AppelliDAO appelliDao = null;
		ArrayList<Corso> corsiStudente;
		ArrayList<Appello> appelliCorso = null;
		Corso c = null;
		//if the request has an id i have to get also the date of the exams for that course 
		String id = request.getParameter("id");
		Integer selectedCourse;
		Message errorMessage = null;
		
		errorMessage = new Message();
		errorMessage.setMessage("");
		try {
			studentiDao = new StudentiDAO(connection);
			corsiDao = new CorsiDAO(connection);
			appelliDao = new AppelliDAO(connection);
			corsiStudente = studentiDao.getListaCorsi(studente.getMatricola());
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
			//in una pagine di errore generica (scleta migliore esteticamente) 
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
				errorMessage.setMessage("Il corso selezionato non e' disponibile");
			}
			
		}else{
			selectedCourse = null;
			if(id != null && id.equals("")) {
				errorMessage.setMessage("Il corso selezionato non e' disponibile");
			}
		}
		
		if(selectedCourse != null) {
			//i need to get the dates 
			for(Corso corso : corsiStudente) {
				if(corso.getId_corso() == selectedCourse) {
					c = corso;
				}
			}
			if(c != null) {
				//vedre se si può prendere l'id in modo più comodo
				try {
					appelliCorso = corsiDao.getAppelliCorso(c.getId_corso());
					System.out.println(appelliCorso);
					//devo selezionare solo gli appelli a cuui lo studente è iscritto 
					ArrayList<IscrittiAppello> ia;
					boolean check = false;
					for(int i =0; i<appelliCorso.size(); i++) {
						check = false;
						ia = appelliDao.getIscrittiAppello(appelliCorso.get(i).getId_appello());
						
						for(IscrittiAppello iscrApp: ia) {
							if(iscrApp.getStudente().getMatricola() == studente.getMatricola()) {
								check = true;
							}
						}
						if(!check) {
							appelliCorso.remove(i);
							i--;
						}
					}
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
					//in una pagine di errore generica (scleta migliore esteticamente) 
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
					errorMessage = new Message();
					errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
					response.getWriter().println("Errore interno al database. Riprova piu' tardi");
					return;
				}
			}else {
				errorMessage.setMessage("Il corso selezionato non e' disponibile");
			}
			
			if(errorMessage.getMessage().equals("")) {
				String appelliJson = new Gson().toJson(appelliCorso);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(appelliJson);
			}else {
				//bad request con messaggio di errore
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
