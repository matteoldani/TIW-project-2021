package it.polimi.tiw.docente;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
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
		Message errorMessage = new Message();
		Message successMessage = new Message();
		AppelliDAO appelliDao;
		ArrayList<IscrittiAppello> iscrittoAppello = null;
		Docente docente;
		DocentiDAO docentiDao;
		CorsiDAO corsiDao;
		String nomeCorso = null;
		Date dataAppello = null;
		String orderAttribute = null;
		String order;
		boolean verbalizzabili = false;
		
		try {
			docentiDao = new DocentiDAO(connection);
			appelliDao = new AppelliDAO(connection);
			corsiDao = new CorsiDAO(connection);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
			//in una pagine di errore generica (scleta migliore esteticamente) 
			errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
			request.getSession().setAttribute("errorMessage", errorMessage);
			String path = getServletContext().getContextPath() + "/ErrorPage";
			response.sendRedirect(path);
			return;
		}
		
		successMessage = (Message) request.getSession().getAttribute("successMessage");
		request.getSession().removeAttribute("successMessage");
		
		
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
			errorMessage.setMessage("Non e' stato selezioanto nessun appello");
		}
		
		//l'id non è un numero
		try {
			id_appello = Integer.parseInt(id);
		}catch(NumberFormatException e) {
			id_appello = null;
			errorMessage.setMessage("L'appello selezionato non e' presente");
		}
		
		if(id_appello != null) {			
			//devo controlalre che effettivamente l'id appartenga a un corso che è tenuto dal professore 
			docente = (Docente) request.getSession(false).getAttribute("docente");
			Appello appello = null;
			ArrayList<Corso> corsiDocente = null;
			try {
				//la scelta di mettere tutto nello stesso try/catch è dovuta al fatto ch la gestione dei vari errori è sempre ugaule in quanto si tratta di un 
				//problema a monte con il database che non è possibile risolere automaticamente. È quindi irrilevante quale delle istruzioni causi l'arrivo nella pagina di 
				//errore
				appello = appelliDao.getAppelloFromID(id_appello);
				corsiDocente = docentiDao.getCourseList(docente.getId_docente());
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
					
					//controllo che tra quelli iscritti a questo appello almeno uno abbia come stato pubblicato o rifiutato
					for(IscrittiAppello ia: iscrittoAppello) {
						if(ia.getStato().equals("pubblicato") || ia.equals("rifiutato")) {
							verbalizzabili = true;
						}
					}
				}else {
					errorMessage.setMessage("Non puoi accedere ai dati di questo appello");
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

			boolean controllo = false;
			for(Corso c : corsiDocente) {
				if(c.getId_corso() == appello.getId_corso()) {
					controllo = true;
					nomeCorso = c.getNome();
					dataAppello = appello.getData();
				}
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
		//se c'è un successo lo stampo a inizio pagina
		ctx.setVariable("successMessage", successMessage);
		//elemento per cui ho ordinato 
		ctx.setVariable("orderAttribute", orderAttribute);
		//come devo ordianre 
		ctx.setVariable("order", order);
		//verbalizzabili
		ctx.setVariable("verbalizzabili", verbalizzabili);
		
		
		
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
