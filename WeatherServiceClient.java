/* Network Communication Using Sockets
 * Author : Pulkit Khemka, Harshdeep Singh Mann
 * Weather Service Client
 */
 
/* Included Libraries */
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.imageio.ImageIO;

/* Class WeatherServiceClient containing the main function and the Client implementation*/
public class WeatherServiceClient          
{
		Socket socket = null;
		PrintWriter out = null;
	   BufferedReader in = null;
	   BufferedWriter bw = null;
	   int n=0;
	   byte[] buffer;
	   int bytesInput;
	   int NoOfBytesRead;
	   public void communicate() /* Communicating over Socket Client */
	   {
	      Scanner sc = new Scanner(System.in);
	      String selection, choice, line;
	      selection = null;
	      boolean flag;
	      try
	      {
	    	  while(true)          /* Displaying different options from A to E to the client */
	    	  {
			      System.out.println("Choose from the following list of commands:");
			      System.out.println("A. Select a location");
			      System.out.println("B. Get temperature at selected location.");
			      System.out.println("C. Get wind direction and speed at selected location.");
			      System.out.println("D. Get image from selected location.");
			      System.out.println("E. Exit");
			      choice = sc.nextLine();
			      flag = false;
	     
			      out.println(choice);                /* Sending data over the Socket */
			      if(choice.equalsIgnoreCase("A")) 	 /* fetching location options and sending selected location*/
			      {
			    	  flag = true;
			    	  line = in.readLine();		
			    	  while(!line.equalsIgnoreCase("done"))
			    	  {
			    		  System.out.println(line);
			    		  line = in.readLine();
			    	  }
			    	  selection = sc.nextLine();
			    	  out.println(selection);
			    	 if(in.readLine().equals("invalid selection")) /* Handling invalid selected location*/
			    	  {
			    		  System.out.println("Invalid Selection");
			    		  selection = null;
			    	  }
			      }
			      
			      if(choice.equalsIgnoreCase("B")||choice.equalsIgnoreCase("C")) /* fetching selected location's temperature or wind data*/
			      {
			    	  flag = true;
			    	  if(selection == null)
			    	  {
			    		  System.out.println("Please select a location first \n");
			    		  continue;
			    	  }
			    	  else
			    	  {
			    		  line = in.readLine();
			    		  System.out.println(line + "\n");
			    	  }			    	 
			      }
			      
			      if(choice.equalsIgnoreCase("D"))/* fetching selected location's weather image and saving it on client computer*/
			      {
			    	  flag = true;
			    	  if(selection == null)
			    	  {
			    		  System.out.println("Please select a location first \n");
			    		  continue;
			    	  }
			    	  else
			    	  {
				    	  
	                      
	                      String size = readResponse(socket.getInputStream());
	
	                      int expectedSize = Integer.parseInt(size);
	                      ByteArrayOutputStream baos = new ByteArrayOutputStream(expectedSize);
	                      buffer = new byte[1024];
	                      NoOfBytesRead = 0;
	                      bytesInput = 0;
	                     
	                      while (NoOfBytesRead < expectedSize) {
	                          bytesInput = socket.getInputStream().read(buffer);
	                          NoOfBytesRead += bytesInput;
	                          baos.write(buffer, 0, bytesInput);
	                      }
	                      baos.close();
	                      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	                      BufferedImage image = ImageIO.read(bais);
	                      bais.close();
	                      ImageIO.write(image, "jpg", new File("image"+ ++n + ".jpg"));
	                      System.out.println("Image saved to image"+ n +".jpg");
			    	  }
			      }
			      
			      if(choice.equalsIgnoreCase("E")) /* exiting the client when selected option 'E'*/
			      {
			    	 System.out.println("You have exited the program");
			    	 break;
			      }
			      
			      if(!flag)
			    	  System.out.println("Invalid Choice");
	    	  }
	    	  
	    	  
	      }
	      catch (IOException e)
	      {
	         System.out.println("Read failed");
	         System.exit(1);
	      }
	      
	      
	   }
	   
	   protected String readResponse(InputStream is) throws IOException {
           StringBuilder sb = new StringBuilder(128);
           int in = -1;
           while ((in = is.read()) != '\n') {
               sb.append((char) in);
           }
           return sb.toString();
       }
	  
	   public void listenSocket(String host, int port)           /* Creating Socket Connection */
	   {
	      
	      try
	      {
		 socket = new Socket(host, port);
		 out = new PrintWriter(socket.getOutputStream(), true);                     /* for sending data from Client to Server */
		 in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  /*for sending data from Server to Client */
	      } 
	      catch (UnknownHostException e) 
	      {
		 System.out.println("Unknown host");
		 System.exit(1);
	      } 
	      catch (IOException e) 
	      {
		 System.out.println("No I/O");
		 System.exit(1);
	      }
	   }

	   public static void main(String[] args) /* Main Function with argument as hostname and port number*/
	   {
	      if (args.length != 2)
	      {
	         System.out.println("Usage:  client hostname port");
	         System.exit(1);
	      }

	      WeatherServiceClient client = new WeatherServiceClient();

	      String host = args[0];
	      int port = Integer.valueOf(args[1]);
	      client.listenSocket(host, port);
	      client.communicate();
	   }
	

}
