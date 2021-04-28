package it.polimi.tiw.docente;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.beans.Docente;
import it.polimi.tiw.beans.Message;
import it.polimi.tiw.beans.UrlPath;
import it.polimi.tiw.beans.UserType;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.common.ThymeleafInstance;
import it.polimi.tiw.dao.DocentiDAO;

/**
 * Servlet implementation class login
 */
//@WebServlet(name = "login_docente", urlPatterns = { "/login_docente" })  < --- may causes problems
public class LoginDocente extends HttpServlet {
	private static final long serialVersionUID = 2L;
	private TemplateEngine templateEngine; //required thymeleaf
	private Connection connection = null; //required connection to db
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginDocente() {
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
		
		//se mi arriva una richiesta get devo creare la pagine di login con thymelef 
		
		HttpSession session = request.getSession(false); // if session does not exist, returns null
		Message errorMessage;
		
		//possible improvement --> check if docente exists 
		//Set the user type that is going to log in 
		UserType userType = new UserType();
		userType.setType("docente");
		
		//Set the url path to be inserted in to the login.html
		UrlPath url = new UrlPath();
		url.setPath("LoginDocente");
		
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
		
		String username = null;
		String password = null;
		DocentiDAO docenteDAO = new DocentiDAO(this.connection);
		Message error = new Message();
		Docente docente = null;
		
		//get the param from the request
		username = request.getParameter("username");
		password = request.getParameter("password");
		docente = docenteDAO.checkCredential(username, password);
		if(docente != null) {
			//login succeed
			if(request.getSession().getAttribute("studente") != null) {
				//se c'è un docente lo rimuovo forzando il logout
				request.getSession().removeAttribute("studente");
			}
			request.getSession().setAttribute("docente", docente);
			response.sendRedirect(getServletContext().getContextPath() + "/HomeDocente");
			
		}else {
			//login failed
			error.setMessage("Username e/o password errati");
			request.setAttribute("errorMessage", error);
			doGet(request, response);
		}
		
	}

}
