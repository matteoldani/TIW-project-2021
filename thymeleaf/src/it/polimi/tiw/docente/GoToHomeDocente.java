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
import it.polimi.tiw.beans.Message;
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
		DocentiDAO docentiDao;
		CorsiDAO corsiDao;
		ArrayList<Corso> corsiDocente;
		ArrayList<Appello> appelliCorso = null;
		Corso c = null;
		//if the request has an id i have to get also the date of the exams for that course 
		String id = request.getParameter("id");
		Integer selectedCourse;
		Message errorMessage = null;
		
		errorMessage = new Message();
		try {
			corsiDao = new CorsiDAO(connection);
			docentiDao = new DocentiDAO(connection);
			corsiDocente = docentiDao.getListaCorsi(docente.getId_docente());
		} catch (SQLException e1) {
			//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
			//in una pagine di errore generica (scleta migliore esteticamente) 
			e1.printStackTrace();
			errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
			request.getSession().setAttribute("errorMessage", errorMessage);
			String path = getServletContext().getContextPath() + "/ErrorPage";
			response.sendRedirect(path);
			return;
		}
		
		/*
		 * Controlli effettuati sul parametro id per evitare errori: 
		 * 1) se null non devo far vedere nessun appello 
		 * 2) se vuoto mostro errore 
		 * 3) se non ?? un corso di quelli che il professore pu?? vedere mostro errore 
		 * 4) se non ?? un numero mostro errore
		 */
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
			System.out.println("letto l'id in mdo corretto");
			//i need to get the dates 
			for(Corso corso : corsiDocente) {
				if(corso.getId_corso() == selectedCourse) {
					c = corso;
				}
			}
			if(c != null) {
				//vedre se si pu?? prendere l'id in modo pi?? comodo
				try {
					appelliCorso = corsiDao.getAppelliCorso(c.getId_corso());
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
			}else {
				errorMessage.setMessage("Il corso selezionato non e' disponibile");
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
		
		//se c'?? un errore lo stampo a inizio pagina
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
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
