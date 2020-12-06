import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class commandTest {

    @Test
    void commandExecutedWithArguments() {

        // Given command with actions
        Action action1 = mock(Action.class);
        Action action2 = mock(Action.class);
        Action action3 = mock(Action.class);
        Command command = new Command();
        command.addAction(action1);
        command.addAction(action2);
        command.addAction(action3);

        // When executed with arguments
        List<String> args = Arrays.asList("foo", "bar", "baz");
        command.execute(args);

        // Then the actions are also executed with those arguments
        ArgumentCaptor<List<String>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        for(Action action: command.getActions()) {
            verify(action).execute(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).asList().containsExactlyElementsOf(args);
        }

    }
}
