package it.polimi.tiw.docente;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.Appello;
import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Docente;
import it.polimi.tiw.beans.IscrittiAppello;
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.beans.Studente;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.dao.AppelliDAO;
import it.polimi.tiw.dao.CorsiDAO;

/**
 * Servlet implementation class GoToModificaVoto
 */
@WebServlet("/ModificaVoto")
public class GoToModificaVoto extends HttpServlet {
	private static final long serialVersionUID = 1L;
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
		AppelliDAO appelliDao;
		CorsiDAO corsiDao;
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
			
			
			try {
				appelliDao = new AppelliDAO(connection);
				corsiDao = new CorsiDAO(connection);
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
			
		/*
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
			
			*/

		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * In questo metodo si è scelto di lasciare divisi i try catch per una migliore divisione e comprensione del codice.
		 * 		
		 */
		//post per aggiornare il voto e lo stato dello studente
		
		Docente docente = (Docente) request.getSession().getAttribute("docente");
		String id_appello_string = request.getParameter("id_appello");
		String matricola_string = request.getParameter("matricola");
		String voto = request.getParameter("voto");
		Integer id_appello = null;
		Integer matricola = null;
		Integer votoNumerico = null;
		Message errorMessage = new Message();
		String commonError = "Impossible processare i dati inseriti. Matricola, id appello e voto inseriti potrebbero non essere validi";
		errorMessage.setMessage(""); //se cambia vuol dire che c'è stato un errore e non devo modificare il database
		
		
		//controllo che sia arriavto un id appello corretto
		if(id_appello_string == null || id_appello_string.equals("")) {
			errorMessage.setMessage(commonError);
		}else {
			try {
				id_appello = Integer.parseInt(id_appello_string);
			}catch(NumberFormatException e) {
				id_appello = null;
				errorMessage.setMessage(commonError);
			}
		}
		
		//controllo che la matricola sia corretta
		if(matricola_string == null || matricola_string.equals("")) {
			errorMessage.setMessage(commonError);
		}else {
			try {
				matricola = Integer.parseInt(matricola_string);
			}catch(NumberFormatException e) {
				matricola = null;
				errorMessage.setMessage(commonError);
			}
		}
		
		//controllo che il voto sia valido
		if(voto == null || voto.equals("")) {
			errorMessage.setMessage(commonError);
		}else {
			if(!voto.equals("assente") && !voto.equals("rimandato") && !voto.equals("riprovato")  && !voto.equals("lode")) {
				try {
					votoNumerico = Integer.parseInt(voto);
					if(votoNumerico < 18 || votoNumerico > 30) {
						errorMessage.setMessage(commonError);
					}
						
				}catch(NumberFormatException e) {
					matricola = null;
					errorMessage.setMessage(commonError);
				}
			}
		}
		
		IscrittiAppello ia = null;
		AppelliDAO appelliDao = null;
		try {
			appelliDao = new AppelliDAO(connection);
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
		//controllo che appello e matricola associati esistanoù
		if(errorMessage.getMessage().equals("")) {
			try {
				ia = appelliDao.getIscrittoAppello(id_appello, matricola);
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
			if(ia == null) {
				errorMessage.setMessage("Lo studente scelto non era iscrtito a questo appello");
			}
		}
		
		
		//controllo che lo stato nel database non sia "pubblicato"
		if(errorMessage.getMessage().equals("")){
			try {
				ia = appelliDao.getIscrittoAppello(id_appello, matricola);
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
			
			if(ia.getStato().equals("pubblicato") || ia.getStato().equals("rifiutato") || ia.getStato().equals("verbalizzato")) {
				errorMessage.setMessage("Non e' possibile modificare un voto gia pubblicato, rifiutato o verbalizzato");
			}
		}
		
		
		
		//se tutto è valido il messaggio d'errore non è stato modificato
		if(errorMessage.getMessage().equals("")) {
			boolean ris;
			try {
				ris = appelliDao.updateVoto(matricola, id_appello, voto);
			} catch (SQLException e) {
				e.printStackTrace();
				//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
				//in una pagine di errore generica (scleta migliore esteticamente) 
				errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
				request.getSession().setAttribute("errorMessage", errorMessage);
				String path = getServletContext().getContextPath() + "/ErrorPage";
				response.sendRedirect(path);
				return;
			}
			
			response.sendRedirect(getServletContext().getContextPath() + "/IscrittiAppello?id="+id_appello.toString());
			
		}else {
			
			//anche se non è un errore lato server in questo caso è dovuto al tentativo di fare una post maligna e quindi
			//vale la pena madare l'utente malevolo nella pagine comunque di errore stampando l'effettivo errore 
			request.getSession().setAttribute("errorMessage", errorMessage);
			String path = getServletContext().getContextPath() + "/ErrorPage";
			response.sendRedirect(path);
			return;
			
		}
		
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
