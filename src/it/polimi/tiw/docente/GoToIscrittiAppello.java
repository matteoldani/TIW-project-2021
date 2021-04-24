package it.polimi.tiw.docente;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
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
import it.polimi.tiw.beans.IscrittiAppello;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.common.ThymeleafInstance;
import it.polimi.tiw.dao.AppelliDAO;
import it.polimi.tiw.dao.CorsiDAO;
import it.polimi.tiw.dao.DocentiDAO;

/**
 * Servlet implementation class GoToIscrittiAppello
 */
@WebServlet("/IscrittiAppello")
public class GoToIscrittiAppello extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine; //required thymeleaf
	private Connection connection = null; //required connection to db
       
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToIscrittiAppello() {
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
		
		String id = request.getParameter("id");
		Integer id_appello = null;
		ErrorMessage errorMessage = new ErrorMessage();
		AppelliDAO appelliDao = new AppelliDAO(connection);
		ArrayList<IscrittiAppello> iscrittoAppello = null;
		Docente docente;
		DocentiDAO docentiDao = new DocentiDAO(connection);
		CorsiDAO corsiDao = new CorsiDAO(connection);
		String nomeCorso = null;
		Date dataAppello = null;
		String orderAttribute = null;
		String order;
		
		//cerco di prendere come oridinare, se null ordino per matricola
		if(request.getParameter("tag") != null) {
			//controllo che non sia qualcosa di sbagliato 
			String tag = request.getParameter("tag");
			switch(tag) {
				case "matricola": orderAttribute = tag; break;
				case "cognome" : orderAttribute = tag; break;
				case "nome" : orderAttribute = tag; break;
				case "email" : orderAttribute = tag; break; // need to add this field to databse 
				case "corso_laurea" : orderAttribute = tag; break;
				case "voto" : orderAttribute = tag; break; 
				case "stato" : orderAttribute = tag; break; 
				default: orderAttribute = "matricola"; break;
			}
		}else {
			orderAttribute = "matricola";
		}
		
		//cerco di prendere l'ordine di ordinameto, se null ordino in modo crescente
		if(request.getParameter("order") != null) {
			//controllo che il valore sia accettabile 
			String o = request.getParameter("order");
			switch(o) {
				case "DESC" : order = o; break;
				case "ASC" : order = o; break; 
				default : order = "ASC"; break;
			}
		}else {
			order = "ASC";
		}
		
		//sono arrivato a questa pagina senza avere un id
		if(id==null || id.equals("")) {
			errorMessage.setError("Non è stato selezioanto nessun appello");
		}
		
		//l'id non è un numero
		try {
			id_appello = Integer.parseInt(id);
		}catch(NumberFormatException e) {
			id_appello = null;
			errorMessage.setError("L'appello selezionato non è presente");
		}
		
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
					nomeCorso = c.getNome();
					dataAppello = appello.getData();
				}
			}
			
			
			if(controllo) {
				iscrittoAppello = appelliDao.getIscrittiAppello(id_appello, orderAttribute, order);
			}else {
				errorMessage.setError("Non puoi accedere ai dati di questo appello");
			}

		}
		
		//path del template
		String path = "WEB-INF/iscrittiAppello.html";
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		//elenco iscritti
		ctx.setVariable("iscritti", iscrittoAppello);
		//nome del corso
		ctx.setVariable("nomeCorso", nomeCorso);
		//id appello (per l'ordinamento) 
		ctx.setVariable("id_appello", id_appello);
		//data appello
		if(dataAppello != null) {
			ctx.setVariable("dataAppello", dataAppello.toString());
		}
		//se c'è un errore lo stampo a inizio pagina
		ctx.setVariable("errorMessage", errorMessage);
		//elemento per cui ho ordinato 
		ctx.setVariable("orderAttribute", orderAttribute);
		//come devo ordianre 
		/*
		 * if(order.equals("ASC")) {	
		 * ctx.setVariable("order", "DESC");
		}else {
			ctx.setVariable("order", "ASC");
		}
		 */
		ctx.setVariable("order", order);
		
		

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
