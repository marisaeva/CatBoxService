package Servlets.Events;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Models.UserModel;
import Servlets.ErrorMessageBox;
import Storage.UserStrorage;

@WebServlet("/Login")
public class LoginEvent extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        if(login != null && login.length() > 0 && password != null && password.length() > 0){
	        PrintWriter out=response.getWriter();
	        UserStrorage base = new UserStrorage();
	        try {
				if(base.SingIn(login, password)){
		            HttpSession session = request.getSession();
		            session.setAttribute("login", login);
		            response.sendRedirect("Global");
		        	out.close();
				}else{
					out.println(ErrorMessageBox.GetMessageBox("Bad login or password!"));
		        	out.close();
				}
			} catch (InterruptedException e) {
				out.println(ErrorMessageBox.GetMessageBox("Maybe server request some exceptions! Contact with tech. support"));
	        	out.close();
			}
		}
        else{
            RequestDispatcher rd=request.getRequestDispatcher("index.html");  
            rd.include(request,response);  

        }
	}

}
