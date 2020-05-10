import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Script extends JFrame implements ActionListener{
	static ArrayList<File> files = new ArrayList<>();
	static byte current = 0; // to know current method is get all branches
	static Vector<String> branchesName = new Vector<>();
	static String path = "/media/elkhafagy/Elkhafagy/Elkhafagy/special/VFES-DXL-ACCOUNT";//


	static String parentBranch;
	static String newBranch;
	static String branchVersion;
	 JPanel panel;
	   JComboBox<String> branchesCombo;
	   JTextField branchName_text,branchVersion_text;
	   JButton submit;
	   Script() {
		   
		   Path directory = Paths.get(path);

			try {
				gitAllBranches(directory);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      // Username Label
		   branchesCombo = new JComboBox<String>(branchesName);
		   branchName_text = new JTextField();
		   branchVersion_text= new JTextField();
	      // Submit
	      submit = new JButton("RUN");
	      panel = new JPanel(new GridLayout(4, 1));
	      panel.add(branchesCombo);
	      panel.add(branchName_text);
	      panel.add(branchVersion_text);
	      panel.add(submit);
	      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      // Adding the listeners to components..
	      submit.addActionListener(this);
	      add(panel, BorderLayout.CENTER);
	      setTitle("Please Login Here !");
	      setSize(450,350);
	      setVisible(true);
	   }
	
	   
	   
	   /*
	    * Run create branche with modify Files 
	    * 
	    * */
	   
	   
	   /*
	    * 1- get all branch
	    * 2- select parent branch
	    * 3- get new branch name
	    * **************************************
	    * check out parent branch
	    * create new branch
	    * loop all files and check - jenkins , deployment , pom -
	    * send to modify methods
	    * add .
	    * commit
	    * push
	    * 
	    * */
	   public static void createBranchWithChnages(String parentBranch,String newBranch) throws IOException, InterruptedException {
		   Path directory = Paths.get(path);

//			gitAllBranches(directory);

		   gitCeckout(directory, parentBranch);
		   gitNewBranch(directory, newBranch);
		   listAllFiles(path);
		   gitStage(directory);
		   gitCommit(directory, "shwayet t3delat zy el fol y 7assan");
	   }
	   	
	   public static void modifyJenkinsAndPomAndDeployment() {
			int count = 0;
			for (int i = 0; i < files.size(); i++) {
				if (files.get(i).getName().startsWith("Jenkinsfile") ||files.get(i).getName().startsWith("deployment") ) 
					modifyFile(files.get(i).getPath(), branchVersion);
				if(files.get(i).getName().startsWith("pom.xml"))
					modifyPomXMl(files.get(i).getPath(), branchVersion);
				
		}
	   }
	// jenkins and deployment files
	public static void modifyFile(String filePath, String branchVersion) {
		File fileToBeModified = new File(filePath);
System.out.println("//////////////////////////////////////////////");
System.out.println(filePath);
System.out.println("/////////////////////////////////////////////");
		String oldContent = "";

		BufferedReader reader = null;

		FileWriter writer = null;

		try {
			reader = new BufferedReader(new FileReader(fileToBeModified));

			// Reading all the lines of input text file into oldContent

			String line = reader.readLine();

			while (line != null) {
				//		IMAGE_NAME = '${IMAGE_PREFIX}-faultmanagement-6.2.${VERSION}-${GIT_COMMIT}'	
				if (line.contains("IMAGE_PREFIX-")||line.contains("${IMAGE_PREFIX}-")) {
					System.out.println(line);
					String[] arr = line.split("-");
					String versions[] = arr[2].split("\\.");
					String editBranch = branchVersion + "." + versions[versions.length-1];
					String lineEdited = arr[0] + "-" + arr[1] + "-" + editBranch + "-" + arr[3];
					System.out.println(lineEdited);
					
					oldContent = oldContent + lineEdited + System.lineSeparator();
					
				}else {
					
					oldContent = oldContent + line + System.lineSeparator();
//					line = reader.readLine();	
				}
				line = reader.readLine();
				
			}
//			 System.out.println(oldContent);

			// Replacing oldString with newString in the oldContent

//			 String newContent = oldContent.replaceAll(oldString, lineEdited);

			// Rewriting the input text file with newContent

			writer = new FileWriter(fileToBeModified);

			writer.write(oldContent);
			// writer.write(newContent);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// Closing the resources

				reader.close();

				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// init value was 6.0.1-SNAPSHOT

	public static final String xmlFilePath = "/media/elkhafagy/Elkhafagy/Elkhafagy/special/VFES-DXL-Fault-Management/pom.xml";
	public static void modifyPomXMl(String filePath,String commonVersion) {
		
		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.parse(filePath);

			// Get employee by tag name
			// use item(0) to get the first node with tage name "employee"
			Node employee = document.getElementsByTagName("properties").item(0);

			if(employee!=null) {
				System.out.println("//////////////////////////////////////////////");
				System.out.println(filePath);
				System.out.println("/////////////////////////////////////////////");
				
			// update employee , set the id to 10
			NamedNodeMap attr = employee.getAttributes();
			Node nodeAttr = attr.getNamedItem("common.version");

			// loop the employee node and update salary value, and delete a node
			NodeList nodes = employee.getChildNodes();

			for (int i = 0; i < nodes.getLength(); i++) {

				Node element = nodes.item(i);

				if ("common.version".equals(element.getNodeName())) {
					System.out.println("*********************************");
					System.out.println(element.getTextContent());
					String arr[] = element.getTextContent().split("-");
					String newVersion = commonVersion + "-" + arr[1];
					element.setTextContent(newVersion);
					System.out.println("********************************");
				}

			}

			// write the DOM object to the file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);

			StreamResult streamResult = new StreamResult(new File(filePath));
			transformer.transform(domSource, streamResult);

			System.out.println("The XML File was ");

			// write the content on console
			//
		/*	DOMSource source = new DOMSource(document);
			System.out.println("-----------Modified File-----------");
			StreamResult consoleResult = new StreamResult(System.out);
			transformer.transform(source, consoleResult);*/
			
			}
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException sae) {
			sae.printStackTrace();
		}
	}

	// Uses Files.walk method
	public static void listAllFiles(String path) {
		System.out.println("In listAllfiles(String path) method");
		try (Stream<Path> paths = Files.walk(Paths.get(path))) {
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
			modifyJenkinsAndPomAndDeployment();
//			renameJenkinsFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void renameJenkinsFile() {
		int count = 0;
		for (int i = 0; i < files.size(); i++) {
			if (files.get(i).getName().startsWith("jenkinsfile"))
				files.get(i).renameTo(new File(files.get(i).getPath() + "1"));
			// System.out.println(files.get(i).getPath());
		}
	}

	public static void runJenkinsScript() throws IOException, InterruptedException {
		Path directory = Paths.get(path);

		gitAllBranches(directory);

		for (int i = 0; i < branchesName.size(); i++) {
			String s = branchesName.get(i).replaceAll("[*\\s]+", "");

			System.out.println("***************************");
			System.out.println("=" + s);
			gitCeckout(directory, s);
			gitPull(directory, s);
			listAllFiles(path); // for rename Jenkins file
			gitStage(directory); // git add .
			gitCommit(directory, "change jenkins name " + i);
			gitPush(directory, s);

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
		// gitPush(directory);
	}

	public static void gitInit(Path directory) throws IOException, InterruptedException {
		runCommand(directory, "git", "init");
	}

	public static void gitStage(Path directory) throws IOException, InterruptedException {
		runCommand(directory, "git", "add", ".");
	}

	public static void gitCommit(Path directory, String message) throws IOException, InterruptedException {
		runCommand(directory, "git", "commit", "-m", message);
	}

	public static void gitPush(Path directory, String branName) throws IOException, InterruptedException {
		runCommand(directory, "git", "push", "origin", branName);
	}

	public static void gitPull(Path directory, String branName) throws IOException, InterruptedException {
		runCommand(directory, "git", "pull", "origin", branName);
	}

	public static void gitAllBranches(Path directory) throws IOException, InterruptedException {
		current = 1;
		runCommand(directory, "git", "branch", "-a");
	}

	public static void gitCeckout(Path directory, String branchName) throws IOException, InterruptedException {
		current = 0;

		runCommand(directory, "git", "checkout", branchName);

	}

	public static void gitNewBranch(Path directory, String branchName) throws IOException, InterruptedException {

		runCommand(directory, "git", "checkout","-b", branchName);

	}
	
	public static void gitClone(Path directory, String originUrl) throws IOException, InterruptedException {
		runCommand(directory.getParent(), "git", "clone", originUrl, directory.getFileName().toString());
	}

	public static void runCommand(Path directory, String... command) throws IOException, InterruptedException {
		Objects.requireNonNull(directory, "directory");
		if (!Files.exists(directory)) {
			throw new RuntimeException("can't run command in non-existing directory '" + directory + "'");
		}
		ProcessBuilder pb = new ProcessBuilder().command(command).directory(directory.toFile());
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

	public static void main(String[] args) throws IOException, InterruptedException {

		// runJenkinsScript();
new Script();
//		modifyPomXMl(xmlFilePath,"44.44.44");
//		modifyFile(jenkinsPath, "6.9.1");		
/*
String s="				IMAGE_NAME = '${IMAGE_PREFIX}-product-service-6.0.${VERSION}-${GIT_COMMIT}'		\n"
		+ "		IMAGE_NAME = '${IMAGE_PREFIX}-product-inventory-management-${RELEASE}.${VERSION}-${GIT_COMMIT}' \n"
		+ "IMAGE_PREFIX = 'ga'// prod & prodref prefix\n" + 
		"		//IMAGE_PREFIX = 'rc'// prefix for all namespaces other than prod and prodref\n" + 
		"    	VERSION = VersionNumber([projectStartDate: '2017-08-01',versionNumberString: '${BUILDS_ALL_TIME}', versionPrefix: '']);\n" + 
		"		ECR_CREDENTIALS_ID = 'ecr:eu-central-1:jenkins-dxl-es-ecr'\n"
		+ "IMAGE_NAME = '${IMAGE_PREFIX}-admintool-6.2.${VERSION}-${GIT_COMMIT}'	";

String s1=s.replaceAll("[0-9\\.]+","5.5.5"+".");

Pattern p = Pattern.compile("(IMAGE_PREFIX\\}?)-([a-zA-Z\\-?a-zA-Z]+)-([0-9\\\\.]+)");
Matcher m = p.matcher(s);
String result = "";
while(m.find()){
    result = s.replaceAll(m.group(3),"9.50"+".");
}
System.out.print(result);
*/
//System.out.println(s1);

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
					if (current == 1)
						branchesName.add(line);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		parentBranch=((String)branchesCombo.getSelectedItem()).replaceAll("[*\\s]+", "");;
		newBranch=branchName_text.getText();
		branchVersion=branchVersion_text.getText();
		
		try {
		
			createBranchWithChnages(parentBranch, newBranch);
		
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

}