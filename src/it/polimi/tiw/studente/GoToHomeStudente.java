package it.polimi.tiw.studente;

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

import com.google.gson.Gson;

import it.polimi.tiw.beans.Appello;
import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.beans.Studente;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.dao.AppelliDAO;
import it.polimi.tiw.dao.CorsiDAO;
import it.polimi.tiw.dao.StudentiDAO;

/**
 * Servlet implementation class GoToHomeStudente
 */
@WebServlet("/HomeStudente")
@MultipartConfig
public class GoToHomeStudente extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null; //required connection to db
       
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToHomeStudente() {
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
		try {
			studentiDao = new StudentiDAO(connection);
			corsiDao = new CorsiDAO(connection);
			appelliDao = new AppelliDAO(connection);
			corsiStudente = studentiDao.getCourseList(studente.getMatricola());
			
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
		
		
		//devo creare i json dei corsi
		//devo settare il messaggio di errrore se presente 
		
		String courseJson = new Gson().toJson(corsiStudente);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(courseJson);
		
	}

	
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
