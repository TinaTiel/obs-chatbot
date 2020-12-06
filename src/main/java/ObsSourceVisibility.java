import java.time.Duration;
import java.util.List;

public class ObsSourceVisibility implements Action {

    private final String sourceName;
    private final Visibility visibility;
    private final Duration duration;

    public ObsSourceVisibility(String sourceName, Visibility visibility, Duration duration) {
        this.sourceName = sourceName;
        this.visibility = visibility;
        this.duration = duration;
    }

    public String getSourceName() {
        return sourceName;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public void execute(List<String> arguments) {

    }

}
