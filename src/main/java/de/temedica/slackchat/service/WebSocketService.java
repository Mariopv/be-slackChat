package de.temedica.slackchat.service;

import de.temedica.slackchat.dto.SlackDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    private final SimpMessagingTemplate template;

    @Autowired
    WebSocketService(SimpMessagingTemplate template){
        this.template = template;
    }

    public void forwardMessage(SlackDto slackDto){
        this.template.convertAndSend("/slackchat",  slackDto);
    }
}
