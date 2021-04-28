package it.polimi.tiw.docente;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;

import it.polimi.tiw.beans.Appello;
import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Docente;
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.beans.Verbale;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.common.ThymeleafInstance;
import it.polimi.tiw.dao.AppelliDAO;
import it.polimi.tiw.dao.DocentiDAO;

/**
 * Servlet implementation class GoToVerbalizzazione
 */
@WebServlet("/Verbalizzazione")
public class GoToVerbalizzazione extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine; //required thymeleaf
	private Connection connection = null; //required connection to db
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToVerbalizzazione() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
    	
    	//Credo la connessione con il database
    	connection = ConnectionHandler.getConnection(getServletContext());
    	//Istanzio il mio template engine per questa pagina
    	templateEngine = new ThymeleafInstance(getServletContext()).getTemplateEngine();

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Docente docente = (Docente) request.getSession().getAttribute("docente");
		String id_appello_string = request.getParameter("id");
		Integer id_appello = null;
		Message errorMessage = new Message();
		Message successMessage = new Message();
		AppelliDAO appelliDao = new AppelliDAO(connection);
		DocentiDAO docentiDao = new DocentiDAO(connection);
		Verbale verbale = null;
		errorMessage.setMessage("");
		successMessage.setMessage("");
		
		//controllo che sia arriavto un id appello corretto
		if(id_appello_string == null || id_appello_string.equals("")) {
			errorMessage.setMessage("Impossibile verbalizzare appello, l'appello selezionato non corrisponde a un appello valido");
		}else {
			try {
				id_appello = Integer.parseInt(id_appello_string);
			}catch(NumberFormatException e) {
				id_appello = null;
				errorMessage.setMessage("Impossibile verbalizzare appello, l'appello selezionato non corrisponde a un appello valido");
			}
		}
		
		//controllo che l'appello sia di un corso tenuto dal docente loggato 
		if(id_appello != null) {
			System.out.println("id appello letto in mdoo corretto");
			
			//devo controlalre che effettivamente l'id appartenga a un corso che è tenuto dal professore 

			Appello appello = appelliDao.getAppelloFromID(id_appello);
			ArrayList<Corso> corsiDocente = docentiDao.getCourseList(docente.getId_docente());
			boolean controllo = false;
			for(Corso c : corsiDocente) {
				if(c.getId_corso() == appello.getId_corso()) {
					controllo = true;
				}
			}
			
			
			if(controllo) {
				verbale = appelliDao.verbalizzaAppello(id_appello);
			}else {
				errorMessage.setMessage("Impossibile verbalizzare appello, l'appello selezionato non corrisponde a un appello valido");
			}

		}
		
		//manca da stampare la pagina del verbale e da fare il controllo che prima di verbalizzare ci siano effettivamente dei voti da verbalizzare e non siano vuoti 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
