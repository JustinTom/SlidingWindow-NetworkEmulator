package finaljk;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class Client2 {

    private static Timer timer;
    private static ArrayList Window;
    private static int receivePacketNumber=0;
    private static DatagramPacket sendPacket;
    private static DatagramSocket clientSocket;
    private static InetAddress ClientIPAddress;
    public static ArrayList PacketArray;
    private static int[] checkedPackets;
    private static int[] receivedPackets;
    private static int WindowSize = 5;
    private static int seqNumber = 0;
    private static int totalPackets = 100;
    private static int packetNumber = 0;
    private static byte[] receiveData = new byte[1024];
    private static byte[] sendData = new byte[1024];
    private static int bitLoss = 5; // ~5% of packets will be dropped. 
//int numofpacketsto sned. (window size)

    public static void main(String args[]) throws Exception {

        checkedPackets = new int[totalPackets - 1];
        int b;

        for (b = 0; b < totalPackets - 1; b++) {
            checkedPackets[b] = 0;
        }

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        clientSocket = new DatagramSocket(7005);
        ClientIPAddress = InetAddress.getByName("localhost");

        System.out.println(ClientIPAddress);

        String sentence = inFromUser.readLine();
        CreatePackets(sentence);

        int i = 0;
        int k = 0;
        int l = 0;

        while (!PacketArray.isEmpty()) {

            do {
                    PrepareWindow();
                    sentence = Window.get(l).toString();
                    sendData = sentence.getBytes();
                    sendPacket = new DatagramPacket(sendData, sendData.length, ClientIPAddress, 7006);
                    Send(sendPacket);
                    System.out.println("SENT: Packet " + sentence);
                    
                    l++;
                    packetNumber++;
                
                    

            } while (l < 5);

            k = 0;
            l = 0;
            receivePacketNumber=0;

            clientSocket.setSoTimeout(2000);

            for (i = 0; i < WindowSize; i++) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                try {
                    clientSocket.receive(receivePacket);
                    CheckOffReceivedPackets(0);
                    String modifiedSentence = new String(receivePacket.getData());
                    System.out.println("FROM SERVER:" + modifiedSentence);
                    receivePacketNumber++;
                } catch (Exception e) {
                    System.out.println("Timeout on packet " + PacketArray.get(0));
                    break;
                }

            }
            //clientSocket.close();
        }

    }

    public static ArrayList CreatePackets(String message) throws IOException {
        PacketArray = new ArrayList();

        int i;
        for (i = 0; i < totalPackets; i++) {

            PacketArray.add(message + String.valueOf(i));

        }

        return PacketArray;

    }

    public static void Send(DatagramPacket Packets) throws IOException {
        clientSocket.send(sendPacket);
        //send(Packets);
    }

    public static void CheckOffReceivedPackets(int packet) {
        //if packet is received, array at that seqnumber becomes 1. 1 means received. 0 means re-send. 
        PacketArray.remove(packet);
    }
    public static void PrepareWindow(){
        int i;
        Window = new ArrayList ();
        for (i=0; i< WindowSize; i++){
            Window.add(PacketArray.get(i));
        }
        
    }

}
