package example.classic;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class ExampleClassicClient {

    private Session session;

    public ExampleClassicClient(String endpoint) {
        try {
            URI uri = new URI(endpoint);
            WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
            webSocketContainer.connectToServer(this, uri);

        } catch (Exception e) {
            System.out.println("Encountered exception while connecting to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to " + this.session.getRequestURI()
                + ", with ID " + this.session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Client ID " + session.getId() + " Received message: " + message);
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
        System.out.println("Client ID " + session.getId() + " sending message: " + yourMessage);
        session.getAsyncRemote().sendText(yourMessage);
    }

    public Session getSession() {
        return session;
    }

}
