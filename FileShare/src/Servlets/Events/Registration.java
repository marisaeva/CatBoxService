package Servlets.Events;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Models.UserModel;
import Servlets.ErrorMessageBox;
import Storage.UserStrorage;

@WebServlet("/Registration")
public class Registration extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("login");
        String password = request.getParameter("password");
        PrintWriter out=response.getWriter();
        Boolean badPass = false;
        //check name and password
        if(!UserModel.CheckString(name) || !UserModel.CheckString(password)){
			out.println(ErrorMessageBox.GetMessageBox("Check login and password! Login and password cannot contains special symbols and spaces!"));
        	out.close();     
        	badPass = true;
        }
        if(!badPass){
	        response.setContentType("text/html");
	        UserStrorage dataBase = new UserStrorage();
	        try {
				if(!name.isEmpty() && !password.isEmpty() && name.length() >= 6 && password.length() >= 6){
					if(dataBase.ValidUser(name, password)){
						dataBase.AddNewUser(name, password);
						out.println(ErrorMessageBox.GetMessageBox("Now you in system! <a href=\"index.html\">Home page</a>"));
			        	out.close();
						}
					else{
						out.println(ErrorMessageBox.GetMessageBox("Current login already in system!"));
			        	out.close();
					}
					}
				else{
					out.println(ErrorMessageBox.GetMessageBox("Check login and password! Login and password must be 6 and more symbols!"));
		        	out.close();	
				}
			} catch (InterruptedException e) {
				out.println(ErrorMessageBox.GetMessageBox("Maybe server request some exceptions! Contact with tech. support"));
	        	out.close();	
			}
        }
	}

}
