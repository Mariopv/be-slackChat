import de.temedica.slackchat.dto.SlackMessageDto;
import de.temedica.slackchat.exception.SendingToSlackException;
import de.temedica.slackchat.persistence.SlackMessageRepository;
import de.temedica.slackchat.persistence.models.SlackMessage;
import de.temedica.slackchat.service.SlackMessageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@ComponentScan(basePackages = {"de.temedica.slackchat"})
@ContextConfiguration(classes = {SlackMessageRepository.class})
public class SlackMessageServiceTests {

    private SlackMessageService slackMessageService;
    private SlackMessageRepository slackMessageRepositoryMock;

    @Before
    public void setUp(){
        slackMessageRepositoryMock = Mockito.mock(SlackMessageRepository.class);
        slackMessageService = new SlackMessageService(slackMessageRepositoryMock);
    }

    @Test
    public void getMessagesByUserSuccessfully(){
        List<SlackMessage> slackMessageListMocked = new LinkedList<>();
        slackMessageListMocked.add(new SlackMessage("text","channel","user",LocalDateTime.now().toString()));
        when(slackMessageRepositoryMock.findAllByUser("user")).thenReturn(slackMessageListMocked);

        List<SlackMessage> slackMessageList = slackMessageService.getMessagesByUser("user");
        assertEquals(slackMessageList,slackMessageListMocked);
    }

    @Test
    public void sendMessageSuccessfully() throws SendingToSlackException {
        SlackMessage slackMessageMocked = new SlackMessage("text","channel","user",LocalDateTime.now().toString());
        SlackMessageDto slackMessageDtoMocked = new SlackMessageDto();
        slackMessageDtoMocked.setText(slackMessageMocked.getText());
        slackMessageDtoMocked.setChannel(slackMessageMocked.getChannel());
        slackMessageDtoMocked.setUser(slackMessageMocked.getUser());
        slackMessageDtoMocked.setTs(slackMessageMocked.getTs());
        when(slackMessageRepositoryMock.save(Matchers.any(SlackMessage.class))).thenReturn(slackMessageMocked);

        SlackMessage slackMessage = slackMessageService.sendToSlack(slackMessageDtoMocked);
        assertEquals(slackMessage.getText(),slackMessageMocked.getText());
        assertEquals(slackMessage.getUser(),slackMessageMocked.getUser());
        assertEquals(slackMessage.getChannel(),slackMessageMocked.getChannel());
        assertEquals(slackMessage.getTs(),slackMessageMocked.getTs());
    }


}
