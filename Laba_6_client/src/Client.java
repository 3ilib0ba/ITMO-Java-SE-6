import data.netData.Report;
import data.netData.Request;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Scanner;
import java.util.Set;

public class Client {
    private String host;
    private int port;
    private DatagramChannel channel;
    private Selector selector;
    private SocketAddress address;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(16384);
    private Scanner scanner;

    public Client(String host, int port, Scanner scanner) {
        this.host = host;
        this.port = port;
        this.scanner = scanner;
    }

    public void run() {
        Report answer;

        try {
            address = new InetSocketAddress(host, port);
            channel = DatagramChannel.open();
            channel.connect(address);
            //channel.configureBlocking(false);
            //while (true) {
                sendRequest();

                answer = getAnswer();
                System.out.println(answer.getReportBody());
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRequest()
            throws IOException{
        Request request = new Request("HelloCommand", "");
        byteBuffer = ByteBuffer.wrap(serialize(request));
        channel.send(byteBuffer, address);
    }

    private Report getAnswer()
            throws IOException, ClassNotFoundException {
        byteBuffer = ByteBuffer.allocate(16384);
        address = channel.receive(byteBuffer);
        return deserialize();
    }

    private byte[] serialize(Request request) throws IOException {
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

    private Report deserialize() throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteBuffer.array());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Report response = (Report) objectInputStream.readObject();
        byteArrayInputStream.close();
        objectInputStream.close();
        return response;
    }

    private Report receive() throws IOException, ClassNotFoundException {
        DatagramChannel channel = null;
        while (channel == null) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            for (SelectionKey key : selectionKeys) {
                if (key.isReadable()) {
                    channel = (DatagramChannel) key.channel();
                    channel.read(byteBuffer);
                    byteBuffer.flip();
                    channel.register(selector, SelectionKey.OP_WRITE);
                    break;
                }
            }
        }
        return deserialize();
    }
}
