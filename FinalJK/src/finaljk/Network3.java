package finaljk;

import java.lang.Math;
import java.io.*;
import java.net.*;
import java.util.*;

class Network3 {
    
    private static int bitLoss = 0; // ~5% of packets will be dropped.
    private static DatagramPacket ReturnPacket;
    private static DatagramPacket ReceivePacket;
    private static DatagramSocket networkReceiveSocket;
    private static DatagramSocket networkSendSocket;
    private static InetAddress NetworkIPAddress;
    private static InetAddress IPAddress;
    private static Packet packet;
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
             byte[] sendData = ReceivePacket.getData();
	
                ByteArrayInputStream in = new ByteArrayInputStream(sendData);
	
                ObjectInputStream is = new ObjectInputStream(in);
	
                try {
	
                    packet = (Packet) is.readObject();
	
                    System.out.println("Packet received = "+packet);
	
                } catch (ClassNotFoundException e) {
	
                    e.printStackTrace();
	
                }
          
            IPAddress = ReceivePacket.getAddress();
            sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7008);
            
            if(!drop()){
            networkSendSocket.send(sendPacket);
            System.out.println("DataPacket Sent " + packet );
            sent=true;
            }
            else{
                System.out.println("Drop Packet " + packet);
                
            }
            
            }
            while (!sent);
           
        }
            
        
        
        
        
        public static void acknowledge (DatagramPacket ReturnPacket) throws IOException{
            networkReceiveSocket.receive(ReturnPacket);
             byte[] sendData = ReturnPacket.getData();
	
                ByteArrayInputStream in = new ByteArrayInputStream(sendData);
	
                ObjectInputStream is = new ObjectInputStream(in);
	
                try {
	
                    packet = (Packet) is.readObject();
	
                    System.out.println("Packet received = "+packet);
	
                } catch (ClassNotFoundException e) {
	
                    e.printStackTrace();
	
                }
            
            DatagramPacket FinalPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7005);
            
            if (!drop()){
            networkReceiveSocket.send(FinalPacket);
            System.out.println("ACK Pack Sent " + packet );
            }
            else{
                System.out.println("Drop Packet " + packet);
            }
            
        }
        
        public static boolean drop () throws IOException{
            Random rand = new Random();
            if (rand.nextInt(100) < bitLoss){
            return true;
        }
          
            else return false;
            
        }
    }



