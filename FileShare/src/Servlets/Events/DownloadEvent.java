package Servlets.Events;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Servlets.ErrorMessageBox;


@WebServlet("/Download")
public class DownloadEvent extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public DownloadEvent() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request != null){
			String id = request.getParameter("id");
	        if(id != null && !id.isEmpty()){
	        	int validId = Integer.parseInt(id);
	        	Storage.FileStorage f = new Storage.FileStorage();
	        	Models.FileModel buff;
				try {
					buff = f.GetFileById(validId);
		        	if(buff == null){
		        		PrintWriter out = response.getWriter();
			        	out.write(ErrorMessageBox.GetMessageBox("File not found!"));
			        	out.close();
		        	}else{
		        		File file = new File(buff.GetPath());
		        		response.setContentType(Files.probeContentType(Paths.get(buff.GetPath())));
		        		response.setHeader("Content-disposition","attachment; filename=" + buff.GetCaption() + "." + buff.GetType());
		                OutputStream download = response.getOutputStream();
		                FileInputStream in = new FileInputStream(file);
		                byte[] buffer = new byte[4096];
		                int length;
		                while ((length = in.read(buffer)) > 0){
		                	download.write(buffer, 0, length);
		                }
		                in.close();
		                download.flush();
		                download.close();
		        	}
				} catch (InterruptedException e) {
					PrintWriter out = response.getWriter();
		        	out.write(ErrorMessageBox.GetMessageBox("File not found!"));
		        	out.close();					
				}
	        }
	        else{
	    		PrintWriter out = response.getWriter();
	        	out.write(ErrorMessageBox.GetMessageBox("Something wrong!"));
	        	out.close();
	        }
        }
        else{
    		PrintWriter out = response.getWriter();
        	out.write(ErrorMessageBox.GetMessageBox("Something wrong!"));
        	out.close();
        }
	}
}
