
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.annotation.WebServlet;


/**
 * Servlet implementation class LoginValidation
 */
@WebServlet("/LoginValidation")
public class LoginValidation extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginValidation() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String targetPage=null;
		String sqlQuery=null;
		boolean userExists = false;
		ArrayList<String> notifications = new ArrayList<String>();
		String formusername = request.getParameter("username");
		String formPassword = request.getParameter("password");
		System.out.println("Inside the Login Validation Servlet Page");
		try {

			HttpSession localHttpSession = request.getSession();

			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/NewExpense", "root", "root");
			Statement s = conn.createStatement();
			sqlQuery="select * from Customer where username IN('" + formusername + "') AND pass IN('" + formPassword + "')";
			ResultSet r = s.executeQuery(sqlQuery);
			
			if (r.next()) {
				userExists= true;
				targetPage="DailyExp.jsp";
			}
			else {
				sqlQuery="select * from Advisor where username IN('" + formusername + "') AND pass IN('" + formPassword + "')";
				r = s.executeQuery(sqlQuery);
				if (r.next()) {
					localHttpSession.setAttribute("advisor", "true");
					userExists= true;
					targetPage="advisorHome.jsp";
					System.out.println("Found the Financial Advisor");
				}
			}

			if(userExists) {
				System.out.println("NewUser Exists");
				localHttpSession.setAttribute("username", formusername);
				sqlQuery="select * from notification where ReceiverName  IN('" + formusername + "')";
				r = s.executeQuery(sqlQuery);
				while (r.next()) {
					System.out.println(r.getString("notificationType") + r.getString("SenderName"));
					notifications.add(r.getString("notificationType"));
					notifications.add(r.getString("SenderName"));
				}
				localHttpSession.setAttribute("NotificationList", notifications);
			}
			else {
				localHttpSession.setAttribute("message", "Please enter valid Credentials.");
				targetPage="InvalidUser.jsp"; 
			}
			response.sendRedirect(targetPage);
			conn.close();
		} catch (Exception localException) {
			localException.printStackTrace();
			System.out.println("Invalid User");
		}
	}

}