import com.fasterxml.jackson.databind.ObjectMapper;
import de.temedica.slackchat.configuration.WebSocketConfiguration;
import de.temedica.slackchat.dto.SlackDto;
import de.temedica.slackchat.persistence.models.SlackMessage;
import de.temedica.slackchat.service.SlackMessageService;
import de.temedica.slackchat.service.WebSocketService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {"de.temedica.slackchat"})
@EnableAutoConfiguration
@ContextConfiguration(classes = {WebSocketConfiguration.class, WebSocketService.class})
public class WebSocketServiceTests {

    @Value("${local.server.port}")
    private int port;
    private String URL;

    private static final String SUBSCRIBE_FORWARD_MESSAGE_ENDPOINT = "/slackchat";

    private CompletableFuture<SlackDto> completableFuture;

    @Autowired
    private WebSocketService webSocketService;

    @Before
    public void setup() {
        completableFuture = new CompletableFuture<>();
        URL = "ws://localhost:" + port + "/socket";
    }

    @Test
    public void forwardMessageFromSlackSuccessfully() throws InterruptedException, ExecutionException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);

        stompSession.subscribe(SUBSCRIBE_FORWARD_MESSAGE_ENDPOINT, new forwardMessageHandler());

        SlackDto messageFromSlackMocked = new SlackDto();
        messageFromSlackMocked.setText("text");
        messageFromSlackMocked.setChannel("channel");
        messageFromSlackMocked.setUser("test_user");
        messageFromSlackMocked.setTs(LocalDateTime.now().toString());
        webSocketService.forwardMessage(messageFromSlackMocked);

        SlackDto messageFromSocket = completableFuture.get(5,TimeUnit.SECONDS);

        Assert.assertNotNull(messageFromSocket);
        Assert.assertEquals(messageFromSlackMocked.getUser(),messageFromSocket.getUser());
        Assert.assertEquals(messageFromSlackMocked.getText(),messageFromSocket.getText());
        Assert.assertEquals(messageFromSlackMocked.getChannel(),messageFromSocket.getChannel());
        Assert.assertEquals(messageFromSlackMocked.getTs(),messageFromSocket.getTs());
    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    private class forwardMessageHandler implements StompFrameHandler{
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return SlackDto.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            completableFuture.complete((SlackDto)o);
        }
    }

}


