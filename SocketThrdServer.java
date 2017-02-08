/* Network Communication Using Sockets
 * Author : Pulkit Khemka, Harshdeep Singh Mann
 * Weather Service Server
 */
 
/* Included Libraries */
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;

/* Class WeatherServiceServer containing the Server thread implementation*/
class WeatherServiceServer implements Runnable
{
	private Socket client;
	String[] locations, temp, wind,imagenames;
	   WeatherServiceServer(Socket client) 
	   {
	      this.client = client;
	   }

	   public void run()
	   {
		   try {
					/*Reading the input text file*/
				   BufferedReader reader =new BufferedReader(new FileReader("Input.txt"));
				   String line1 = "";
				   line1 = reader.readLine();
				   this.locations = line1.trim().split(";");
				   line1 = reader.readLine();
				   this.temp = line1.trim().split(";");
				   line1 = reader.readLine();
				   this.wind = line1.trim().split(";");
				   line1 = reader.readLine();
				   this.imagenames = line1.trim().split(";");
				   reader.close();
				}
			catch (IOException e1) {
				e1.printStackTrace();
			}
		   System.out.println("Client Connected");
	      String choice, line, selection = null;
	      int i;
	      BufferedReader in = null;
	      PrintWriter out = null;

	      try 
	      {
			
			 in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			 out = new PrintWriter(client.getOutputStream(), true);
	      } 
	      catch (IOException e) 
	      {
			 System.out.println("in or out failed");
			 System.exit(-1);
	      }

	      try 
	      {
	    	  while(true)
	    	  {
				/*reading the choice from client*/
	    		  choice = in.readLine();
	    		  
				  /*if the choice is 'A', list of locations is sent and the selection is received*/
	    		  if(choice.equalsIgnoreCase("A"))
	    		  {
	    			  for(i=0;i<locations.length;i++)
	    			  {
	    				  line = (i+1) +"  "+ locations[i];
	    				  out.println(line);
	    			  }
	    			  out.println("done");
	    			  selection = in.readLine();
	    			  int intselection = 0;
	    			  try
	    			  {
	    				  intselection = Integer.parseInt(selection);
	    			  }
	    			  catch(NumberFormatException e)
	    			  {
	    				  selection = null;
	    				  out.println("invalid selection");
	    				  System.out.println("Invalid Selection");	
	    			  }
	    			  
	    			  if((intselection<(locations.length+1))&&(intselection>0))
	    			  {
	    				  System.out.println("Selected " + locations[intselection-1]);
	    			  		out.println("valid selection");
	    			  }
	    		  }
	    		  
				  /*if the choice is 'B', temperature of the selected location is sent*/
	    		  if(choice.equalsIgnoreCase("B"))
	    		  {
	    			  if(selection!=null)
	    			  {
	    			  System.out.println("Sending " + locations[(Integer.parseInt(selection)-1)]+ " temperature.");
	    			  line = "Temperature in " + locations[(Integer.parseInt(selection)-1)] + ": " + temp[(Integer.parseInt(selection)-1)];
	    			  out.println(line);
	    			  }
	    		  }
	    		  
				  /*if the choice is 'B', wind reading of the selected location is sent*/
	    		  if(choice.equalsIgnoreCase("C"))
	    		  {
	    			  if(selection!=null)
	    			  {
	    			  System.out.println("Sending " + locations[(Integer.parseInt(selection)-1)]+ " wind reading.");
	    			  line = "Wind reading in " + locations[(Integer.parseInt(selection)-1)] + ": " + wind[(Integer.parseInt(selection)-1)];
	    			  out.println(line);
	    			  }
	    		  }
	    		  
				  /*if the choice is 'D', image of the selected location is sent*/
	    		  if(choice.equalsIgnoreCase("D"))
	    		  {
	    			  
	    			  if(selection!=null)
	    			  {
	    				  System.out.println("Sending " + locations[(Integer.parseInt(selection)-1)]+ " current image.");
		    			  BufferedImage br=ImageIO.read(new File(imagenames[(Integer.parseInt(selection)-1)] + ".jpg"));
		    			  ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    		      ImageIO.write(br, "jpg", baos);
		    		      baos.close();
		    		      client.getOutputStream().write((Integer.toString(baos.size()) + "\n").getBytes());
		    		      client.getOutputStream().write(baos.toByteArray());
	    			  }
    		  	  }
				  /*if the choice is 'E', the program is exited*/
	    		  if(choice.equalsIgnoreCase("E"))
	    		  {
	    			  break;
    		  }
			
	    	  }
	      } 
	    	  
	      catch (IOException e) 
	      {
			 System.out.println("Read failed");
			 System.exit(-1);
	      }

	      try 
	      {
	    	  client.close();
	    	  System.out.println("Client Disconnected");
	      } 
	      catch (IOException e) 
	      {
			 System.out.println("Close failed");
			 System.exit(-1);
	      }
	   }
	}

	/* Class SocketThrdServer containing the main thread*/
	public class SocketThrdServer 
	{
	   ServerSocket server = null;

	   public void listenSocket(int port)
	   {
	      try
	      {
		 server = new ServerSocket(port); 
		 System.out.println("Server running on port " + port + 
		                     "," + " use ctrl-C to end");
	      } 
	      catch (IOException e) 
	      {
		 System.out.println("Error creating socket");
		 System.exit(-1);
	      }
	      while(true)
	      {
	    	  WeatherServiceServer w;
	         try
	         {
	            w = new WeatherServiceServer(server.accept());
	            Thread t = new Thread(w);
	            t.start();
		 } 
		 catch (IOException e) 
		 {
		    System.out.println("Accept failed");
		    System.exit(-1);
	         }
	      }
	   }

	   protected void finalize()
	   {
	      try
	      {
	         server.close();
	      } 
	      catch (IOException e) 
	      {
	         System.out.println("Could not close socket");
	         System.exit(-1);
	      }
	   }

	   /* main function with argument as port number*/
	   public static void main(String[] args)
	   {
	      if (args.length != 1)
	      {
	         System.out.println("Usage: java SocketThrdServer port");
	         System.exit(1);
	      }

	      SocketThrdServer server = new SocketThrdServer();
	      int port = Integer.valueOf(args[0]);
	      server.listenSocket(port);
	   }
}
