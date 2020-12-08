import obs.EchoClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
//        SpringApplication.run(App.class, args);
        String endpoint = "wss://echo.websocket.org/";
        try {
            // client in this example connects in the constructor instead of here
            EchoClient echoClient = new EchoClient(endpoint);
            echoClient.sendSomeMessage("Here is a message");
            echoClient.sendSomeMessage("Another message");

            // We need to keep the program running to we can receive a response
            // Otherwise it will exit before we get a response
            System.out.println("Press ENTER key to exit.");
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
