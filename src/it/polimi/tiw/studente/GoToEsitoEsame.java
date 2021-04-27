package it.polimi.tiw.studente;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;

import it.polimi.tiw.beans.IscrittiAppello;
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.beans.Studente;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.common.ThymeleafInstance;
import it.polimi.tiw.dao.AppelliDAO;

/**
 * Servlet implementation class GoToEsitoEsameù
 */
@WebServlet("/EsitoEsame")
public class GoToEsitoEsame extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine; //required thymeleaf
	private Connection connection = null; //required connection to db
       
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToEsitoEsame() {
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
		String id_appello_string = request.getParameter("id");
		Integer id_appello = null;
		Studente studente = (Studente) request.getSession().getAttribute("studente");
		Message errorMessage = new Message();
		errorMessage.setMessage("");
		
		//controllo che l'id sia un numero
		if(id_appello_string != null && !id_appello_string.equals("")) {
			try {
				id_appello = Integer.parseInt(id_appello_string);
			}catch(NumberFormatException e) {
				id_appello = null;
				errorMessage.setMessage("L'appello selezionato non è corretto");
			}
			
		}else{
			id_appello = null;
			if(id_appello_string != null && id_appello_string.equals("")) {
				errorMessage.setMessage("L'appello selezionato non è corretto");
			}
		}
		
		//verifico che la matricola sia iscritta all'appello 
		if(id_appello != null) {
			AppelliDAO appelliDao = new AppelliDAO(connection);
			IscrittiAppello iscritto = null;
			iscritto = appelliDao.getIscrittoAppello(id_appello, studente.getMatricola());
			
			if(iscritto == null) {
				errorMessage.setMessage("Non sei iscritto all'appello selezionato");
			}
		}
		
		//se l'erore è ancora vuoto allora prendo il voto
		if(errorMessage.getMessage().equals("")) {
			//posso settare le cose per i voti come singole variabili boolean 
			//posso settare un bean capace di gestire i voti
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
