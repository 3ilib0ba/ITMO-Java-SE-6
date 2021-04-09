import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Client {
    private String host;
    private int port;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(16384);

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {

        try {
            SocketAddress socket = new InetSocketAddress(host, port);
            DatagramChannel channel = DatagramChannel.open();
            channel.connect(socket);
            //channel.configureBlocking(false);

            byteBuffer = ByteBuffer.wrap(serializable("FIRST message"));
            channel.send(byteBuffer, socket);

            ByteBuffer answer = ByteBuffer.allocate(16384);
            socket = channel.receive(answer);
            System.out.println(deserialize(answer));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private byte[] serializable(String request) throws IOException {
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

    private String deserialize(ByteBuffer byteBuffer) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteBuffer.array());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        String response = (String) objectInputStream.readObject();
        byteArrayInputStream.close();
        objectInputStream.close();
        return response;
    }
}
