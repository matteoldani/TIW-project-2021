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

import it.polimi.tiw.beans.ErrorMessage;
import it.polimi.tiw.beans.UrlPath;
import it.polimi.tiw.beans.UserType;

import it.polimi.tiw.dao.DocentiDAO;

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
    	ServletContext servletContext = getServletContext();
    	
    	 
    	final String DB_URL = "jdbc:mysql://localhost:3306/verbalizzazione_voti";
 		final String USER = "root";
 		final String PASS = "password";
    	
    	/*settings and data are stored into web.xml as contex param
    	String driver = servletContext.getInitParameter("dbDriver");
		String url = servletContext.getInitParameter("dbUrl");
		String user = servletContext.getInitParameter("dbUser");
		String password = servletContext.getInitParameter("dbPassword");
		*/
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); 
 			connection = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
			
		}
    	
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
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
			String path = "login.html";
			
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("userType", userType);
			ctx.setVariable("urlPath", url);
			ctx.setVariable("errorMessage", errorMessage);
			templateEngine.process(path, ctx, response.getWriter());
		}else {
			//redirect alla pagine dei corsi appena sarà pronta
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		/*create the dao and check the credential, if true is returned:
		 * -create a session
		 * -add info about the user
		 * -go to the course page 
		 * 
		 * if false is returned 
		 * -set the error message
		 * -reload the login page with the error message (with the redirect to the get)
		 */
		
		String username = null;
		String password = null;
		DocentiDAO docente = new DocentiDAO(this.connection);
		ErrorMessage error = new ErrorMessage();
		
		//get the param from the request
		username = request.getParameter("email");
		password = request.getParameter("password");
		
		if(docente.checkCredential(username, password)) {
			//login succeed
		}else {
			//login failed
			error.setError("Username e/o password errati");
			request.setAttribute("errorMessage", error);
			doGet(request, response);
		}
		
	}

}
