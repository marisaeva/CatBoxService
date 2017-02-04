package Servlets;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Storage.FileStorage;
import Storage.UserStrorage;

@WebServlet("/Edit")
public class EditFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String name;
	private Models.FileModel file;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(false);
		if(session != null){
			name = (String)session.getAttribute("login");
		}
		else{
			out.print(ErrorMessageBox.GetMessageBox("current session was ended, or you should not be here "));
			out.close();
		}

		if(request != null){
			String id = request.getParameter("id");
			if(id != null && !id.isEmpty()){
				int validId = Integer.parseInt(id);
				Storage.FileStorage f = new Storage.FileStorage();
				try {
					file = f.GetFileById(validId);
					if(file != null){
						out.println(GetHTMLHead());
						out.println(GetHTMLForm());
						out.println(GetHTMLFooter());
						out.close();
					}
					else{
						out.print(ErrorMessageBox.GetMessageBox("This file does not exist in the database. As you were here?"));
						out.close();						
					}
				} catch (InterruptedException e) {
					out.print(ErrorMessageBox.GetMessageBox("This file does not exist in the database. As you were here?"));
					out.close();					
				}
			}
			else{
				out.print(ErrorMessageBox.GetMessageBox("This file does not exist in the database. As you were here?"));
				out.close();
			}
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String caption = request.getParameter("caption");
		String note = request.getParameter("note");
		String[] shared = request.getParameterValues("user");
		java.io.PrintWriter out = response.getWriter( );
		if(caption == null || caption.isEmpty() || caption.trim().length() < 3){
			out.print(ErrorMessageBox.GetMessageBox("Caption <b> must be more than 3 sybmols! </b>!"));
			out.close();
			return;	
		}
		else{
			file.SetCaption(caption);
			file.SetNote(note);
			file.SetShared(shared);
			FileStorage s = new FileStorage();
			try {
				s.ReplaceFile(file);
			} catch (InterruptedException e) {

			}
			response.sendRedirect("Edit?id=" + file.GetId());
		}
	}

	private String GetHTMLForm() throws InterruptedException, IOException{
		UserStrorage ub = new UserStrorage();
		List<String> users = ub.GetUserList();
		if(users != null){
			if(!users.isEmpty()){
				if(users.contains(name))
					users.remove(name);
			}
		}
		List<String> alreadyShared = file.GetShared();
		String buffer ="";
		buffer += "<div id = \"Form\">";
		buffer += "</br></br>";
		buffer += "</br></br>";
		buffer += "You can edit and share <b>only</b> your files </br>";
		buffer += "<form action=\"Edit\" method=\"post\" >";
		buffer += "<label>Caption : </label></br>";
		buffer += "<input type=\"text\" name=\"caption\" value=" + file.GetCaption() + "></br>";
		buffer += "<label>Note :</label></br>";
		buffer += "<input type=\"text\" name=\"note\"  value=" + file.GetNote() + "></br>";
		if(users!=null){
			if(!users.isEmpty()){
				buffer += "<label>Share to users: </br>";
				for(int i = 0; i < users.size(); ++i){
					if(alreadyShared != null && !alreadyShared.isEmpty()){
						if(alreadyShared.contains(users.get(i))){
							buffer += "<input type=\"checkbox\" name=\"user\"  value=\"" + users.get(i) + "\" checked>" + users.get(i) + "</br>";
							continue;
						}
					}
					buffer += "<input type=\"checkbox\" name=\"user\"  value=\"" + users.get(i) + "\">" + users.get(i) + "</br>";
				}
			}
		}
		buffer += "<input type=\"submit\" value=\"Edit\"></br>";
		buffer += " </form>";
		buffer += "</div>";
		return buffer;
	}

	private String GetHTMLHead(){
		String buffer = "<!DOCTYPE html>";
		buffer += "<html>";
		buffer += "<head>";
		buffer += "<title>CatBox free file sharing service</title>";
		buffer += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />";
		buffer += "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />";
		buffer += "</head>";
		buffer += "<body>";
		buffer += "<div id=\"Container\">";
		buffer += "<div id=\"Head\">";
		buffer += "<div id=\"HeadImage\">";
		buffer += "<img src=\"catbox.png\"/>";
		buffer += "</div>";
		buffer += "<div id=\"HeadCaption\">";
		buffer += "<h1><a href=\"index.html\">CatBox</a></h1>";
		buffer += "\"First\" free file share service in the world!";
		buffer += "</div>";
		buffer += "</div>";
		buffer += "<div id=\"Content\"><div id=\"InfoForm\">";
		buffer += "<p align=\"left\">Hello  " + name +"!</p>";
		buffer += "<p align=\"right\"><a href=\"LogOut\">logout</a></p></br></br>";
		buffer += "<p align=\"right\"><a href=\"Global\">Back</a></p>";
		buffer += "</br>";
		return buffer;
	}

	private String GetHTMLFooter(){
		String buffer = "</div></div>";
		buffer += "<div id=\"Footer\">";
		buffer += "<center>Maria Isaeva 2016&#169;</center>";
		buffer += "</div>";
		buffer += "</div>";
		buffer += "</body>";
		buffer += "</html>";
		return buffer;
	}

}
