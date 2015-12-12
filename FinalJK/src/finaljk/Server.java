package finaljk;

import java.io.*;
import java.net.*;

class Server {

    private static DatagramPacket ReturnPacket;
    private static DatagramPacket ReceivePacket;
    private static DatagramSocket serverSocket;
    private static DatagramSocket networkSendSocket;
    private static InetAddress ServerIPAddress;
    private static InetAddress IPAddress;
    private static byte[] receiveData = new byte[1024];
    private static byte[] sendData = new byte[1024];

    public static void main(String args[]) throws Exception {

        serverSocket = new DatagramSocket(7008);
        ServerIPAddress = InetAddress.getByName("localhost");

        while (true) {
            ReceivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(ReceivePacket);
            
            
            String sentence = new String(ReceivePacket.getData());
            
            System.out.println("Server Received: " + sentence);
            IPAddress = ReceivePacket.getAddress();
            sendData = sentence.getBytes();
           
            
    

            
            
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7007);
            serverSocket.send(sendPacket);
        }
    }
}
