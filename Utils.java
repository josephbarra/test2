package org.usfirst.frc.team20.robot;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Utils {
	
	public float getCameraAngle()
	{
		    float  angleToTurn = 500f;
			try {
				System.out.println("*******Trying to turn angle");
				angleToTurn = Float.parseFloat(readSocket("10.0.20.9", 9999, "009"));
				System.out.println("*******Turned angle  ********** = " + angleToTurn );
			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		   return angleToTurn;
	}	
	
	
	
	public String readSocket(String ipAddress, int Port, String sentence ) throws IOException
	{
				int timeOut =1500;
		        String socketString = "-1";
			//	BufferedReader inFromUser =
		    //      new BufferedReader(new InputStreamReader(System.in));
		          DatagramSocket clientSocket;
				try {
						clientSocket = new DatagramSocket();
					  //  clientSocket.setSoTimeout(timeOut);
						InetAddress IPAddress = InetAddress.getByName(ipAddress);
						byte[] sendData = new byte[1024];
						byte[] receiveData = new byte[1024];
						//String sentence = inFromUser.readLine();
						System.out.println("sentence = " + sentence);
						sendData = sentence.getBytes();
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, Port);
					
						clientSocket.send(sendPacket);
						DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
						clientSocket.receive(receivePacket);
						String modifiedSentence = new String(receivePacket.getData());
						System.out.println("FROM SERVER:" + modifiedSentence);
						socketString = modifiedSentence;
						clientSocket.close();
						System.out.println("done");
					 } catch (SocketException e) {
					// TODO Auto-generated catch block
					System.out.println("Socket error: " + e.toString());
				}
			
				return socketString;
	}

}
