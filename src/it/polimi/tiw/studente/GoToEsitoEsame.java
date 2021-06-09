package it.polimi.tiw.studente;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import it.polimi.tiw.beans.Appello;
import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.IscrittiAppello;
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.beans.Studente;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.dao.AppelliDAO;
import it.polimi.tiw.dao.CorsiDAO;

/**
 * Servlet implementation class GoToEsitoEsameù
 */
@WebServlet("/EsitoEsame")
public class GoToEsitoEsame extends HttpServlet {
	private static final long serialVersionUID = 1L;
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
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
				errorMessage = new Message();
				errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
				response.getWriter().println("Errore interno al database. Riprova piu' tardi");
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
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
				errorMessage = new Message();
				errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
				response.getWriter().println("Errore interno al database. Riprova piu' tardi");
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
			Date data;
			try {
			    data = appelliDao.getAppelloFromID(id_appello).getData();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
				//in una pagine di errore generica (scleta migliore esteticamente) 
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
				errorMessage = new Message();
				errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
				response.getWriter().println("Errore interno al database. Riprova piu' tardi");
				return;
			}
			
			if(errorMessage.getMessage().equals("")) {
				//tutto a buon fine, mando il json con le informazioni
				
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				JsonElement studenteJsonElement = gson.toJsonTree(studente);
				studenteJsonElement.getAsJsonObject().addProperty("votoPubblicato", votoPubblicato);
				studenteJsonElement.getAsJsonObject().addProperty("votoRifiutabile", votoRifiutabile);
				studenteJsonElement.getAsJsonObject().addProperty("votoRifiutato", votoRifiutato);
				studenteJsonElement.getAsJsonObject().addProperty("voto", voto);
				studenteJsonElement.getAsJsonObject().addProperty("id_appello", id_appello);
				studenteJsonElement.getAsJsonObject().addProperty("corso", c.getNome());
				studenteJsonElement.getAsJsonObject().addProperty("data", data.toString());
				
				String json = gson.toJson(studenteJsonElement);
				
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(json);
				
				System.out.println(json);

			}else {
				//bad request con messaggio di errore
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println(errorMessage.getMessage());
			}
			
			
		}else {
			//bad request con messaggio di errore
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(errorMessage.getMessage());
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
