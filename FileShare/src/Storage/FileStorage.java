package Storage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import Models.FileModel;

public class FileStorage {
	
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
		
		java.io.File file = new java.io.File("files.txt");
		
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
		file.SetId(lastId + 1);
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
		java.io.File s = new java.io.File(file.GetPath());
		if(s.exists())
			s.delete();
		Save();
		semm.release();
	}
	
	public void Save() throws IOException, InterruptedException{
		sem.acquire();
		java.io.File file = new java.io.File("files.txt");
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
}
