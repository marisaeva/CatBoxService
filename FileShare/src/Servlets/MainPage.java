package Servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;











import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import Storage.FileStorage;

@WebServlet("/Global")
public class MainPage extends HttpServlet {

	private boolean isMultipart;
	private String filePath;
	private int maxFileSize = 64 * 1024 * 1024;
	private int maxMemSize = 4 * 1024;
	private File file ;
	private String name;

	public void init(){
		filePath = 
				getServletContext().getInitParameter("file-upload"); 
	}

	public MainPage() {
		super();
	}

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
		out.println(GetHTMLHead());
		out.println(GetUploadForm());
		out.println(GetFileList());
		out.println(GetHTMLFooter());
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List<FileItem> fileItems;
		try {
			fileItems = upload.parseRequest(new ServletRequestContext(request));

			String caption = null;
			String note = null;
			String sfilePath = null;
			String owner = name;

			Iterator i = fileItems.iterator();
			while ( i.hasNext () ) 
			{
				FileItem fi = (FileItem)i.next();
				if(fi.isFormField()){
					if(fi.getFieldName().equals("caption"))
						caption = fi.getString();
					if(fi.getFieldName().equals("note"))
						note = fi.getString();
				}
			}

			java.io.PrintWriter out = response.getWriter( );
			if(caption == null || caption.isEmpty() || caption.trim().length() < 3){
				out.print(ErrorMessageBox.GetMessageBox("Caption <b> must be more than 3 sybmols! </b>!"));
				out.close();
				return;				
			}
			isMultipart = ServletFileUpload.isMultipartContent(request);
			response.setContentType("text/html");
			if( !isMultipart ){
				out.print(ErrorMessageBox.GetMessageBox("Cannot upload file!"));
				out.close();
				return;
			}
			factory.setSizeThreshold(maxMemSize);
			factory.setRepository(new File("e:\\temp"));

			upload.setSizeMax( maxFileSize );

			try{ 
				i = fileItems.iterator();

				while ( i.hasNext () ) 
				{
					FileItem fi = (FileItem)i.next();
					if ( !fi.isFormField () )	
					{
						fi.getFieldName();
						String fileName = fi.getName();
						fi.getContentType();
						fi.isInMemory();
						fi.getSize();
						if( fileName.lastIndexOf("\\") >= 0 ){
							boolean valid = false;
							int index = 0;
							while(!valid){
								file = new File( filePath + PrepFileName(fileName.substring(fileName.lastIndexOf("\\")),index));								
								++index;
								if(!file.exists())
									valid = true;
							}
							sfilePath = file.getAbsolutePath();

						}else{
							boolean valid = false;
							int index = 0;
							while(!valid){
								file = new File( filePath + PrepFileName(fileName.substring(fileName.lastIndexOf("\\") + 1),index));	
								++index;
								if(!file.exists())
									valid = true;		
							}
							sfilePath = file.getAbsolutePath();
						}
						fi.write( file ) ;
					}
				}
				Models.FileModel buff = new Models.FileModel(name, caption, note, sfilePath);
				FileStorage fil = new FileStorage();
				fil.AddFile(buff);
				response.sendRedirect("Global");
				out.close();
			}catch(Exception ex) {
				out.print(ErrorMessageBox.GetMessageBox("File is BIG, or cannot upload this file! Try again!"));
			}
		} catch (FileUploadException e) {

		}
	}

	private String PrepFileName(String str,int index){
		String container = "";
		if(index > 0){
			if(str.contains(".")){
				String[] splited = str.split("\\.");
				if(splited.length > 0){
					for(int i = 0; i < splited.length; ++i){
						if(i + 2 == splited.length){
							container += splited[i] + index + ".";
							continue;
						}
						container += splited[i];
					}
				}
				return container;
			}
			else{
				container = str + index;
			}
		}
		else
			return str;
		return container;
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
		buffer += "<p align=\"right\"><a href=\"LogOut\">logout</a></p>";
		buffer += "</br></br></br>";
		return buffer;
	}

	private String GetFileList(){
		List<Models.FileModel> files = new ArrayList<Models.FileModel>();
		String buffer = "";
		FileStorage s = new FileStorage();
		try{
			files = s.GetFilesByUser(name);
			if(files != null){
				if(!files.isEmpty()){
					buffer += "</br></br></br>";
					buffer += "<table align = \"center\" border = \"0\" border-style = \"hidden\"  cellpadding=\"6\" cellspacing=\"8\">";

					for(int i = 0; i < files.size(); ++i){
						buffer+="<tr><td><table border = \"1\">";
						Models.FileModel buff = files.get(i);
						buffer += "<tr>";
						buffer += "<td width=\"120\">FileOwner : </td>";						
						buffer += "<td width=\"240\">" + buff.GetOwner() + "</td>";
						buffer += "</tr>";
						buffer += "<tr>";
						buffer += "<td>Caption : </td>";
						if(!buff.GetCaption().equals("null"))
							buffer += "<td>" + buff.GetCaption() + "</td>";
						else
							buffer += "<td></td>";	
						buffer += "</tr>";
						buffer += "<tr>";
						buffer += "<td>Note : </td>";	
						if(!buff.GetNote().equals("null"))
							buffer += "<td>" + buff.GetNote() + "</td>";
						else
							buffer += "<td></td>";	
						buffer += "<tr>";
						buffer += "<td>Download link :</td><td> <a href=\"Download?id=" +  buff.GetId() +  "\" download>Link</a> </td>";						
						buffer += "</tr>";
						if(buff.GetOwner().equals(name)){
							buffer += "<tr>";
							buffer += "<td>Shared for :</td>";
							List<String> users = buff.GetShared();
							if(users != null){
								if(!users.isEmpty()){
									buffer += "<td>";
									for(int j = 0; j < users.size(); ++j)
										buffer += users.get(j) + " ";
									buffer += "</td>";
								}
								else{
									buffer += "<td></td>";
								}
							}
							else{
								buffer += "<td></td>";							
							}
							buffer += "</tr>";
							buffer += "<tr><td>Edit/Share file</td>";
							buffer += "<td> <a href=\"Edit?id=" +  buff.GetId() +  "\">Edit/Share</a> </td>";
							buffer += "</tr/>";
							buffer += "<tr><td>Delete file</td>";
							buffer += "<td> <a href=\"Delete?id=" +  buff.GetId() +  "\">Delete</a> </td>";
							buffer += "</tr/>";
						}
						buffer+="</table></td></tr>";
					}

					buffer +="</table>";
				}
				else{
					return "<center><h2> You have not yet uploaded the files, and other users have shared their files with you </h2><center>";
				}
			}
			else{
				return "<center><h2> You have not yet uploaded the files, and other users have shared their files with you </h2><center>";
			}
		}
		catch(Exception ex)
		{
			return "<center><h2> You have not yet uploaded the files, and other users have shared their files with you </h2><center>";
		}
		return buffer;
	}

	private String GetUploadForm(){
		String buffer = "<div id =\"inner\"><form action=\"Global\" method=\"post\"";
		buffer +="enctype=\"multipart/form-data\">";
		buffer +="<label>File caption : </label>";
		buffer +="<input type=\"text\" name=\"caption\">";
		buffer +="<br/>";
		buffer +="<label>File note : </label>";
		buffer +="<input type=\"text\" name=\"note\">";
		buffer +="<br/>";
		buffer +="<input type=\"file\" name=\"file\" size=\"50\" />";
		buffer +="<br/>";
		buffer +="<input type=\"submit\" value=\"Upload File\" />";
		buffer +="</form></div>";
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
