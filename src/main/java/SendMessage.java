import java.util.List;

public class SendMessage implements Action {

    private final String message;

    public SendMessage(String message) {
        this.message = message;
    }

    @Override
    public void execute(List<String> arguments) {

    }

    public String getMessage() {
        return message;
    }


}
