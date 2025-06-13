package main;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class StreamingClient {
	static Logger log = LogManager.getLogger(StreamingClient.class);
	
	public static void main(String[] args) {

		WaitingPage wp = new WaitingPage();
		wp.frame.setVisible(true);
		log.info("Successfully loaded waiting window.");
		
		// First, connect to load balancer
		int assignedServerPort = 0;
		try {
			Socket lbSocket = new Socket("localhost", 7070);
			ObjectInputStream lbIn = new ObjectInputStream(lbSocket.getInputStream());
			//Get port of next server in line
			assignedServerPort = (int) lbIn.readObject();
			lbSocket.close();
		} catch (ClassNotFoundException | IOException e) {
			log.error("Problem with load balancer: "+e);
		}
		
		// Trust store config for SSL
		System.setProperty("javax.net.ssl.trustStore", "src/StreamingTrust.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "S3cur3Str3am1ng");
		
		SSLSocketFactory sf = (SSLSocketFactory)SSLSocketFactory.getDefault();
		 
		//connect to server
		try (SSLSocket socket = (SSLSocket)sf.createSocket("localhost", assignedServerPort)) {
	          
            // reading from server
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			// Sends output to the socket
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

			//Run speedtest to get client's speed
            SpeedTest st = new SpeedTest(); 
            log.info("Starting speed test.");
            st.testSpeed("http://speedtest.tele2.net/1GB.zip");
            
            while(st.getPercent()!=100) {
            	wp.progressBar.setValue((int) (st.getPercent()*10));
            	TimeUnit.MILLISECONDS.sleep(500); //update progress bar every 0.5 seconds
            }
            
            float speed = st.getSpeed();
            int recommendedQuality = BandwidthToQuality(speed);
    		log.debug("Recommended quality for current speed is " + recommendedQuality +"p");
    		
    		out.writeObject(recommendedQuality); //send that to server
    		//get the videos that client can play
    		@SuppressWarnings("unchecked")
			ArrayList<String> availableVideos= (ArrayList<String>) in.readObject();

    		
    		//start selection window
    		wp.frame.setVisible(false);
    		SelectionPage sp = new SelectionPage(availableVideos,speed);
    		sp.frame.setVisible(true);

    		
    		// Use CountDownLatch to wait for button click
            CountDownLatch latch = new CountDownLatch(1);

            sp.streamButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sp.frame.setVisible(false); // close GUI
                    latch.countDown(); // unblock main thread
                }
            });

            latch.await(); // Wait here until button is clicked
            
            //Now, we initialise our variables
            String title = (String) sp.titleChoice.getSelectedItem();
            String resolution = (String) sp.qualityChoice.getSelectedItem();
            String filetype = (String) sp.filetypeChoice.getSelectedItem();
            String protocol = (String) sp.protocolChoice.getSelectedItem();

            //Change placeholder name "Auto-choice" based on resolution
            if(protocol=="Auto-choice") {
		    	if(resolution=="240p") {
		    		protocol="TCP";
		    	}
		    	else if(resolution=="360p"||resolution=="480p") {
		    		protocol="UDP";
		    	}
		    	else {
		    		protocol="RTP/UDP";
		    	}
		    }

            //Send protocol to server
            out.writeObject(protocol);
    			
    		String filename = title+"-"+resolution+"."+filetype;
    		out.writeObject(filename);
    		int listenSocket = (int)in.readObject();
    		log.info("Listening on port:" + listenSocket);

    		ArrayList<String> command = new ArrayList<>();

    		//Structure the ffplay command based on protocol
    		command.add("ffplay");
			command.add("-loglevel");
			command.add("warning"); //show only warnings and up
			if (protocol.equals("TCP")) {
				command.add("tcp://127.0.0.1:" + listenSocket);
			} 
			else if (protocol.equals("UDP")) {
				command.add("udp://127.0.0.1:" + listenSocket);
			}
			else {
				command.add("-protocol_whitelist");
				command.add("file,rtp,udp");
				command.add("../video.sdp");
			}

			//Run the command through java processbuilder. Then wait for process to finish
			log.debug("Running command: " + String.join(" ", command));
			long playbackStart = System.currentTimeMillis();
			Thread ffplayThread = new Thread(() -> {
			    try {
			        new ProcessBuilder(command).inheritIO().start().waitFor();

			    } catch (IOException | InterruptedException ex) {
			        log.error("Problem with ProcessBuilder thread: "+ex);
			    }
			});
			ffplayThread.start();
			ffplayThread.join();
		
			//Print stats in logger
			log.info("--------- PLAYBACK FINISHED AT " + new java.util.Date() + " ---------");
			log.info("Additional stats:");
		    log.info("File title: \t\t" + title);
		    log.info("Streaming protocol: \t" + protocol);
		    log.info("Bitrate: \t\t" + speed + " Mbps");
			log.info("Streaming duration: \t" + (( System.currentTimeMillis()- playbackStart ) / 1000) + " seconds");
			log.info("---------------------------------------------------------------------");
			System.exit(0); //Exit! Bye bye! :)

		}
        catch (Exception e) {
           log.fatal("Problem with socket: "+e);
        }
	}
	
	//Get recommended quality for given bandwidth
	static int BandwidthToQuality(float bandwidth) {
		if(bandwidth>=4.5) {
			return 1080;
		}
		else if(bandwidth>=2.5) {
			return 720;
		}
		else if(bandwidth>=1) {
			return 480;	
		}
		else if(bandwidth>=0.75) {
			return 360;
		}
		else {
			return 240;
		}
	}
}
