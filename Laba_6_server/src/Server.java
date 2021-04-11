import data.netData.Report;
import data.netData.ReportState;
import data.netData.Request;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {
    private int PORT;
    private InetAddress address;
    private DatagramSocket socket;

    private Scanner scanner;

    public Server(int port, Scanner scanner) {
        PORT = port;
        this.scanner = scanner;
    }

    public void run() {
        try {
            socket = new DatagramSocket(2468);

            while (true)
                clientRequest();
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public void clientRequest() {
        Request request = null;
        Report report = null;

        try {
            byte[] accept = new byte[16384];
            DatagramPacket getPacket = new DatagramPacket(accept, accept.length);

            //Getting a new request from client
            socket.receive(getPacket);
            request = deserialize(getPacket);
            System.out.println("Command has been got: " + request.getCommandName());

            //Save path to client
            address = getPacket.getAddress();
            PORT = getPacket.getPort();

            //Trying to send a string from console mode in blocking configuration
            System.out.print("Enter the answer: ");
            String simpleAnswer = scanner.nextLine();
            //String simpleAnswer = "OKOKOKOKOK";

            //Sending a report to client
            report = new Report(ReportState.OK, simpleAnswer);
            byte[] sendBuffer = serialize(report);
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, PORT);
            socket.send(sendPacket);
            System.out.println("Sending to " + sendPacket.getAddress() + ", message: " + report.getReportBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private <T> T deserialize(DatagramPacket getPacket) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(getPacket.getData());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        T request = (T) objectInputStream.readObject();
        byteArrayInputStream.close();
        objectInputStream.close();
        return request;
    }

    private <T> byte[] serialize(T toSerialize) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(toSerialize);
        byte[] buffer = byteArrayOutputStream.toByteArray();
        objectOutputStream.flush();
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        objectOutputStream.close();
        return buffer;
    }
}
