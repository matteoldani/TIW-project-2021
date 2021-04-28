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

import it.polimi.tiw.beans.IscrittiAppello;
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.beans.Studente;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.common.ThymeleafInstance;
import it.polimi.tiw.dao.AppelliDAO;

/**
 * Servlet implementation class GoToRifiutaVoto
 */
@WebServlet("/RifiutaVoto")
public class GoToRifiutaVoto extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine; //required thymeleaf
	private Connection connection = null; //required connection to db
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToRifiutaVoto() {
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
		Studente studente = (Studente) request.getSession().getAttribute("studente");
		String id_appello_string = request.getParameter("id");
		Integer matricola = studente.getMatricola();
		Integer id_appello = null;
		Message errorMessage = new Message();
		errorMessage.setMessage("");
		
		
		//non devo fare i controlli sulla matricola perchè fatti a monte dal filter
		
		//faccio i controlli sull'id appello
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
		//verifico che lo studente sia iscrtito a questo appello 
		AppelliDAO appelliDao = new AppelliDAO(connection);
		IscrittiAppello ia = appelliDao.getIscrittoAppello(id_appello, matricola);
		if(ia != null) {
			//studente iscritto, devo verificare che il voto fosse rifiutabile
			if(ia.getStato().equals("pubblicato")) {
				String voto = ia.getVoto();
				Integer voto_numerico;
				boolean controllo = false;
				if(voto.equals("rimandato") || voto.equals("riprovato") || voto.equals("assente")) {
					errorMessage.setMessage("Voto non rifiutabile");
				}else {
					//se posso rifiutre
					if(voto.equals("30 e Lode")) {
						controllo = appelliDao.rifiutaVoto(id_appello, matricola);
					}else {
						try {
							voto_numerico = Integer.parseInt(voto);
							controllo = appelliDao.rifiutaVoto(id_appello, matricola);
						}catch(NumberFormatException e) {
							//se c'è iun prolema a livello di consistenza del database 
							voto_numerico = null;							
							errorMessage.setMessage("Errore nel database, riprova più tardi");
						}
					}
					
					if(!controllo) {
						errorMessage.setMessage("Impossibile rifiutare il voto, riprovare più tardi");
					}
					
				}
			}else {
				errorMessage.setMessage("Voto non rifiutabile");
			}
			
			if(errorMessage.getMessage().equals("")) {
				String path = request.getContextPath() + "/EsitoEsame?id=" + id_appello;
				response.sendRedirect(path);
			}else {
				String path = "WEB-INF/errorPage.html";
				
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				
				//se ho trovato un errore l'utente viene mandato in una pagina in cui viene fatto vedere l'errore e 
				//data la possibilità di tornare alla home. 
				//in questo modo gestisco anche gli errori a livello di server. In ogni caso l'utente arriva in qeusta
				//pagina solo se cerca di moficare in modo malevolo la request
				
				ctx.setVariable("errorMessage", errorMessage);
				
				templateEngine.process(path, ctx, response.getWriter());
			}
			
			
			
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
