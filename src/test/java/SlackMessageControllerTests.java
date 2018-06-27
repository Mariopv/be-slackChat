import com.fasterxml.jackson.databind.ObjectMapper;
import de.temedica.slackchat.dto.SlackDto;
import de.temedica.slackchat.dto.SlackMessageDto;
import de.temedica.slackchat.persistence.models.SlackMessage;
import de.temedica.slackchat.service.MessageService;
import de.temedica.slackchat.service.SlackMessageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {"de.temedica.slackchat"})
@ContextConfiguration(classes = {SlackMessageService.class,ObjectMapper.class})
@AutoConfigureMockMvc
public class SlackMessageControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockBean
    MessageService messageServiceMock;

    @Autowired
    ObjectMapper objectMapper;

    @Before
    public void setup() {
        mockMvc =  MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void sendMessageToSlackSuccessfully() throws  Exception {
        SlackMessage slackMessageMocked = new SlackMessage("text","channel","user","12345");
        SlackMessageDto slackMessageDto = new SlackMessageDto();
        slackMessageDto.setText(slackMessageMocked.getText());
        slackMessageDto.setChannel(slackMessageMocked.getChannel());
        slackMessageDto.setUser(slackMessageMocked.getUser());
        slackMessageDto.setTs(slackMessageMocked.getTs());

        when(messageServiceMock.sendToSlack(Matchers.any(SlackMessageDto.class))).thenReturn(slackMessageMocked);

        mockMvc.perform(post("/api/slackmessage/send")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(slackMessageDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.text", is(slackMessageMocked.getText())))
            .andExpect(jsonPath("$.channel", is(slackMessageMocked.getChannel())))
            .andExpect(jsonPath("$.user", is(slackMessageMocked.getUser())));
    }

    @Test
    public void receiveUrlVerificationMessageFromSlackSuccessfully() throws Exception{
        SlackDto slackDto = new SlackDto();
        slackDto.setChallenge("test_challenge");

       mockMvc.perform(post("/api/slackmessage/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(slackDto)))
                .andExpect(jsonPath("$.challenge", is(slackDto.getChallenge())));
    }

    @Test
    public void receiveMessageFromSlackSuccessfully() throws Exception{
        SlackDto slackDto = new SlackDto();
        slackDto.setText("text");
        slackDto.setUser("user");

        mockMvc.perform(post("/api/slackmessage/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(slackDto)))
                .andExpect(jsonPath("$.text", is(slackDto.getText())))
                .andExpect(jsonPath("$.user", is(slackDto.getUser())));
    }

    @Test
    public void getMessagesByUserSuccessFully() throws Exception{
        String username = "usertest";
        List<SlackMessage> slackMessageList = new LinkedList<>();
        SlackMessage slackMessageMocked = new SlackMessage("text","channel",username,LocalDateTime.now().toString());
        slackMessageList.add(slackMessageMocked);

        given(messageServiceMock.getMessagesByUser(username)).willReturn(slackMessageList);

        mockMvc.perform(get("/api/slackmessage/{username}",username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text", is(slackMessageMocked.getText())))
                .andExpect(jsonPath("$[0].channel", is(slackMessageMocked.getChannel())))
                .andExpect(jsonPath("$[0].user", is(slackMessageMocked.getUser())));
    }

}
