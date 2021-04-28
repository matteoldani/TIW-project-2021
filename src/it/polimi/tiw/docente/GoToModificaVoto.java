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
import it.polimi.tiw.beans.IscrittiAppello;
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.beans.Studente;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.common.ThymeleafInstance;
import it.polimi.tiw.dao.AppelliDAO;
import it.polimi.tiw.dao.CorsiDAO;

/**
 * Servlet implementation class GoToModificaVoto
 */
@WebServlet("/ModificaVoto")
public class GoToModificaVoto extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine; //required thymeleaf
	private Connection connection = null; //required connection to db
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToModificaVoto() {
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
		//get per far vedere la pagina con le info
		Appello appello = null; 
		Corso corso = null;
		Studente studente = null;
		String voto = null;
		String stato = null;
		AppelliDAO appelliDao = new AppelliDAO(connection);
		CorsiDAO corsiDao = new CorsiDAO(connection);
		Docente docente = (Docente) request.getSession().getAttribute("docente");
		String id_appello_string = request.getParameter("id_appello");
		String matricola_string = request.getParameter("matricola");
		Integer id_appello = null;
		Integer matricola = null;
		Message errorMessage = new Message();
		
		if(id_appello_string == null || id_appello_string.equals("")) {
			errorMessage.setMessage("Non sono stati inseriti un id appello e/o una matricola corretti");
		}else {
			try {
				id_appello = Integer.parseInt(id_appello_string);
			}catch(NumberFormatException e) {
				id_appello = null;
				errorMessage.setMessage("Non sono stati inseriti un id appello e/o una matricola corretti");
			}
		}
		
		if(matricola_string == null || matricola_string.equals("")) {
			errorMessage.setMessage("Non sono stati inseriti un id appello e/o una matricola corretti");
		}else {
			try {
				matricola = Integer.parseInt(matricola_string);
			}catch(NumberFormatException e) {
				matricola = null;
				errorMessage.setMessage("Non sono stati inseriti un id appello e/o una matricola corretti");
			}
		}
		
		if(matricola != null && id_appello != null) {
			//devo controllare che l'appello sia del prof e che la matricola sia nell'appello
			
			
			appello = appelliDao.getAppelloFromID(id_appello);
			if(appello == null) {
				errorMessage.setMessage("appello non esistente");
			}else {
				corso = corsiDao.getCorsoFromId(appello.getId_corso());
				
				if(corso == null) {
					errorMessage.setMessage("appello selezionato non associato a nessun corso");
				}else {
					if(corso.getId_docente() == docente.getId_docente()) {
												
						//il corso è del docente, devo solo controllare che la matricola sia iscritta all'appello e al corso
						ArrayList<IscrittiAppello> listaIscritti = new ArrayList<>();
						listaIscritti = appelliDao.getIscrittiAppello(id_appello, "", "");
						boolean controllo = false;
						for(IscrittiAppello iscritti : listaIscritti) {
							if(iscritti.getStudente().getMatricola() == matricola) {
								controllo =  true;
								studente = iscritti.getStudente();
								voto = iscritti.getVoto();
								break;
							}
						}
						
						if(!controllo) {
							//dovrei controllare che sia iscrtito al corso ma per ora non è fatto siccome il database è consistente
							// e non modificabile da questo punto di vista 
							
							//ho bisogno dello studente, dell'id appello e dell'error message 
							errorMessage.setMessage("matricola non presente in questo appello");
						}else {
							IscrittiAppello ia;
							stato = appelliDao.getIscrittoAppello(id_appello, matricola).getStato();
						}
	
					}else {
						errorMessage.setMessage("Appello selezionato non esistente");
					}
						
				}
					
			}
		}
			
		//path del template
		String path = "WEB-INF/modificaEsito.html";
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		//estudente con le informazioni
		ctx.setVariable("studente", studente);
		//id appello
		ctx.setVariable("id_appello", id_appello);
		//vecchio voto
		ctx.setVariable("voto", voto);
		//stato del voto
		ctx.setVariable("stato", stato);
		
		//se c'è un errore lo stampo a inizio pagina
		ctx.setVariable("errorMessage", errorMessage);

		templateEngine.process(path, ctx, response.getWriter());
			

		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		//post per aggiornare il voto e lo stato dello studente
		
		Docente docente = (Docente) request.getSession().getAttribute("docente");
		String id_appello_string = request.getParameter("id_appello");
		String matricola_string = request.getParameter("matricola");
		String voto = request.getParameter("voto");
		Integer id_appello = null;
		Integer matricola = null;
		Integer votoNumerico = null;
		Message errorMessage = new Message();
		errorMessage.setMessage(""); //se cambia vuol dire che c'è stato un errore e non devo modificare il database
		
		
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
		
		//controllo che la matricola sia corretta
		if(matricola_string == null || matricola_string.equals("")) {
			errorMessage.setMessage("Impossible processare i dati inseriti. Matricola, id appello e voto inseriti potrebbero non essere validi");
		}else {
			try {
				matricola = Integer.parseInt(matricola_string);
			}catch(NumberFormatException e) {
				matricola = null;
				errorMessage.setMessage("Impossible processare i dati inseriti. Matricola, id appello e voto inseriti potrebbero non essere validi");
			}
		}
		
		//controllo che il voto sia valido
		if(voto == null || voto.equals("")) {
			errorMessage.setMessage("Impossible processare i dati inseriti. Matricola, id appello e voto inseriti potrebbero non essere validi");
		}else {
			if(!voto.equals("assente") && !voto.equals("rimandato") && !voto.equals("riprovato")  && !voto.equals("lode")) {
				try {
					votoNumerico = Integer.parseInt(voto);
					if(votoNumerico < 18 || votoNumerico > 30) {
						errorMessage.setMessage("Impossible processare i dati inseriti. Matricola, id appello e voto inseriti potrebbero non essere validi");
					}
						
				}catch(NumberFormatException e) {
					matricola = null;
					errorMessage.setMessage("Impossible processare i dati inseriti. Matricola, id appello e voto inseriti potrebbero non essere validi");
				}
			}
		}
		
		IscrittiAppello ia;
		AppelliDAO appelliDao = new AppelliDAO(connection);
		//controllo che appello e matricola associati esistanoù
		if(errorMessage.getMessage().equals("")) {
			ia = appelliDao.getIscrittoAppello(id_appello, matricola);
			if(ia == null) {
				errorMessage.setMessage("Lo studente scelto non era iscrtito a questo appello");
			}
		}
		
		
		//controllo che lo stato nel database non sia "pubblicato"
		if(errorMessage.getMessage().equals("")){
			ia = appelliDao.getIscrittoAppello(id_appello, matricola);
			
			if(ia.getStato().equals("pubblicato")) {
				errorMessage.setMessage("Non è possibile modificare un voto gia pubblicato");
			}
		}
		
		
		
		//se tutto è valido il messaggio d'errore non è stato modificato
		if(errorMessage.getMessage().equals("")) {
			boolean ris;
			ris = appelliDao.updateVoto(matricola, id_appello, voto);
			if(ris) {
				response.sendRedirect(getServletContext().getContextPath() + "/IscrittiAppello?id="+id_appello.toString());
			}else {
				String path = "WEB-INF/errorPage.html";
				
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				
				errorMessage.setMessage("Non è stato possibile aggiornare il voto per un problema con il server");
				//se c'è un errore lo stampo a inizio pagina
				ctx.setVariable("errorMessage", errorMessage);

				templateEngine.process(path, ctx, response.getWriter());
			}
			
			
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
