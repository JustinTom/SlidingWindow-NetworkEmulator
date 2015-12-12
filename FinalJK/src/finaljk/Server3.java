package finaljk;

import static finaljk.client4.PacketArray;
import java.io.*;
import java.net.*;

class Server3 {

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
            IPAddress = ReceivePacket.getAddress();

            byte[] sendData = ReceivePacket.getData();

            ByteArrayInputStream in = new ByteArrayInputStream(sendData);

            ObjectInputStream is = new ObjectInputStream(in);

            Packet packet = (Packet) is.readObject();
            System.out.println("Packet received = " + packet);

            if (packet.getPacketType() == 3) {
                SendEnd(packet);
            } else {
                int seqNum = packet.getSeqNum();
                int windowSize = packet.getWindowSize();

                packet = new Packet(2, seqNum, windowSize, seqNum);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(outputStream);
                os.writeObject(packet);
                sendData = outputStream.toByteArray();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7007);

                serverSocket.send(sendPacket);
                System.out.println("Packet Sent = " + packet);
            }
        }

    }

    public static void SendEnd(Packet packet) throws IOException {
        int seqNum = packet.getSeqNum();
        int windowSize = packet.getWindowSize();

        packet = new Packet(4, seqNum, windowSize, seqNum);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(outputStream);
        os.writeObject(packet);
        sendData = outputStream.toByteArray();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7007);

        serverSocket.send(sendPacket);
        System.out.println("Packet Sent = " + packet);
    }

}
