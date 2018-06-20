package de.temedica.slackchat.service;

import de.temedica.slackchat.dto.SlackDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class WebSocketService {
    private final SimpMessagingTemplate template;

    @Autowired
    WebSocketService(SimpMessagingTemplate template){
        this.template = template;
    }

    public void onReceivedMessage(SlackDto slackDto){
        this.template.convertAndSend("/slackchat",  new SimpleDateFormat("HH:mm:ss").format(new Date())+"- "+ slackDto);
    }
}
