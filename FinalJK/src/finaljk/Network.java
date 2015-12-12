package finaljk;

import java.lang.Math;
import java.io.*;
import java.net.*;
import java.util.*;

class Network {
    
    private static int bitLoss = 3; // ~5% of packets will be dropped.
    private static DatagramPacket ReturnPacket;
    private static DatagramPacket ReceivePacket;
    private static DatagramSocket networkReceiveSocket;
    private static DatagramSocket networkSendSocket;
    private static InetAddress NetworkIPAddress;
    private static InetAddress IPAddress;
    private static boolean sent = false;
    private static byte[] receiveData = new byte[1024];
    private static byte[] sendData = new byte[1024];
        
    
    
    public static void main(String args[]) throws Exception {
        networkSendSocket = new DatagramSocket(7006);
        networkReceiveSocket = new DatagramSocket(7007);
        NetworkIPAddress = InetAddress.getByName("localhost");
        
        
        
            while (true) {
            sent = false;
            ReceivePacket = new DatagramPacket(receiveData, receiveData.length);
            forward(ReceivePacket);
            
            
            
            
            
            
            ReturnPacket = new DatagramPacket(receiveData, receiveData.length);
            acknowledge(ReturnPacket);
            
            
            
            
        }
    }
    
    
    
    
    
        public static void forward (DatagramPacket sendPacket) throws IOException{
            do {
            networkSendSocket.receive(ReceivePacket);
            String sentence = new String(ReceivePacket.getData());
            System.out.println("RECEIVED: " + sentence);
            IPAddress = ReceivePacket.getAddress();
            sendData = sentence.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7008);
            
            if(!drop()){
            networkSendSocket.send(sendPacket);
            sent=true;
            }
            else{
                System.out.println("Drop Packet " + sentence);
                
            }
            
            }
            while (!sent);
           
        }
            
        
        
        
        
        public static void acknowledge (DatagramPacket ReturnPacket) throws IOException{
            networkReceiveSocket.receive(ReturnPacket);
            String sentence2 = new String(ReceivePacket.getData());
            
            DatagramPacket FinalPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7005);
            
            if (!drop()){
            networkReceiveSocket.send(FinalPacket);
            System.out.println("RETURNED " + sentence2 );
            }
            else{
                System.out.println("Drop Packet " + sentence2);
            }
            
        }
        
        public static boolean drop () throws IOException{
            int minimum = 0;
            int maximum = 100;
             
            int randomNum = minimum + (int)(Math.random() * maximum);
            if (randomNum > bitLoss){
                return false;
                
            }
            else return true;
            
        }
    }



