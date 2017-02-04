package Models;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileModel {
	String mainOwner;
	List<String> sharedUsers;
	String caption;
	String note;
	String path;
	int id;
	
	public static FileModel Create(String fromFile){
		FileModel buffer = new FileModel();
		List<String> seporated = new ArrayList<String>();
		Pattern p = Pattern.compile("\\{(.*?)\\}");
		Matcher regexMatcher = p.matcher(fromFile);

		while (regexMatcher.find()) {//Finds Matching Pattern in String
			seporated.add(regexMatcher.group(1));//Fetching Group from String
		}
		
		if(seporated.size() == 6){
		
			buffer.id = Integer.parseInt(seporated.get(0).trim());
			buffer.mainOwner = seporated.get(1);
			buffer.caption = seporated.get(3);
			buffer.note = seporated.get(4);
			buffer.path = seporated.get(5);
			String shared = seporated.get(2).trim();
			if(!shared.isEmpty()){
				String[] preparse = shared.split("\\,");
				for(int i = 0; i < preparse.length; ++i){
					buffer.AddShared(preparse[i]);
				}
			}
		}
		else
			return null;
		return buffer;
	}
	
	public FileModel(){
		
	}
	
	public String GetType(){
		if(!this.path.contains(".")){
			return "";
		}
		String[] seporated = this.path.split("\\.");
		return seporated[seporated.length - 1];
	}
	
	public String toString(){
		String buffer = "{" + this.id + "}";
		buffer += "{" + this.mainOwner + "}";
		if(sharedUsers!=null){
			if(!sharedUsers.isEmpty()){
				buffer += "{";
				for(int i = 0; i < sharedUsers.size(); ++i){
					if(i + 1 != sharedUsers.size()){
						buffer += sharedUsers.get(i) + ",";						
					}
					else{
						buffer += sharedUsers.get(i);
					}
				}
				buffer += "}";
			}
			else{
				buffer += "{}";
			}
		}
		else{
			buffer += "{}";
		}
		buffer += "{" + this.caption + "}";
		buffer += "{" + this.note + "}";
		buffer += "{" + this.path + "}";
		return buffer;
	}
	
	public FileModel(String mainOwner, String caption, String note, String path){
		this.mainOwner = mainOwner;
		this.caption = caption;
		this.note = note;
		this.path = path;
	}
	
	public List<String> GetShared(){
		if(sharedUsers == null)
			return null;
		return sharedUsers;
	}
	
	public void SetShared(List<String> sharedUsers){
		this.sharedUsers = sharedUsers;
	}
	
	public void SetShared(String[] sharedArray){
		if(sharedArray == null)
			sharedUsers = null;
		else{
			if(sharedUsers == null)
				sharedUsers = new ArrayList<String>();
			else
				sharedUsers.clear();
			for(int i = 0;i < sharedArray.length; ++i){
				sharedUsers.add(sharedArray[i]);
			}
		}
	}
	
	public boolean UserInShared(String userName){
		if(sharedUsers != null){
			if(sharedUsers.contains(userName)){
				return true;
			}
		}
		return false;
	}
	
	public void AddShared(String username){
		if(sharedUsers == null)
			sharedUsers = new ArrayList<String>();
		if(!sharedUsers.contains(username))
			sharedUsers.add(username);
	}
	
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof FileModel))return false;
	    FileModel otherFile = (FileModel)other;
		if(this.id == otherFile.GetId())
			return true;
		return false;			
	}
	
	public boolean UserIsOwner(String userName){
		if(mainOwner.equals(userName))
			return true;
		return false;
	}
	
	public int GetId(){
		return id;
	}
	
	public void SetId(int id){
		this.id = id;
	}
	
	public void SetPath(String path){
		this.path = path;
	}
	
	public void SetNote(String note){
		this.note = note;
	}
	
	public void SetCaption(String caption){
		this.caption = caption;
	}
	
	public void SetOwner(String owner){
		this.mainOwner = owner;
	}
	
	public String GetPath(){
		return path;
	}
	
	public String GetNote(){
		return note;
	}
	
	public String GetCaption(){
		return caption;
	}
	
	public String GetOwner(){
		return mainOwner;
	}
}
