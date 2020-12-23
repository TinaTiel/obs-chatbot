import com.tinatiel.obschatbot.core.actionservice.obs.ObsClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.tinatiel.obschatbot"})
public class App {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(App.class);
        ObsClient client = context.getBean(ObsClient.class);
        client.setSourceVisibility("Scene", "someTextSource", false);
    }
}
