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
        Request request;
        Report answer;
        try {
            channel = DatagramChannel.open();
            address = new InetSocketAddress("localhost", port);
            channel.connect(address);
            channel.configureBlocking(false);
            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_WRITE);


            while (true) {
                request = null; // new initialization
                sendRequest(request);

                answer = null; // new initialization
                answer = getAnswer();
                System.out.println("Received from server: " + answer.getReportBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                selector.close();
            } catch (IOException e) {
                System.out.println("Selector can't be closing");;
            }
        }
    }

    private void sendRequest(Request request)
            throws IOException {
        System.out.print("Enter the command: ");
        String command = scanner.nextLine();
        request = new Request(command, "");

        byteBuffer = ByteBuffer.wrap(serialize(request));

        // sending from selector with unblocking configuration
        selector.select();
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        for (SelectionKey key : selectionKeys) {
            if (key.isWritable()) {
                DatagramChannel datagramChannel = (DatagramChannel) key.channel();
                datagramChannel.write(byteBuffer);
                datagramChannel.register(selector, SelectionKey.OP_READ);
                break;
            }
        }
        byteBuffer.clear();

    }

    private Report getAnswer()
            throws IOException, ClassNotFoundException {
        byteBuffer = ByteBuffer.allocate(16384);

        DatagramChannel datagramChannel = null;
        while (datagramChannel == null) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            for (SelectionKey key : selectionKeys) {
                if (key.isReadable()) {
                    datagramChannel = (DatagramChannel) key.channel();
                    datagramChannel.read(byteBuffer);
                    byteBuffer.flip();
                    datagramChannel.register(selector, SelectionKey.OP_WRITE);
                    break;
                }
            }
        }
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
