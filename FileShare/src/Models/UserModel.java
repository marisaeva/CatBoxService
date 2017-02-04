package Models;

import java.util.Random;

public class UserModel{
	
	private String login;
	private String password;
	
	public UserModel(String login, String password){
		this.login = login;
		this.password = password;
	}
	
	public String GetLogin(){
		return this.login;
	}
	
	public String GetPassword(){
		return this.password;
	}
	
	public String toString(){
		return login + "%" + password;
	}
	
	//for example, can add some new symbols for validation
	private static String[] BadSymbols = {
		"!","@","#","$","%","^","&","*","(",")","|"," "
	};
	
	public static Boolean CheckString(String str){
		if(str.contains(" "))
			return false;
		for(int i = 0 ; i < BadSymbols.length; ++i){
			if(str.contains(BadSymbols[i]))
				return false;
		}
		return true;
	}
	
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof UserModel))return false;
		UserModel otherUser = (UserModel)other;
		if(this.login.equals(otherUser.GetLogin()) && this.password.equals(otherUser.GetPassword()))
			return true;
		return false;			
	}
	
}
