import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 2468);
        client.run();
    }
}
