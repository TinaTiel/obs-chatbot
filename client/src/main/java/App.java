import obs.EchoClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
//        SpringApplication.run(App.class, args);
        //String endpoint = "wss://echo.websocket.org/";
        String endpoint = "ws://localhost:4444/";
        try {
            // client in this example connects in the constructor instead of here
            EchoClient echoClient = new EchoClient(endpoint);
            EchoClient badPracticeClient = new EchoClient(endpoint);
            EchoClient broadcastClient = new EchoClient(endpoint);
            echoClient.sendSomeMessage("{\"request-type\": \"GetVersion\",\"message-id\":\"" + echoClient.getSession().getId() + "\"}" );
            badPracticeClient.sendSomeMessage("{\"request-type\": \"GetVersion\",\"message-id\":\"foobarbaz\"}");
            broadcastClient.sendSomeMessage("{\"request-type\": \"BroadcastCustomMessage\",\"message-id\":\"" + broadcastClient.getSession().getId() + "\",\"realm\": \"myCustomRealm\",\"data\": {\"foo\":\"bar\"} }" );

            // We need to keep the program running to we can receive a response
            // Otherwise it will exit before we get a response
            System.out.println("Press ENTER key to exit.");
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
