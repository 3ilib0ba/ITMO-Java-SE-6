import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.Scanner;

public class Server {
    private int PORT;
    private DatagramChannel channel;
    private InetAddress address;
    private DatagramSocket socket;
    private Scanner scanner;

    public Server(int port){
        PORT = port;
    }

    public void run() {
        System.out.println("Server is running");
        boolean processStatus = true;

        //while (processStatus) {
            processStatus = clientRequest();
        //}
    }

    public boolean clientRequest() {
        try {
            socket = new DatagramSocket(2468);

            byte[] buffer = new byte[1000];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            socket.receive(packet);
            System.out.println("Package has been got: " + deserialize(packet));
            address = packet.getAddress();
            PORT = packet.getPort();

            byte[] sendBuffer = serialize("With love from server");
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, PORT);
            socket.send(sendPacket);
            System.out.println("Sending to " + sendPacket.getAddress() + ", message: " + deserialize(sendPacket));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private String deserialize(DatagramPacket getPacket) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(getPacket.getData());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        String request = (String) objectInputStream.readObject();
        byteArrayInputStream.close();
        objectInputStream.close();
        return request;
    }

    private byte[] serialize(String response) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(response);
        byte[] buffer = byteArrayOutputStream.toByteArray();
        objectOutputStream.flush();
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        objectOutputStream.close();
        return buffer;
    }

    public static void main(String[] args) {
        Server server = new Server(2468);
        server.run();
    }
}
