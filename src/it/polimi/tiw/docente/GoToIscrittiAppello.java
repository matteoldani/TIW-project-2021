package it.polimi.tiw.docente;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.tiw.beans.Appello;
import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Docente;
import it.polimi.tiw.beans.IscrittiAppello;
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.dao.AppelliDAO;
import it.polimi.tiw.dao.CorsiDAO;
import it.polimi.tiw.dao.DocentiDAO;

/**
 * Servlet implementation class GoToIscrittiAppello
 */
@WebServlet("/IscrittiAppello")
public class GoToIscrittiAppello extends HttpServlet {
	private static final long serialVersionUID = 1L;
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

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String id = request.getParameter("id");
		Integer id_appello = null;
		Message errorMessage = new Message();
		AppelliDAO appelliDao;
		ArrayList<IscrittiAppello> iscrittoAppello = null;
		Docente docente;
		DocentiDAO docentiDao;
		CorsiDAO corsiDao;
		String nomeCorso = null;
		Date dataAppello = null;
		
		boolean verbalizzabili = false;
		
		errorMessage.setMessage("");
		
		try {
			docentiDao = new DocentiDAO(connection);
			appelliDao = new AppelliDAO(connection);
			corsiDao = new CorsiDAO(connection);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
			//in una pagine di errore generica (scleta migliore esteticamente) 
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
			errorMessage = new Message();
			errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
			response.getWriter().println("Errore interno al database. Riprova piu' tardi");
			return;
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
				corsiDocente = docentiDao.getListaCorsi(docente.getId_docente());
				
				boolean controllo = false;
				if(appello == null) {
					errorMessage.setMessage("appello non trovato nel database");
				}else{
					
					for(Corso c : corsiDocente) {
						if(c.getId_corso() == appello.getId_corso()) {
							controllo = true;
							nomeCorso = c.getNome();
							dataAppello = appello.getData();
						}
					}
				}
				
				
				if(controllo) {
					
					iscrittoAppello = appelliDao.getIscrittiAppello(id_appello);
					
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
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
				errorMessage = new Message();
				errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
				response.getWriter().println("Errore interno al database. Riprova piu' tardi");
				return;
			}

			boolean controllo = false;
			if(appello == null) {
				errorMessage.setMessage("appello non trovato nel database");
			}else{
				
				for(Corso c : corsiDocente) {
					if(c.getId_corso() == appello.getId_corso()) {
						controllo = true;
						nomeCorso = c.getNome();
						dataAppello = appello.getData();
					}
				}
			}

		}
		
		if(errorMessage.getMessage().equals("")) {
			//non ci sono stati errori
			String iscrittiAppelloJson = new Gson().toJson(iscrittoAppello);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(iscrittiAppelloJson);
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
