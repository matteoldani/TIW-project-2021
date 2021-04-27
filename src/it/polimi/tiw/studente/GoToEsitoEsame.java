package it.polimi.tiw.studente;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

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
		AppelliDAO appelliDao = new AppelliDAO(connection);
		String voto = null;
		Integer voto_numerico = null;
		Corso c = null;
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
			
			iscritto = null;
			iscritto = appelliDao.getIscrittoAppello(id_appello, studente.getMatricola());
			
			if(iscritto == null) {
				errorMessage.setMessage("Non sei iscritto all'appello selezionato");
			}
			
			CorsiDAO corsiDao = new CorsiDAO(connection);
			c = corsiDao.getCorsoFromId(appelliDao.getAppelloFromID(id_appello).getId_corso());
		}
		
		//se l'erore è ancora vuoto allora prendo il voto
		if(errorMessage.getMessage().equals("")) {
			//posso settare le cose per i voti come singole variabili boolean <-- scelta questa strada
			//posso settare un bean capace di gestire i voti
			
			iscritto = appelliDao.getIscrittoAppello(id_appello, studente.getMatricola());
			String stato = iscritto.getStato();
			
			//se il voto è gia stato rifiutato
			if(stato.equals("rifiutato")) {
				votoRifiutabile = false;
				votoRifiutato = true;
				votoPubblicato = true;
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
			ctx.setVariable("nomeCorso", votoRifiutabile);
			//se il voto è stato rifiutato
			ctx.setVariable("id_appello", votoRifiutato);
			//il voto
			ctx.setVariable("voto", voto);
			//id appello
			ctx.setVariable("id_appello", id_appello);
			//corso
			ctx.setVariable("corso", c);
			//data appello
			ctx.setVariable("data", appelliDao.getAppelloFromID(id_appello).getData());
			
			
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

}
