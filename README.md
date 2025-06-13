# Multimedia-Streaming-Server
*Project for University of West Attica multimedia course*


## Project Description:
This program uses  FFMpeg to generate and stream files of resolutions 240p-1080p and file types avi, mp4 and mkv. <br> Essentially, it's made up of three projects:

- Streaming Load Balancer
- Streaming Server
- Streaming Client

The **Load Balancer** connects the client to the next available server.

The **Server** uses FFmpeg to generate files and streams a file of the client's choosing.

The **Client** runs a speed test on themselves, and can choose from the Server's files depending on Bandwidth.<br>
Speed test monitoring and file choice are handled with Java Swing GUI.

Information and errors is logged in command line, and also seperate log files.

Client-server program runs on localhost, but could be modified to function remotely.



## Usage:

First, place the source files in StreamingServer/src/videos .

Run StreamingLoadBalancer and one or more instances of StreamingServer.

After the server(s) are done generating files, run StreamingClient.

Wait for speed test to be over and choose a file through the GUI.



## Libraries used:

- Log4j2
- [FFmpeg](https://ffmpeg.org/)
- [FFmpeg CLI Wrapper for Java](https://github.com/bramp/ffmpeg-cli-wrapper)
- [JSpeedTest](https://github.com/bertrandmartel/speed-test-lib)
- Java Swing
- Eclipse WindowBuilder

## Screenshots:

![image](https://github.com/user-attachments/assets/31ce7340-d05e-4ad7-b56a-62e74ef4ec7e)
![image](https://github.com/user-attachments/assets/fb81e6a3-dd8d-4dec-8378-24e411a5b786)



