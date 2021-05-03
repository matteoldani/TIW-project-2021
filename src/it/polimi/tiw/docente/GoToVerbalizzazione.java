package it.polimi.tiw.docente;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.beans.Appello;
import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Docente;
import it.polimi.tiw.beans.IscrittiAppello;
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
		AppelliDAO appelliDao;
		DocentiDAO docentiDao;
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
				appello = appelliDao.getAppelloFromID(id_appello);
				corsiDocente = docentiDao.getCourseList(docente.getId_docente());
				
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
				errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
				request.getSession().setAttribute("errorMessage", errorMessage);
				String path = getServletContext().getContextPath() + "/ErrorPage";
				response.sendRedirect(path);
				return;
			}
			
			
			
			
		}
		//path del template
		String path = "WEB-INF/verbale.html";
				
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		//verbale
		ctx.setVariable("verbale", verbale);
		
		//se c'è un errore lo stampo a inizio pagina
		ctx.setVariable("errorMessage", errorMessage);
		
		//mi serve il nome del corso
		if(corso != null) {
			ctx.setVariable("nomeCorso", corso.getNome());
		}
		
		
		//mi serve la data dell'appello
		if(appello != null){
			ctx.setVariable("dataAppello", appello.getData());
		}
		
		
		templateEngine.process(path, ctx, response.getWriter());
		//manca da stampare la pagina del verbale e da fare il controllo che prima di verbalizzare ci siano effettivamente dei voti da verbalizzare e non siano vuoti 
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
