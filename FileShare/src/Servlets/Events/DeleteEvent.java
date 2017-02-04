package Servlets.Events;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Delete")
public class DeleteEvent extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public DeleteEvent() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request != null){
			String id = request.getParameter("id");
	        if(id != null && !id.isEmpty()){
	        	int validId = Integer.parseInt(id);
	        	Storage.FileStorage f = new Storage.FileStorage();
	        	try {
					Models.FileModel file = f.GetFileById(validId);
					f.DeleteFile(file);
					response.sendRedirect("Global");
				} catch (InterruptedException e) {
					
				}
	        }
		}
	}
}
