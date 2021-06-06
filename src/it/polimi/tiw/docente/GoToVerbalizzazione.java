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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import it.polimi.tiw.beans.Appello;
import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Docente;
import it.polimi.tiw.beans.IscrittiAppello;
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.beans.Verbale;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.dao.AppelliDAO;
import it.polimi.tiw.dao.CorsiDAO;
import it.polimi.tiw.dao.DocentiDAO;

/**
 * Servlet implementation class GoToVerbalizzazione
 */
@WebServlet("/Verbalizzazione")
public class GoToVerbalizzazione extends HttpServlet {
	private static final long serialVersionUID = 1L;
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
		AppelliDAO appelliDao;
		DocentiDAO docentiDao;
		CorsiDAO corsiDao;
		Verbale verbale = null;
		Corso corso = null;
		Appello appello = null;
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

			
			ArrayList<Corso> corsiDocente;

			try {
				appelliDao = new AppelliDAO(connection);
				docentiDao = new DocentiDAO(connection);
				corsiDao = new CorsiDAO(connection);
				appello = appelliDao.getAppelloFromID(id_appello);
				corsiDocente = docentiDao.getCourseList(docente.getId_docente());
				
				boolean controllo = false;
				if(appello == null) {
					errorMessage.setMessage("appello non trovato nel database");
				}else{
					
					for(Corso c : corsiDocente) {
						if(c.getId_corso() == appello.getId_corso()) {
							controllo = true;
							corso = corsiDao.getCorsoFromId(c.getId_corso());
						}
					}
				}
				
				
				if(controllo) {
					
					//devo controllare che esista qualcosa di verbalizzabile 
					boolean verbalizzabili = false;
					ArrayList<IscrittiAppello> iscrittiAppello;
					
					iscrittiAppello = appelliDao.getIscrittiAppello(id_appello, "", "");
					
					//controllo che tra quelli iscritti a questo appello almeno uno abbia come stato pubblicato o rifiutato
					for(IscrittiAppello ia: iscrittiAppello) {
						if(ia.getStato().equals("pubblicato") || ia.equals("rifiutato")) {
							verbalizzabili = true;
						}
					}
					if(verbalizzabili) {						
						verbale = appelliDao.verbalizzaAppello(id_appello);						
					}else {
						errorMessage.setMessage("non ci sono voti da verbalizzare nell'appello selezionato");
					}
				}else {
					errorMessage.setMessage("Impossibile verbalizzare appello, l'appello selezionato non corrisponde a un appello valido");
				}

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
		}
		if(errorMessage.getMessage().equals("")) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonElement verbaleJsonElement = gson.toJsonTree(verbale);
			verbaleJsonElement.getAsJsonObject().addProperty("corso", corso.getNome());
			verbaleJsonElement.getAsJsonObject().addProperty("data_appello", appello.getData().toString());
			verbaleJsonElement.getAsJsonObject().addProperty("id_appello", appello.getId_appello());

			String json = gson.toJson(verbaleJsonElement);
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);		
		}
		
		
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
