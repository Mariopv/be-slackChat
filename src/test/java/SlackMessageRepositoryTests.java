import de.temedica.slackchat.persistence.SlackMessageRepository;
import de.temedica.slackchat.persistence.models.SlackMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataMongoTest
@EnableAutoConfiguration
@ComponentScan(basePackages = {"de.temedica.slackchat"})
@ContextConfiguration(classes = {SlackMessageRepository.class})
public class SlackMessageRepositoryTests {

    @Autowired
    private SlackMessageRepository slackMessageRepository;

    static final int NUM_MESSAGES = 10;

    @Before
    public void init() {
        slackMessageRepository.deleteAll();
        for (Integer i = 0; i < NUM_MESSAGES; i++) {
            SlackMessage slackMessage =
                    new SlackMessage("Hello" + i.toString(),"slack","user"+ i.toString(),LocalDateTime.now().toString());
            SlackMessage slackMessageSaved = slackMessageRepository.save(slackMessage);
            assertEquals(slackMessageSaved.getText(),slackMessage.getText());
        }
    }

    @Test
    public void addMessagesSuccessfully() {
        List<SlackMessage> list = slackMessageRepository.findAll();
        assertEquals(NUM_MESSAGES, list.size());
    }

}
