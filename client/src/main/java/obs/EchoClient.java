package obs;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class EchoClient {

    private Session session;

    public EchoClient(String endpoint) {
        try {
            URI uri = new URI(endpoint);
            System.out.println("URI: " + uri);
            WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
            System.out.println("Container is: " + webSocketContainer);
            webSocketContainer.connectToServer(this, uri);

        } catch (Exception e) {
            System.out.println("Encountered exception while connecting to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected! Session info: ");
        System.out.println("URI: " + this.session.getRequestURI());
        System.out.println("Session ID: " + this.session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message: " + message);
        System.out.println("Session id is still: " + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Session closed");
        this.session = null;
    }

    @OnError
    public void onError(Session session, Throwable exception) {
        System.out.println("On Error, exception: " + exception);
        exception.printStackTrace();
    }

    public void sendSomeMessage(String yourMessage) {
        System.out.println("Sending message: " + yourMessage);
        session.getAsyncRemote().sendText(yourMessage);
    }

}
