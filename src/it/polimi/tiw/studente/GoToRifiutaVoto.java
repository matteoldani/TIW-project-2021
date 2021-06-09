package it.polimi.tiw.studente;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.IscrittiAppello;
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.beans.Studente;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.dao.AppelliDAO;

/**
 * Servlet implementation class GoToRifiutaVoto
 */
@WebServlet("/RifiutaVoto")
public class GoToRifiutaVoto extends HttpServlet {
	private static final long serialVersionUID = 1L;
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
				errorMessage.setMessage("L'appello selezionato non e' corretto");
			}
			
		}else{
			id_appello = null;

			errorMessage.setMessage("L'appello selezionato non e' corretto");
		}
		
		
		//verifico che lo studente sia iscrtito a questo appello 
		AppelliDAO appelliDao ;
		IscrittiAppello ia = null;
		if(errorMessage.getMessage().equals("")) {
			try {
				appelliDao = new AppelliDAO(connection);
				ia = appelliDao.getIscrittoAppello(id_appello, matricola);
				
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
								appelliDao.rifiutaVoto(id_appello, matricola);	
							}else {
								try {
									voto_numerico = Integer.parseInt(voto);
									appelliDao.rifiutaVoto(id_appello, matricola);
								}catch(NumberFormatException e) {
									//se c'è iun prolema a livello di consistenza del database 
									e.printStackTrace();
									voto_numerico = null;							
									response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
									errorMessage = new Message();
									errorMessage.setMessage("E' stato riscontrato un problema con il database, riprova piu' tardi");
									response.getWriter().println("Errore interno al database. Riprova piu' tardi");
									
									
									return;
								} 
							}					
						}
					}else {
						errorMessage.setMessage("Voto non rifiutabile");
					}
					
				}else {
					errorMessage.setMessage("L'utente non e' iscrtitto a questo appello");
				}
				
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
		}
		
		

		
		if(errorMessage.getMessage().equals("")) {
			response.setStatus(200);
		}else {				
			//in caso di errore mando la bad request e l'errore come messaggio 
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
