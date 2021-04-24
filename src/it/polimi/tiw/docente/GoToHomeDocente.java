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
import it.polimi.tiw.beans.ErrorMessage;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.common.ThymeleafInstance;
import it.polimi.tiw.dao.CorsiDAO;
import it.polimi.tiw.dao.DocentiDAO;

/**
 * Servlet implementation class CourseList
 */
@WebServlet("/HomeDocente")
public class GoToHomeDocente extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine; //required thymeleaf
	private Connection connection = null; //required connection to db
       
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToHomeDocente() {
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
		
		Docente docente = (Docente) request.getSession(false).getAttribute("docente");
		DocentiDAO docentiDao = new DocentiDAO(connection);
		CorsiDAO corsiDao = new CorsiDAO(connection);
		ArrayList<Corso> corsiDocente = docentiDao.getCourseList(docente.getId_docente());
		ArrayList<Appello> appelliCorso = null;
		Corso c = null;
		//if the request has an id i have to get also the date of the exams for that course 
		String id = request.getParameter("id");
		Integer selectedCourse;
		ErrorMessage errorMessage = null;
		
		errorMessage = new ErrorMessage();
		
		/*
		 * Controlli effettuati sul parametro id per evitare errori: 
		 * 1) se null non devo far vedere nessun appello 
		 * 2) se vuoto mostro errore 
		 * 3) se non è un corso di quelli che il professore può vedere mostro errore 
		 * 4) se non è un numero mostro errore
		 */
		if(id != null && !id.equals("")) {
			try {
				selectedCourse = Integer.parseInt(id);
			}catch(NumberFormatException e) {
				selectedCourse = null;
				errorMessage.setError("Il corso selezionato non è disponibile");
			}
			
		}else{
			selectedCourse = null;
			if(id != null && id.equals("")) {
				errorMessage.setError("Il corso selezionato non è disponibile");
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
				appelliCorso = corsiDao.getAppelliCorso(c.getId_corso());
			}else {
				errorMessage.setError("Il corso selezionato non è disponibile");
			}
		}
		
		//path del template
		String path = "WEB-INF/home.html";
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		//elenco corsi
		ctx.setVariable("corsi", corsiDocente);
		//elenco appelli se corso selezionato
		ctx.setVariable("appelli", appelliCorso);
		//corso selezionto per gli appelli
		if(c != null) {
			ctx.setVariable("corsoSelezionato", c.getId_corso());
		}
		
		//se c'è un errore lo stampo a inizio pagina
		ctx.setVariable("errorMessage", errorMessage);

		templateEngine.process(path, ctx, response.getWriter());
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
