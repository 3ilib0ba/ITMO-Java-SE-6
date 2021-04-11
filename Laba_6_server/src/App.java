public class App {

    public static void main(String[] args) {
        System.out.println("Server is running");

        Server server = new Server(2468);
        server.run();
    }
}
