import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.junit.Test;

import Models.UserModel;


public class UserStorageTest {

	
	static Semaphore sem = new Semaphore(1);
	static Semaphore semm = new Semaphore(2);
	
	private List<UserModel> users = new ArrayList<UserModel>(1000);
	
    public void Load() throws InterruptedException, IOException{    
       sem.acquire();
 	   if(users == null)
 		   users = new ArrayList<UserModel>();
 	   if(!users.isEmpty())
 		   users.clear();
 	   
 	   File file = new File("test.txt");
 	   
       
     	if (!file.exists()) {
     		sem.release();
            return;
      	}
              
       try (BufferedReader br = new BufferedReader(new FileReader(file))) {
    	    String line;
    	    while ((line = br.readLine()) != null) {
    	    	if(line.length() > 0 && line.contains("%")){
    	    		String[] splited = line.split("%");
    	    		if(splited.length == 2){
    	    			UserModel buffer = new UserModel(splited[0],splited[1]);
    	    			users.add(buffer);
    	    		}
    	    	}
    	    }
    	}
       sem.release();
    }
    
    public List<String> GetUserList() throws InterruptedException, IOException{
    	Load();
		semm.acquire();
    	if(users == null){
			semm.release();
			return null;
    	}
    	if(users.isEmpty()){
			semm.release();
			return null;
    	}
    	List<String> buffer = new ArrayList<String>();
    	for(int i = 0; i < users.size(); ++i){
    		buffer.add(users.get(i).GetLogin());
    	}
		semm.release();
    	return buffer;
    }
    
    public void Save() throws InterruptedException, IOException{	
    
    	sem.acquire();
	  	File file = new File("test.txt");
	
	  	if (!file.exists()) {
	  		file.createNewFile();
	  	}
	
	  	FileWriter fw = new FileWriter(file.getAbsoluteFile());
	
	  	for(int i = 0; i < users.size(); ++i){
	  		fw.write(users.get(i).toString() + System.lineSeparator());
	  	}
	  	fw.close();
	  	sem.release();
  	       	
    }
    
	public boolean ValidUser(String login, String password) throws InterruptedException, IOException{
		semm.acquire();
		Load();
		
		if(users != null){
			UserModel buff = new UserModel(login,password);
			for(int i = 0; i < users.size(); ++i){
				if(users.get(i).GetLogin().equals(login)){
					semm.release();
					return false;
				}
			}
			semm.release();
			return true;
		}
		semm.release();
		return false;
	}
	
	public boolean SingIn(String login, String password) throws InterruptedException, IOException{
		semm.acquire();
		Load();
		
		if(users != null){
			UserModel buff = new UserModel(login,password);
			if(users.contains(buff)){
				semm.release();
				return true;
			}
		}
		semm.release();
		return false;
	}
	
	public void AddNewUser(String login, String password) throws InterruptedException, IOException{
		Load();
		semm.acquire();
		UserModel buffer = new UserModel(login, password);
		if(!users.contains(buffer)){
			users.add(buffer);
		}
		semm.release();
		Save();
	}   
	
	public int GetSize(){
		return users.size();
	}
	
	@Test
	public void test() throws InterruptedException, IOException {
	  	File file = new File("test.txt");
		
	  	if (file.exists()) {
	  		file.delete();
	  	}
		UserStorageTest test = new UserStorageTest();
		test.AddNewUser("misha1990", "123456");
		test.AddNewUser("masha1996", "123456");
		test.AddNewUser("sasha2000", "123456");
		
		assertEquals(3,test.GetSize());
		
		assertEquals(true,UserModel.CheckString("misha1990"));
		
		assertEquals(false,UserModel.CheckString("UberJe$$usKillTR@MP"));
		
		test.Save();
		test.Load();
		
		assertEquals(3,test.GetSize());
		
		assertEquals(true,test.SingIn("misha1990", "123456"));
		assertEquals(false,test.ValidUser("misha1990", "123456"));
		
		test.AddNewUser("peter", "123456");
		
		test.Save();
		test.Load();
		
		assertEquals(4,test.GetSize());
		
		List<String> s = test.GetUserList();
		
		assertEquals(test.GetSize(),s.size());
		
	  	if (file.exists()) {
	  		file.delete();
	  	}
	}

}
