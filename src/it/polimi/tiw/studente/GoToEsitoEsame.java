package it.polimi.tiw.studente;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
import it.polimi.tiw.beans.IscrittiAppello;
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.beans.Studente;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.common.ThymeleafInstance;
import it.polimi.tiw.dao.AppelliDAO;
import it.polimi.tiw.dao.CorsiDAO;

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
		IscrittiAppello iscritto = null;
		AppelliDAO appelliDao = null;
		CorsiDAO corsiDao;
		String voto = null;
		Integer voto_numerico = null;
		Corso c = null;
		Appello appello = null;
		boolean votoRifiutabile = false;
		boolean votoRifiutato = false;
		boolean votoPubblicato = false;
		
		errorMessage.setMessage("");
		
		
		//controllo che l'id sia un numero
		if(id_appello_string != null && !id_appello_string.equals("")) {
			try {
				id_appello = Integer.parseInt(id_appello_string);
			}catch(NumberFormatException e) {
				id_appello = null;
				errorMessage.setMessage("L'appello selezionato non e' corretto");
			}
			
		}else{
			id_appello = null;
			if(id_appello_string != null && id_appello_string.equals("")) {
				errorMessage.setMessage("L'appello selezionato non e' corretto");
			}
		}
		
		//verifico che la matricola sia iscritta all'appello 
		if(id_appello != null) {
			
			iscritto = null;
			try {
				appelliDao = new AppelliDAO(connection);
				corsiDao = new CorsiDAO(connection);
				iscritto = appelliDao.getIscrittoAppello(id_appello, studente.getMatricola());
				
				if(iscritto == null) {
					errorMessage.setMessage("Non sei iscritto all'appello selezionato");
				}
				appello = appelliDao.getAppelloFromID(id_appello);
				if(appello == null) {
					errorMessage.setMessage("Appello non esistente");
				}else{
					c = corsiDao.getCorsoFromId(appello.getId_corso());
				}
				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
				//in una pagine di errore generica (scleta migliore esteticamente) 
				errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
				request.getSession().setAttribute("errorMessage", errorMessage);
				String path = getServletContext().getContextPath() + "/ErrorPage";
				response.sendRedirect(path);
				return;
			}
			
		}else {
			errorMessage.setMessage("Appello non inserito in modo corretto");
		}
		
		//se l'erore è ancora vuoto allora prendo il voto
		
		if(errorMessage.getMessage().equals("")) {
			
			try {
				
				iscritto = appelliDao.getIscrittoAppello(id_appello, studente.getMatricola());
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
				//in una pagine di errore generica (scleta migliore esteticamente) 
				errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
				request.getSession().setAttribute("errorMessage", errorMessage);
				String path = getServletContext().getContextPath() + "/ErrorPage";
				response.sendRedirect(path);
				return;
			}
			String stato = iscritto.getStato();
			
			//se il voto è gia stato rifiutato
			if(stato.equals("rifiutato") || stato.equals("verbalizzato")) {
				if(stato.equals("rifiutato")) {
					votoRifiutabile = false;
					votoRifiutato = true;
					votoPubblicato = true;
				}else{
					votoRifiutabile = false;
					votoRifiutato = false;
					votoPubblicato = true;
				}
				
				voto = iscritto.getVoto();		
			}else {
				//il voto non è stato rifiutato ma è pubblicato
				votoRifiutato = false;
				if(stato.equals("pubblicato")) {
					votoPubblicato = true;
					voto = iscritto.getVoto();
					//se non posso rifiutare
					if(voto.equals("rimandato") || voto.equals("riprovato") || voto.equals("assente")) {
						votoRifiutabile = false;
					}else {
						//se posso rifiutre
						if(voto.equals("30 e Lode")) {
							votoRifiutabile = true;
						}else {
							try {
								voto_numerico = Integer.parseInt(voto);
								votoRifiutabile = true;
							}catch(NumberFormatException e) {
								//se c'è iun prolema a livello di consistenza del database 
								voto_numerico = null;
								votoRifiutabile = false;
								errorMessage.setMessage("Errore nel database, riprova più tardi");
							}
						}
						
					}
				}else {
					//il voto non è pubblicato quindi non è visibile 
					votoPubblicato = false;
					
				}
			}
			
			//path del template
			String path = "WEB-INF/esitoEsame.html";
			
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			
			//se il voto è stato pubblicato 
			ctx.setVariable("votoPubblicato", votoPubblicato);
			//se il voto è rifiutabile
			ctx.setVariable("votoRifiutabile", votoRifiutabile);
			//se il voto è stato rifiutato
			ctx.setVariable("votoRifiutato", votoRifiutato);
			//il voto
			ctx.setVariable("voto", voto);
			//id appello
			ctx.setVariable("id_appello", id_appello);
			//corso
			ctx.setVariable("corso", c);
			//data appello
			
			// errormessge
			ctx.setVariable("errorMessage", errorMessage);
			try {
				ctx.setVariable("data", appelliDao.getAppelloFromID(id_appello).getData());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
				//in una pagine di errore generica (scleta migliore esteticamente) 
				errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
				request.getSession().setAttribute("errorMessage", errorMessage);
				path = getServletContext().getContextPath() + "/ErrorPage";
				response.sendRedirect(path);
				return;
			}
			
			
			templateEngine.process(path, ctx, response.getWriter());	
			
		}else {
			//se trovo un errore devo stampare solo quello 
			String path = "WEB-INF/esitoEsame.html";
			
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			
			// errormessge
			ctx.setVariable("errorMessage", errorMessage);
			templateEngine.process(path, ctx, response.getWriter());
			
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
