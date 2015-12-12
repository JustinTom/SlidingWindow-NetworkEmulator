/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author thilinaratnayake
 */
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HostA {

    public static int pax = 15; // Number of packets to send
    public static int window = 5;
    public static int seqNum = 0;
    public static ArrayList<Packet> packetsContainer;

    public static int maxPacketSent;

    public static ArrayList<threadID> threadList = new ArrayList<>();
    
    public static DatagramSocket listenSocket;
    public static DatagramSocket sendSocket;

    public static boolean SendMode;

    public static Timer timer;

    public static void main(String args[]) throws Exception {

        packetsContainer = new ArrayList<>();

        //IP address you are sending to
        InetAddress IP = InetAddress.getByName("localhost");
         listenSocket = new DatagramSocket(9006);
         sendSocket = new DatagramSocket();
        

        while (true) {
            System.out.println("What do you want to do? Press 1 to SEND and 2 to RECEIVE");
            Scanner scan = new Scanner(System.in);
            int input = scan.nextInt();
            System.out.println("You chose" + input);

            byte[] sendData = new byte[1024];

            if (input == 1) {
                SEND();
            } else {
                RECEIVE();
            }

        }

    }

    public static void SEND() throws Exception {
        
        
        byte[] sendData = new byte[1024];
        InetAddress IP = InetAddress.getByName("localhost");
        //Create a socket for you as the client to send on
        DatagramSocket clientSocket = new DatagramSocket();
        
        Packet EOTpacket = new Packet(3, pax, 5, pax);

               
        // IF PACKETS CONTAINER IS NOT EMPTY: 
        // set the packets in the container to PSH
        // Fill the empty
        
        if (!packetsContainer.isEmpty()) {
            System.out.println("PAX CONTAINER is NOT EMPTY");
            System.out.println("Moving on");
            
            //set each packet that is already in the container to type PSH and NOT LOSS
            for (Packet packet1: packetsContainer) {
                packet1.setPacketType(1);
                System.out.println("packet "+packet1.getSeqNum() +"set to PSH now");
            }
            
            int gap = window - packetsContainer.size();
            System.out.println("There are " + gap + "more packets that can be added to the array to send.");
            
            for (int i = 0; i < gap; i++) {
                int startNum = seqNum;
                //packet params are: (int pType, int sNum, int wSize, int aNum)
                Packet packet = new Packet(1, seqNum, 5, seqNum);
                System.out.println("EmptyPacketMethodPAX created "+seqNum);
                //Add this pcaket into the container
                packetsContainer.add(packet);
                System.out.println(packetsContainer.get(i).getSeqNum() + "Added");
                seqNum++;
            }
            
            //If our EOT packet is in there, grab the element position. Delete all the rest
            int eotNum;
            if (packetsContainer.contains(EOTpacket)){
                for (int i = 0; i < packetsContainer.size(); i++) {
                    if (packetsContainer.get(i).getPacketType() == EOTpacket.getSeqNum()){
                        eotNum = i;
                       while(packetsContainer.isEmpty())
                       {
                           packetsContainer.indexOf(i);
                           System.out.println("test this shizss");
                       }
                    }
                }
                
                
            }
            
            
        } 
        //If CONTAINER EMPTY:
        //create the packets to fill array
        else {
            
            for (int i = packetsContainer.size(); i < window; i++) {
                
                System.out.println("WINDOW = " + window);
                System.out.println("seqNum= " + seqNum);
                System.out.println("i= " + i);

                //packet params are: (int pType, int sNum, int wSize, int aNum)
                Packet packet = new Packet(1, seqNum, 5, seqNum);
                //Add this pcaket into the container
                packetsContainer.add(packet);
                System.out.println(packetsContainer.get(i).getSeqNum() + "Added");
                    //Prep the packet and send data

                seqNum++;

            }

        }
        
        //FINALLY: Send all the pax in the container
        for(int i = 0; i < packetsContainer.size(); i++) {
              
            sendData = prepPacket(packetsContainer.get(i));
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IP, 7005);
            clientSocket.send(sendPacket);
            System.out.println("PACKET # " + packetsContainer.get(i).getSeqNum() + "SENT");
            //System.out.println("LOOK HERE LOOK HERE");
            System.out.println("Window is " + window);
            System.out.println("i =" +i);

                    //Create the timer for last packet
            // If its the last packet, create a timer
            if (i == packetsContainer.size()-1) {
                System.out.println("LAST PACKET SENT!!");
                HostA.maxPacketSent = seqNum;
                System.out.println("Max Packet is now " + seqNum);
                timer = new Timer(String.valueOf(seqNum));
                timer.schedule(new timeOut() {
                }, 500);
                System.out.println("TIMER CREATED for last packet, seq number " + seqNum);
                //Create a threadID object that will be held in a container for easy access later
                threadID elton = new threadID(seqNum, timer);
                threadList.add(elton);
                clientSocket.close();
            }
        
        }
        System.out.println("SEND completed, there are this many elements in the array");
        System.out.println(packetsContainer.size());

        System.out.println("GOING INTO RECEIVE MODE");
        RECEIVE();

    }
    
    public static void exit(){
        System.out.println("Reached the exit");
    }
    public static void postConversation() {
        try {
            System.out.println("For that last transmission, window size was " + window);
            System.out.println("The amount of packets lost was " + HostA.packetsContainer.size());
            int success = window - packetsContainer.size();
            float successRate = success / window;
            System.out.println("Success rate is " + successRate + "%");
            Thread.sleep(5000);
            System.out.println("Sleep Finished! sending again");
            HostA.SEND();
        } catch (InterruptedException ex) {
            Logger.getLogger(HostA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(HostA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void RECEIVE() {

        try {
            int networkListen = 7006;

            InetAddress IP = InetAddress.getByName("localhost");

            //Create a socket for the clients to connec to you on.
            
            //DatagramSocket sendSocket = new DatagramSocket();

            //Define the size of byte arrays that will hold data
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];

            System.out.println("socket created, waiting to receive");

            // While this loop is true, keep listening
            while (true) {
                try {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    listenSocket.receive(receivePacket);
                    String received = new String(receivePacket.getData());

                    receiveData = receivePacket.getData();

                    ByteArrayInputStream in = new ByteArrayInputStream(receiveData);
                    ObjectInputStream is = new ObjectInputStream(in);

                    Packet packet = (Packet) is.readObject();

                    System.out.println("RECEIVED PACKET " + packet.getSeqNum());

                    
                    if(packet.getPacketType()==3){
                        System.out.println("LAST PACKET HAS BEEN RECEIVED! \n\n\n\n");
                        Thread.sleep(10000000);
                    }
                    
                    // Delete from array the ones that have come in
                    checkArray(packet);
                    
                    

                } catch (IOException ex) {
                    Logger.getLogger(HostA.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(HostA.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(HostA.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(HostA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void checkArray(Packet packet) {

        //If the packet supplied exists in the array, remove it.
        for (Packet packetsContainer1 : packetsContainer) {
        }
        for (int i = 0; i < packetsContainer.size(); i++) {
            if (packetsContainer.get(i).getSeqNum() == packet.getSeqNum()) {
                packetsContainer.remove(i);

            }
        }

        System.out.println("PACKETS still waiting to be acked " + packetsContainer.size());

        //If ALL PACKETS RECEIVED (there are no packets left in the container), stop timer and then time to get ready to SEND AGAIN
        if (packetsContainer.size() == 0) {
            try {
                //Cancel running timer
                timer.cancel();

                System.out.println("ALL PACKETS ACKED!");
                System.out.println("Number of elements in the array is now = " + HostA.packetsContainer.size());
                System.out.println("LAST PACKET STATE");
                postConversation();

            } catch (Exception ex) {
                Logger.getLogger(HostA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static byte[] prepPacket(Packet packet) {
        //Serialize packet object into a bytearray
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream os;
        try {
            os = new ObjectOutputStream(outputStream);
            os.writeObject(packet);
        } catch (IOException ex) {
            Logger.getLogger(HostA.class.getName()).log(Level.SEVERE, null, ex);
        }

        return outputStream.toByteArray();

    }

    public static void setLoss() {
        //@ TIMEOUT: set any packet that HASNT been set to ACK as LOSS
        //if it exists in array, that means it hasn't been acked and therefore LOST
        for (Packet packetsContainer1 : packetsContainer) {
            if (packetsContainer1.getPacketType() != 2) {
                packetsContainer1.setPacketType(4);

            }
        }

        System.out.println("TIME OUT Losses set. Number of lost packets is " + packetsContainer.size());

        postConversation();

    }
}

abstract class timeOut extends TimerTask {

 

    

    public void run() {
        System.out.println("TIME OUT! Proceeding to set losses");
        HostA.setLoss();
        //If all other packets in the array are LOSS, then that means 

    }

}