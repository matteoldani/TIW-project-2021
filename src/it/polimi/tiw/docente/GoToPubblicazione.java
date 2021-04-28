package it.polimi.tiw.docente;

import java.io.IOException;
import java.sql.Connection;
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
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.common.ThymeleafInstance;
import it.polimi.tiw.dao.AppelliDAO;
import it.polimi.tiw.dao.DocentiDAO;

/**
 * Servlet implementation class GoToPubblicazione
 */
@WebServlet("/Pubblicazione")
public class GoToPubblicazione extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine; //required thymeleaf
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
    	//Istanzio il mio template engine per questa pagina
    	templateEngine = new ThymeleafInstance(getServletContext()).getTemplateEngine();

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
		AppelliDAO appelliDao = new AppelliDAO(connection);
		DocentiDAO docentiDao = new DocentiDAO(connection);
		
		errorMessage.setMessage("");
		
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
		
		//se l'appello è corretto verifico che sia del docente giusto
		
		if(id_appello != null) {
			System.out.println("id appello letto in mdoo corretto");
			
			//devo controlalre che effettivamente l'id appartenga a un corso che è tenuto dal professore 
			docente = (Docente) request.getSession(false).getAttribute("docente");
			Appello appello = appelliDao.getAppelloFromID(id_appello);
			ArrayList<Corso> corsiDocente = docentiDao.getCourseList(docente.getId_docente());
			boolean controllo = false;
			for(Corso c : corsiDocente) {
				if(c.getId_corso() == appello.getId_corso()) {
					controllo = true;
				}
			}
			
			
			if(!controllo) {
				errorMessage.setMessage("Non puoi accedere ai dati di questo appello");
			}

		}
		
		//se non ci sono errori allora pubblico 
		if(errorMessage.getMessage().equals("")) {
			boolean ris;
			ris = appelliDao.pubblicaVoti(id_appello);
			
			if(ris) {
				successMessage.setMessage("Pubblicazione effettuata con successo");
				request.setAttribute("successMessage", successMessage);
				String path = getServletContext().getContextPath() + "/IscrittiAppello?id=" + id_appello;
				response.sendRedirect(path);
			}else {
	
				String path = "WEB-INF/errorPage.html";
				
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				
				
				//se c'è un errore lo stampo a inizio pagina
				ctx.setVariable("errorMessage", errorMessage);

				templateEngine.process(path, ctx, response.getWriter());
				
			}
		}

	}

}
