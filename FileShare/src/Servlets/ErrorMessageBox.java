package Servlets;

public class ErrorMessageBox {

	public static String GetMessageBox(String innerMessage){
	    
		String buffer = "<!DOCTYPE html>";
		buffer += "<html>";
		buffer += "<head>";
		buffer += "<title>CatBox free file sharing service</title>";
		buffer += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />";
		buffer += "<style>";
		buffer += "body {";
		buffer += "background: #efeae2;";
		buffer += "}";
		buffer += "div {";
		buffer += "position: absolute;";
		buffer += "margin: auto;";
		buffer += "top: 0;";
		buffer += "right: 0;";
		buffer += "bottom: 0;";
		buffer += "left: 0;";
		buffer += "width: 640px;";
		buffer += "height: 180px;";
		buffer += "text-align: center;";
		buffer += "border-top-left-radius: 22px;";
		buffer += "border-top-right-radius: 22px;";
		buffer += "border-bottom-right-radius: 22px;";
		buffer += "border-bottom-left-radius: 22px;";
		buffer += "line-height: 180px;";
		buffer += "background: #e9ecf4;";
		buffer += "border: 2px solid #556c87;";
		buffer += "}";
		buffer += "</style>";
		buffer += "</head>";
		buffer += "<body>";
		buffer += "<div>";
		buffer += innerMessage;
		buffer += "</div>";
		buffer += "</body>";
		buffer += "</html>";
		return buffer;
	}
	
}
