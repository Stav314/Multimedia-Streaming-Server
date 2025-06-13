package main;

import java.io.*;
//import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;



public class StreamingServer {
	static Logger log = LogManager.getLogger(StreamingServer.class);
	static int clientsConnected = 0; //number of clients connected
	
	
	public static void main(String[] args) throws IOException 
    {
		SSLServerSocket s = null;

		String videosLocation = "src/videos";
		int[][] resolution = { {1920,1080} , {1280,720} , {854,480} , {640,360} , {426,240} }; //All the available resolutions
		String[] filetype = { ".mp4", ".avi", ".mkv" }; //All the available file formats
		
		FFmpeg ffmpeg = new FFmpeg();
        FFprobe ffprobe = new FFprobe();
        //We will use an executor service with three threads, to create missing ffmpeg files three at once 
        ExecutorService executorService = Executors.newFixedThreadPool(3);
		
        
        //Before we even start on the socket server, we need to create the available files
		
        //List the files in the folder
		String[] availableVideos = new File(videosLocation).list();
		
		//Enter the for loop of hell
		for (int i = 0; i < availableVideos.length; i++) { //iterate through all the videos in the folder
			//get video info (title and resolution)
			String videoTitle=availableVideos[i].substring(0,availableVideos[i].lastIndexOf("-"));
			int maxQuality=Integer.valueOf(availableVideos[i].substring(availableVideos[i].lastIndexOf("-")+1,availableVideos[i].lastIndexOf("p.")));
			for (int j=0; j < resolution.length; j++) {
				//check for every resolution equal or lower than current.
				if(maxQuality>=resolution[j][1]) {
					//Check for every filetype if this title+resolution of it exists
					for (int k=0; k < filetype.length; k++) {
						String tempTitle=videoTitle+"-"+resolution[j][1]+"p"+filetype[k];
						//temporary final variables for threads, so i and j can keep iterating
						final int iT = i;
						final int jT = j;
							
						executorService.submit(() -> { 
							//file check needs to happen inside the executor process, or threads all try to create the same file
							if(!new File(videosLocation+"/"+tempTitle).exists()) {
								FFmpegBuilder builder = new FFmpegBuilder()
										.setInput(videosLocation+"/"+availableVideos[iT])
										.overrideOutputFiles(false) 
										.addOutput(videosLocation+"/"+tempTitle)
										.setVideoResolution(resolution[jT][0], +resolution[jT][1])
									.done();
									
									FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
		                            executor.createJob(builder).run();
							}
						});
					}
				}
			}
			
		}
		executorService.shutdown();		
		try
		{
			//waiting for all conversions to finish
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			  log.warn(e);
		}
		

		//Now that executor's run is over, connect to the client(s)
		// Key store config for SSL
		System.setProperty("javax.net.ssl.keyStore", "src/StreamingServer.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "S3cur3Str3am1ng");
		try 
        {
        
			//We start by default at 7071 and assume each server can handle 9 clients.
			//If 7071 is not available, the next port to be bound will be 7081, then 7091 etc
			int socket=7071;
			while(true) {
				try {
					SSLServerSocketFactory sf = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
					s = (SSLServerSocket)sf.createServerSocket(socket);
					break;
				}
				catch (Exception e){
					socket=socket+10;
				}
			}
			log.debug("Running in: " + s);    
			// Infinite loop for getting client request
		    while (true) 
			{	            
		        // socket object to receive incoming client requests
		    	SSLSocket client = (SSLSocket)s.accept();
		    	clientsConnected++;
		    	log.info("A new client is connected : " + client);                
		        log.info("Assigning new thread for client");
		
		        // create a new thread object
		        Thread t = new ClientHandler(client,clientsConnected);
		
		        // Invoking the start() method
		        t.start();
			}
        }
        catch (Exception e){     
        	log.error(e);
        }
        finally {
          	if (s != null) {
            	try {
            		s.close();
            		clientsConnected--;
                }
                catch (IOException e) {
                	log.error(e);
                }
            }
        }
	}   
}
	


//ClientHandler class
class ClientHandler extends Thread 
{
 final SSLSocket s;
 static Logger log = LogManager.getLogger(ClientHandler.class);
 static int clientsConnected;
	 // Constructor
	 public ClientHandler(SSLSocket s,int c)
	 {
	     this.s = s;
	     ClientHandler.clientsConnected=c;
	 }
	
	 @Override
	 public void run() 
	 {
 	 
		 try { 
			 
             // get the outputstream of client
			 ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			// receive the answer from client
			 ObjectInputStream in = new ObjectInputStream(s.getInputStream());
			 int maxQuality = (int) in.readObject();
			 log.info("Client recommended quality is " + maxQuality + "p");
	
			 String videosLocation = "src/videos";
			 String[] availableVideos = new File(videosLocation).list();
			 ArrayList<String> clientVideos = new ArrayList<String>();;
			 			 
			 for (int i = 0; i < availableVideos.length; i++) {
				 if(Integer.valueOf(availableVideos[i].substring(availableVideos[i].lastIndexOf("-")+1,availableVideos[i].lastIndexOf("p.")))<=maxQuality) {
					 clientVideos.add(availableVideos[i]);
				 }
			 }
			 
			 out.writeObject(clientVideos);
			 
			 String clientProtocol = (String)in.readObject();
			 log.info("Client chose protocol: "+ clientProtocol);
			 String filename = (String)in.readObject();
			 log.info("client chose file: "+ filename);
			 out.writeObject(s.getLocalPort()+clientsConnected);

			//Structure the ffmpeg command based on protocol
			 ArrayList<String> command = new ArrayList<>();
			 command.add("ffmpeg");
		     command.add("-re"); //realtime streaming
		     command.add("-i");
		     
		     command.add(videosLocation + "/" + filename);
			 if (clientProtocol.equals("TCP")) {
				 command.add("-f");
			     command.add("mpegts"); //streaming will crash unless we output the data in mpeg-ts format
			     command.add("tcp://127.0.0.1:" + (s.getLocalPort() + clientsConnected) + "?listen");
			 }
			 else if (clientProtocol.equals("UDP")) {
			     command.add("-f");
			     command.add("mpegts");
			     command.add("udp://127.0.0.1:" + (s.getLocalPort() + clientsConnected) + "?listen");
			 }
			 else {

			     command.add("-c:v");
			     command.add("copy");
			     command.add("-an"); //Rtp ignored audio anyway, so this helps with warnings
			     command.add("-f");
			     command.add("rtp");
			     command.add("-sdp_file");
			     command.add("../video.sdp");
			     command.add("-loglevel");
				 command.add("warning"); //show only warnings
			     command.add("rtp://127.0.0.1:" + (s.getLocalPort() + clientsConnected));
			 }
			 

			 log.debug("Running command: " + String.join(" ", command));
			 ProcessBuilder pb = new ProcessBuilder(command);
			 
			 pb.inheritIO().start();
             
	      } catch (IOException | ClassNotFoundException e) {
	        	 log.error(e);
	      } 
	}
}
