import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Script {
	static ArrayList<File> files = new ArrayList<>();
	static byte current=0; // to know current method is get all branches
	static ArrayList<String> branchesName= new ArrayList<>();
	static String path="//media//elkhafagy//Elkhafagy//TestJenkins//AskFM";



		public static void main(String[] args) throws IOException, InterruptedException {
	  
		runJenkinsScript();	
		
		
	      }
	     
	
	
	     // Uses Files.walk method
	     public static void listAllFiles(String path){
	         System.out.println("In listAllfiles(String path) method");
	         try(
	        		 Stream<Path> paths = Files.walk(Paths.get(path))) {
	             paths.forEach(filePath -> {
	                 if (Files.isRegularFile(filePath)) {
	                     try {
	                         files.add(new File(filePath.toString()));
	                     } catch (Exception e) {
	                         // TODO Auto-generated catch block
	                         e.printStackTrace();
	                     }
	                 }
	             });
	             renameJenkinsFile();
	         } catch (IOException e) {
	             // TODO Auto-generated catch block
	             e.printStackTrace();
	         }
	     }
	     
	     
	     public static void renameJenkinsFile() {
	    	 int count=0;
	    	 for (int i = 0; i < files.size(); i ++){
	    		 if(files.get(i).getName().equals("jenkinsfile"))
	    	        files.get(i).renameTo(new File(files.get(i).getPath() + "1"));
//	    	 System.out.println(files.get(i).getPath());
	    	 }
	     }
	   
	     
	     /*
	      * Need on this method to add two methods but need password :( 
	      * 1) git fetch 
	      * 2) git push
	      * 
	      * */
	     public static void runJenkinsScript() throws IOException, InterruptedException{
		 		Path directory = Paths.get(path);
		 		
		 		
		 		gitAllBranches(directory);
	    	 
		 		for(String branchName:branchesName) {
		 			
		 			gitCeckout(directory, branchName);
		 			listAllFiles(path); // for rename Jenkins file
		 			gitStage(directory); // git add .
		 			gitCommit(directory, "change jenkins name");
		 			
		 		}
	    	 
	     }
	     

	  // example of usage
	 	private static void initAndAddFile() throws IOException, InterruptedException {
	 		Path directory = Paths.get(path);
	 		Files.createDirectories(directory);
	 		gitInit(directory);
	 		Files.write(directory.resolve("example.txt"), new byte[0]);
	 		gitStage(directory);
	 		gitCommit(directory, "Add example.txt");
	 		
	 	}

	 	// example of usage
	 	private static void cloneAndAddFile() throws IOException, InterruptedException {
	 		String originUrl = "https://github.com/EslamElkhafagy/TestJenkinsFile.git";
	 		Path directory = Paths.get(path);
	 		gitClone(directory, originUrl);
	 		Files.write(directory.resolve("example.txt"), new byte[0]);
	 		gitStage(directory);
	 		gitCommit(directory, "Add example.txt");
	 		gitPush(directory);
	 	}

	 	public static void gitInit(Path directory) throws IOException, InterruptedException {
	 		runCommand(directory, "git", "init");
	 	}

	 	public static void gitStage(Path directory) throws IOException, InterruptedException {
	 		runCommand(directory, "git", "add", "-A");
	 	}

	 	public static void gitCommit(Path directory, String message) throws IOException, InterruptedException {
	 		runCommand(directory, "git", "commit", "-m", message);
	 	}

	 	public static void gitPush(Path directory) throws IOException, InterruptedException {
	 		runCommand(directory, "git", "push");
	 	}
	 	
	 	public static void gitAllBranches(Path directory) throws IOException, InterruptedException {
	 		current=1;
	 		runCommand(directory, "git", "branch","-a");
	 	}
	
	 	public static void gitCeckout(Path directory,String branchName) throws IOException, InterruptedException {
	 		current=0;
	 		if(branchName.equals("* master"))
	 		runCommand(directory, "git", "checkout","master");
	 		else
	 		runCommand(directory, "git", "checkout",branchName);

	 	}

	 	public static void gitClone(Path directory, String originUrl) throws IOException, InterruptedException {
	 		runCommand(directory.getParent(), "git", "clone", originUrl, directory.getFileName().toString());
	 	}

	 	public static void runCommand(Path directory, String... command) throws IOException, InterruptedException {
	 		Objects.requireNonNull(directory, "directory");
	 		if (!Files.exists(directory)) {
	 			throw new RuntimeException("can't run command in non-existing directory '" + directory + "'");
	 		}
	 		ProcessBuilder pb = new ProcessBuilder()
	 				.command(command)
	 				.directory(directory.toFile());
	 		Process p = pb.start();
	 		StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");
	 		StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
	 		outputGobbler.start();
	 		errorGobbler.start();
	 		int exit = p.waitFor();
	 		errorGobbler.join();
	 		outputGobbler.join();
	 		if (exit != 0) {
	 			throw new AssertionError(String.format("runCommand returned %d", exit));
	 		}
	 	}
	 	
	 	private static class StreamGobbler extends Thread {

			private final InputStream is;
			private final String type;

			private StreamGobbler(InputStream is, String type) {
				this.is = is;
				this.type = type;
			}

			@Override
			public void run() {
				try (BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
					String line;
					while ((line = br.readLine()) != null) {
						System.out.println(type + "> " + line);
						if(current==1)
							branchesName.add(line);
					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}

}