import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.junit.Test;

import Models.FileModel;


public class FileStorageTest {

	static Semaphore sem = new Semaphore(2);
	static Semaphore semm = new Semaphore(1);

	private List<FileModel> files = new ArrayList<FileModel>();
	private int lastId = 0;
	
	public void Load() throws FileNotFoundException, IOException, InterruptedException{
		sem.acquire();
		if(files == null)
			files= new ArrayList<FileModel>();
		if(!files.isEmpty())
			files.clear();
		
		java.io.File file = new java.io.File("test2.txt");
		
		if(!file.exists()){
			sem.release();
			return;
		}
		
	       try (BufferedReader br = new BufferedReader(new FileReader(file))) {
	    	    String line;
	    	    while ((line = br.readLine()) != null) {
	    	    	FileModel buffer = FileModel.Create(line);
	    	    	if(buffer != null){
	    	    		if(buffer.GetId() > lastId)
	    	    			lastId = buffer.GetId();
	    	    		files.add(buffer);
	    	    	}
	    	    }	    	    
	       }
	       sem.release();
	}
	
	public int GetSize() throws FileNotFoundException, IOException, InterruptedException{
		return files.size();
	}
	
	public List<FileModel> GetFilesByUser(String user) throws FileNotFoundException, IOException, InterruptedException{
		semm.acquire();
		Load();
		if(files == null){
			semm.release();
			return null;
		}
		if(files.isEmpty()){
			semm.release();
			return null;
		}
		List<FileModel> buffer = new ArrayList<FileModel>();
		
		for(int i = 0; i < files.size(); ++i){
			FileModel buff = files.get(i);
			if(buff.UserInShared(user)){
				buffer.add(buff);
				continue;
			}
			if(buff.UserIsOwner(user)){
				buffer.add(buff);
				continue;
			}
		}
		
		if(buffer.isEmpty()){
			semm.release();
			return null;
		}
			semm.release();
		return buffer;
	}
	
	public FileModel GetFileById(int id) throws FileNotFoundException, IOException, InterruptedException{
		semm.acquire();
		Load();
		if(files == null){
			semm.release();
			return null;
		}
		if(files.isEmpty()){
			semm.release();
			return null;
		}
		for(int i = 0; i < files.size(); ++i){
			if(files.get(i).GetId() == id){
				semm.release();
				return files.get(i);
			}
		}
		semm.release();
		return null;
	}
	
	public void AddFile(Models.FileModel file) throws InterruptedException, FileNotFoundException, IOException{
		semm.acquire();
		Load();
		if(files == null)
			files = new ArrayList<FileModel>();
		file.SetId(files.size() + 1);
		files.add(file);
		Save();
		semm.release();
	}
	
	public void ReplaceFile(Models.FileModel file) throws InterruptedException, FileNotFoundException, IOException{
		semm.acquire();
		Load();
		if(files == null){
			semm.acquire();
			return;
		}
		files.remove(file);
		files.add(file);
		Save();
		semm.release();
	}
	
	public void DeleteFile(Models.FileModel file) throws FileNotFoundException, IOException, InterruptedException{
		semm.acquire();
		Load();
		if(files == null)
			return;
		if(files.isEmpty())
			return;
		files.remove(file);
		//java.io.File s = new java.io.File(file.GetPath());
		//if(s.exists())
			//s.delete();
		Save();
		semm.release();
	}
	
	public void Save() throws IOException, InterruptedException{
		sem.acquire();
		java.io.File file = new java.io.File("test2.txt");
		if(!file.exists()){
			file.createNewFile();
		}
	  	FileWriter fw = new FileWriter(file.getAbsoluteFile());

	  	if(files != null){
	  		if(!files.isEmpty()){
	  			for(int i = 0; i < files.size(); ++i){
	  				fw.write(files.get(i).toString() + System.lineSeparator());
	  			}
	  		}
	  	}
	  	fw.close();
	  	sem.release();
	}
	
	@Test
	public void test() throws FileNotFoundException, IOException, InterruptedException {
	  	File file = new File("test2.txt");
		
	  	if (file.exists()) {
	  		file.delete();
	  	}
	  	
	  	FileStorageTest test = new FileStorageTest();
	  	
	  	FileModel[] fileArray = new FileModel[10];
	  	
	  	for(int i = 0; i < 10; ++i){
	  		FileModel buf = new FileModel();
	  		buf.SetCaption("A");
	  		buf.SetNote("B");
	  		if(i == 4 || i == 8)
	  			buf.SetOwner("Masha");
	  		test.AddFile(buf);
	  	}
	  	
	  	test.Save();
	  	test.Load();
	  	
	  	assertEquals(10,test.GetSize());
	  	
	  	test.DeleteFile(test.GetFileById(2));
	  	
	  	assertEquals(9,test.GetSize());
	  	
	  	test.Save();
	  	test.Load();
	  	test.Save();
	  	test.DeleteFile(test.GetFileById(1));
	  	test.Save();
	  	test.Load();
	  	
	  	assertEquals(8,test.GetSize());	  	
	  	
	  	assertEquals(2,test.GetFilesByUser("Masha").size());
	  	
	  	FileModel teee = test.GetFileById(3);
	  	teee.SetCaption("boots");
	  	
	  	test.ReplaceFile(teee);
	  	
	  	assertEquals("boots",test.GetFileById(3).GetCaption());
	  	
	  	if (file.exists()) {
	  		file.delete();
	  	}
	}

}
