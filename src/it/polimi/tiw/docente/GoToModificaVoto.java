package it.polimi.tiw.docente;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.polimi.tiw.beans.Appello;
import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Docente;
import it.polimi.tiw.beans.IscrittiAppello;
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.beans.Studente;
import it.polimi.tiw.beans.StudenteFromJson;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.dao.AppelliDAO;
import it.polimi.tiw.dao.CorsiDAO;

/**
 * Servlet implementation class GoToModificaVoto
 */
@WebServlet("/ModificaVoto")
@MultipartConfig
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
		
		errorMessage.setMessage("");
		
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
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
				errorMessage = new Message();
				errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
				response.getWriter().println("Errore interno al database. Riprova piu' tardi");
				return;
			}
			
		}
		
		if(errorMessage.getMessage().equals("")) {
			//non ho avuto errori
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonElement studenteJsonElement = gson.toJsonTree(studente);
			studenteJsonElement.getAsJsonObject().addProperty("stato", stato);
			studenteJsonElement.getAsJsonObject().addProperty("voto", voto);
			
			String json = gson.toJson(studenteJsonElement);
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
		}else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(errorMessage.getMessage());
		}
			
		
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
		//String matricola_string = request.getParameter("matricola");
		//String voto = request.getParameter("voto");
		Integer id_appello = null;
		Integer matricola = null;
		Integer votoNumerico = null;
		Message errorMessage = new Message();
		String commonError = "Impossible processare i dati inseriti. Matricola, id appello e voto inseriti potrebbero non essere validi";
		errorMessage.setMessage(""); //se cambia vuol dire che c'è stato un errore e non devo modificare il database
		
		//prendo il body come stringa
		StringBuffer jb = new StringBuffer();
	    String line = null;
		try {
		  BufferedReader reader = request.getReader();
		  while ((line = reader.readLine()) != null)
		    jb.append(line);
		} catch (Exception e) { /*report an error*/ }
	
		//salvola stringa dentro un jsonObject oer poi prenderne i componenti
		JsonObject convertedObject = new Gson().fromJson(jb.toString(), JsonObject.class);
		
		//salvo l'appello
		id_appello_string = convertedObject.get("id_appello").getAsString();
		
		//costriusco l'array di "studenti" presi dal json
		JsonElement studentiObject = convertedObject.get("studenti");
		StudenteFromJson[] arrayOfStudenti = new Gson().fromJson(studentiObject.toString(), StudenteFromJson[].class);
		
		/* DEBUGGING
		System.out.println(arrayOfStudenti.length);
		System.out.println(arrayOfStudenti[0].getMatricola());
		System.out.println(id_appello_string);
		*/
		
		
		AppelliDAO appelliDao = null;
		try {
			appelliDao = new AppelliDAO(connection);
		} catch (SQLException e1) {
			//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
			//in una pagine di errore generica (scleta migliore esteticamente) 
			e1.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
			errorMessage = new Message();
			errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
			response.getWriter().println("Errore interno al database. Riprova piu' tardi");
			return;
		}
		
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
		
		
		
		for(int i=0; i<arrayOfStudenti.length; i++) {
			
			//controllo che tutte le matricole e i voti siano corretti
			if(arrayOfStudenti[i].getMatricola() == null) {
				errorMessage.setMessage(commonError);
			}
			
			
			if(arrayOfStudenti[i].getVoto() == null || arrayOfStudenti[i].getVoto().equals("")) {
				errorMessage.setMessage(commonError);
			}else {
				if(!arrayOfStudenti[i].getVoto().equals("assente") && !arrayOfStudenti[i].getVoto().equals("rimandato") && 
						!arrayOfStudenti[i].getVoto().equals("riprovato")  && !arrayOfStudenti[i].getVoto().equals("lode")) {
					try {
						System.out.println(arrayOfStudenti[i].getVoto());
						votoNumerico = Integer.parseInt(arrayOfStudenti[i].getVoto());
						if(votoNumerico < 18 || votoNumerico > 30) {
							errorMessage.setMessage(commonError);
						}
							
					}catch(NumberFormatException e) {
						e.printStackTrace();
						matricola = null;
						errorMessage.setMessage(commonError);
					}
				}
			}
			
		
			
			IscrittiAppello ia = null;
			
			//controllo che appello e matricola associati esistano
			if(errorMessage.getMessage().equals("")) {
				try {
					ia = appelliDao.getIscrittoAppello(id_appello, arrayOfStudenti[i].getMatricola());
				} catch (SQLException e) {
					//se trovo un eccezione lato server causata dal databse non posso fare altro che madnare l'utente
					//in una pagine di errore generica (scleta migliore esteticamente) 
					e.printStackTrace();
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
					errorMessage = new Message();
					errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
					response.getWriter().println("Errore interno al database. Riprova piu' tardi");
				}
				if(ia == null) {
					errorMessage.setMessage("Lo studente scelto non era iscrtito a questo appello");
				}
			}
			
			
			//controllo che lo stato nel database non sia "pubblicato"
			if(errorMessage.getMessage().equals("")){
				try {
					ia = appelliDao.getIscrittoAppello(id_appello, arrayOfStudenti[i].getMatricola());
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
				
				if(ia.getStato().equals("pubblicato") || ia.getStato().equals("rifiutato") || ia.getStato().equals("verbalizzato")) {
					errorMessage.setMessage("Non e' possibile modificare un voto gia pubblicato, rifiutato o verbalizzato");
				}
			}
			
			
			
			//se tutto è valido il messaggio d'errore non è stato modificato
			if(errorMessage.getMessage().equals("")) {
				boolean ris;
				try {
					ris = appelliDao.updateVoto(arrayOfStudenti[i].getMatricola(), id_appello, arrayOfStudenti[i].getVoto());
				} catch (SQLException e) {
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
				
				//anche se non è un errore lato server in questo caso è dovuto al tentativo di fare una post maligna e quindi
				//vale la pena madare l'utente malevolo nella pagine comunque di errore stampando l'effettivo errore 
			
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
				response.getWriter().println(errorMessage.getMessage());
				return;
				
			}

		}
		
		//solo alla fine di tutti gli aggiornamenti mando la risposta, se non ho trovato nessun errore
		
		if(errorMessage.getMessage().equals("")) {
			response.setStatus(200);
		}else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
			response.getWriter().println(errorMessage.getMessage());
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
