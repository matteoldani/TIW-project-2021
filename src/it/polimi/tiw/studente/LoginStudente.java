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
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.beans.Message;
import it.polimi.tiw.beans.Studente;
import it.polimi.tiw.beans.UrlPath;
import it.polimi.tiw.beans.UserType;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.common.ThymeleafInstance;
import it.polimi.tiw.dao.StudentiDAO;

/**
 * Servlet implementation class login
 */
@WebServlet("/LoginStudente")
public class LoginStudente extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine; //required thymeleaf
	private Connection connection = null; //required connection to db
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginStudente() {
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
		
		//se mi arriva una richiesta get devo creare la pagine di login con thymelef 
		
		HttpSession session = request.getSession(false); // if session does not exist, returns null
		Message errorMessage;
		
		//possible improvement --> check if docente exists 
		//Set the user type that is going to log in 
		UserType userType = new UserType();
		userType.setType("studente");
		
		//Set the url path to be inserted in to the login.html
		UrlPath url = new UrlPath();
		url.setPath("/LoginStudente");
		
		//get the possible error message
		errorMessage = (Message) request.getAttribute("errorMessage");
		if(errorMessage == null) {	
			//if no attribute were retrieved 
			errorMessage = new Message();
			errorMessage.setMessage("");		
		}
		
		//path del template
		String path = "WEB-INF/login.html";
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("userType", userType);
		ctx.setVariable("urlPath", url);
		ctx.setVariable("errorMessage", errorMessage);
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		/*create the dao and check the credential, if credentials are accepted the corresponded bean is returned:
		 * -add info about the user
		 * -go to the home page 
		 * 
		 * if null is returned 
		 * -set the error message
		 * -reload the login page with the error message (with the redirect to the get)
		 */
		
		String matricola = null;
		String password = null;
		StudentiDAO studentiDao;
		Message errorMessage = new Message();
		Studente studente = null;
		
		//get the param from the request
		matricola = request.getParameter("username");
		password = request.getParameter("password");
		try {
			studentiDao = new StudentiDAO(this.connection);
			studente = studentiDao.checkCredentials(matricola, password);
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
		if(studente != null) {
			//login succeed
			if(request.getSession().getAttribute("docente") != null) {
				//se c'è un docente lo rimuovo forzando il logout
				request.getSession().removeAttribute("docente");
			}
			request.getSession().setAttribute("studente", studente);
			response.sendRedirect(request.getContextPath() + "/HomeStudente");
			
		}else {
			//login failed
			errorMessage.setMessage("Username e/o password errati");
			request.setAttribute("errorMessage", errorMessage);;
			doGet(request, response);
		}
	}

}
