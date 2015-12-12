package finaljk;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class client4 {

    private static Timer timer;
    private static ArrayList<Packet> Window;
    private static int receivePacketNumber = 0;
    private static DatagramPacket sendPacket;
    private static DatagramSocket clientSocket;
    private static InetAddress ClientIPAddress;
    public static ArrayList<Packet> PacketArray;
    private static int[] checkedPackets;
    private static int[] receivedPackets;
    private static int WindowSize = 10;
    private static int seqNumber = 0;
    private static int totalPackets = 100;
    private static int packetNumber = 0;
    
    private static byte[] receiveData = new byte[1024];
    private static byte[] sendData = new byte[1024];
    private static int bitLoss = 5; // ~5% of packets will be dropped. 
//int numofpacketsto sned. (window size)

    public static void main(String args[]) throws Exception {
        PacketArray = new ArrayList<Packet>();
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

            PrepareWindow();

            for (int c = 0; c < Window.size(); c++) {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(outputStream);
                os.writeObject(PacketArray.get(l));
                byte[] sendData = outputStream.toByteArray();
                sendPacket = new DatagramPacket(sendData, sendData.length, ClientIPAddress, 7006);
                Send(sendPacket);
                Packet packet2 = (Packet) Window.get(l);
                
                System.out.println("SENT: Packet " + packet2.getSeqNum());

                l++;
                packetNumber++;

            }
            

            k = 0;
            l = 0;
            receivePacketNumber = 0;

            clientSocket.setSoTimeout(2000);

            for (i = 0; i < WindowSize; i++) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                try {
                    clientSocket.receive(receivePacket);
                    byte[] receivedata = receivePacket.getData();

                    ByteArrayInputStream in = new ByteArrayInputStream(receivedata);
                    ObjectInputStream is = new ObjectInputStream(in);

                    try {

                        Packet packet2 = (Packet) is.readObject();
                        System.out.println("Packet object received = " + packet2);

                        if (packet2.getPacketType() == 4) {
                            System.out.println("EOT Received, Goodbye!");
                            System.exit(0);
                        }
                        CheckOffReceivedPackets(packet2);
                        receivePacketNumber++;

                    } catch (ClassNotFoundException e) {

                        e.printStackTrace();

                    }

                } catch (Exception e) {
                    
                    System.out.println("Timeout on packet " + PacketArray.get(0).getSeqNum());

                    break;
                }

            }
            //clientSocket.close();
        }
        
        
    }

    

    public static ArrayList CreatePackets(String message) throws IOException {
        int i;

        for (i = 0; i < totalPackets; i++) {
            Packet packet = new Packet(1, i, WindowSize, i);
            PacketArray.add(packet);
        }
        Packet finalPacket = new Packet(3, totalPackets, WindowSize, totalPackets);
        PacketArray.add(finalPacket);
        return PacketArray;

    }

    public static void Send(DatagramPacket Packets) throws IOException {
        clientSocket.send(sendPacket);
        //send(Packets);
    }

    public static void CheckOffReceivedPackets(Packet packet) {
        //if packet is received, array at that seqnumber becomes 1. 1 means received. 0 means re-send. 
        for (Packet PacketArray1 : PacketArray) {

        }
        for (int i = 0; i < PacketArray.size(); i++) {
            if (PacketArray.get(i).getSeqNum() == packet.getSeqNum()) {
                PacketArray.remove(i);
            }
        }
        PacketArray.remove(packet);
    }

    public static void PrepareWindow() {
        int i;
        Window = new ArrayList(WindowSize);
        if (PacketArray.size() < WindowSize) {
            for (i = 0; i < PacketArray.size(); i++) {
                Window.add(PacketArray.get(i));
                
            }
        
            
        } else {
            for (i = 0; i < WindowSize; i++) {
                Window.add(PacketArray.get(i));
            }
        }

    }



}
