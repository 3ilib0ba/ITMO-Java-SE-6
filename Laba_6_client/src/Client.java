import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;


public class Client {
    private String host;
    private int port;
    private Console console;
    private DatagramSocket socket;
    private SocketAddress address;
    //private ByteBuffer byteBuffer = ByteBuffer.allocate(16384);

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        try {
            byte[] buffer = new byte[1000];
            socket = new DatagramSocket();
            //DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            address = new InetSocketAddress("localhost", this.port);
            socket.connect(address);

            byte[] sending = serializable("Жопа АДМИНА");
            socket.send(new DatagramPacket(sending, sending.length, address));
            System.out.println("Отправлено на сервер: " + "Жопа АДМИНА");

            System.out.println("Получение с сервера");
            DatagramPacket answer = new DatagramPacket(buffer, buffer.length);
            socket.receive(answer);
            System.out.println(deserialize(answer));

            //String otvet = deserialize(packet);
            //System.out.println(otvet);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    private byte[] serializable(String request) throws IOException{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(request);
        byte[] buffer = byteArrayOutputStream.toByteArray();
        objectOutputStream.flush();
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        objectOutputStream.close();
        return buffer;
    }

    private String deserialize(DatagramPacket getPacket) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(getPacket.getData());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        String response = (String) objectInputStream.readObject();
        byteArrayInputStream.close();
        objectInputStream.close();
        return response;
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 2468);
        client.run();
    }
}
