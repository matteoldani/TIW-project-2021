package it.polimi.tiw.docente;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.beans.Docente;
import it.polimi.tiw.beans.ErrorMessage;
import it.polimi.tiw.beans.UrlPath;
import it.polimi.tiw.beans.UserType;

import it.polimi.tiw.dao.DocentiDAO;
import it.polimi.tiw.common.ConnectionHandler;
import it.polimi.tiw.common.ThymeleafInstance;

/**
 * Servlet implementation class login
 */
@WebServlet(name = "login_docente", urlPatterns = { "/login_docente" })
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
		HttpSession session = request.getSession(false); // if session does not exist, returns null
		ErrorMessage errorMessage;
		if(session == null) {
			//no session available --> login is needed
			
			//Info per il titolo
			UserType userType = new UserType();
			userType.setType("docente");
			
			//url per la action del post per login
			UrlPath url = new UrlPath();
			url.setPath("LoginDocente");
			
			//try to get the errorMessage, if set
			errorMessage = (ErrorMessage) request.getAttribute("errorMessage");
			
			if(errorMessage == null) {	
				//if no attribute were retrieved 
				errorMessage = new ErrorMessage();
				errorMessage.setError("");		
			}
			
			//path del template
			String path = "WEB-INF/login.html";
			
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("userType", userType);
			ctx.setVariable("urlPath", url);
			ctx.setVariable("errorMessage", errorMessage);
			templateEngine.process(path, ctx, response.getWriter());
		}else {
			response.sendRedirect(request.getContextPath() + "/CourseList");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		/*create the dao and check the credential, if credentials are accepted the corresponded bean is returned:
		 * -create a session
		 * -add info about the user
		 * -go to the course page 
		 * 
		 * if null is returned 
		 * -set the error message
		 * -reload the login page with the error message (with the redirect to the get)
		 */
		
		String username = null;
		String password = null;
		DocentiDAO docenteDAO = new DocentiDAO(this.connection);
		ErrorMessage error = new ErrorMessage();
		Docente docente = null;
		
		//get the param from the request
		username = request.getParameter("username");
		password = request.getParameter("password");
		docente = docenteDAO.checkCredential(username, password);
		if(docente != null) {
			//login succeed
			request.getSession(true).setAttribute("docente", docente);
			response.sendRedirect(request.getContextPath() + "/CourseList");
			
		}else {
			//login failed
			error.setError("Username e/o password errati");
			request.setAttribute("errorMessage", error);
			doGet(request, response);
		}
		
	}

}
