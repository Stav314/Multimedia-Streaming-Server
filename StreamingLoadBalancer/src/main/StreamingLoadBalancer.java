package main;

import java.io.*;
import java.net.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StreamingLoadBalancer {
    //All the available ports
    private static final int[] servers = {7071, 7081, 7091, 7101, 7111};
    //index of server currently next to be accessed
    private static int i = 0;
    static Logger log = LogManager.getLogger(StreamingLoadBalancer.class);
    
    public static void main(String[] args) {
        log.info("Starting Load Balancer on port 7070"); //always 7070

        try (ServerSocket serverSocket = new ServerSocket(7070)) {
        	while (true) {
        		//No need for threads since load balancer doesn't need to process multiple clients at once. It will be done sequentially.
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            log.error(e);
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            Socket socket = clientSocket;
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
        ) {
            int attempts = 0;
            int assignedPort = -1; //value should change if there is not problems

            //We want the load balancer to assign one client, and then move on to the next available server
            //Round Robin algorithm (going around circularly, one at a time)
            while (attempts < servers.length) //attempt once for each server
            {
                int port = servers[i];
                i = (i + 1) % servers.length;

                try (Socket test = new Socket()) {
                	// test connection in port. If there is a problem with connection, throw a warning. Loop keeps going.
                    test.connect(new InetSocketAddress("127.0.0.1", port)); 
                    assignedPort = port;
                    break;
                } catch (IOException e) {
                    log.warn(e);
                }
                //Even if a server is unavailable, we just skip it and keep searching. Just a warn message that can be ignored.
                attempts++;
            }
            
            //if no server was found
            if (assignedPort == -1) {
                log.error("No available servers for client: " + clientSocket.getInetAddress());
            } else {
                log.info("Redirecting client to port " + assignedPort);
            }
            //send available port to client
            out.writeObject(assignedPort);

        } catch (IOException e) {
            log.fatal(e);
        }
    }
}