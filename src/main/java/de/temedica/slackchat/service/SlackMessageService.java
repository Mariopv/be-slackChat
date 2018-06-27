package de.temedica.slackchat.service;

import de.temedica.slackchat.dto.SlackAttachmentsDto;
import de.temedica.slackchat.dto.SlackDto;
import de.temedica.slackchat.dto.SlackMessageDto;
import de.temedica.slackchat.exception.ForwardingMessageFromSlackException;
import de.temedica.slackchat.exception.SendingToSlackException;
import de.temedica.slackchat.persistence.SlackMessageRepository;
import de.temedica.slackchat.persistence.models.SlackMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

@Service
public class SlackMessageService implements MessageService{

    @Autowired
    private SlackMessageRepository slackMessageRepository;

    @Autowired
    private WebSocketService webSocketService;

    private static final String SLACK_WEB_HOOK_URL = "https://hooks.slack.com/services/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";

    public SlackMessageService(SlackMessageRepository slackMessageRepository){
        this.slackMessageRepository = slackMessageRepository;
    }

    @Override
    public SlackMessage sendToSlack(SlackMessageDto slackMessageDto) throws SendingToSlackException{
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        SlackDto slackDto = new SlackDto();
        slackDto.setText(slackMessageDto.getText());
        SlackAttachmentsDto slackAttachmentsDto = new SlackAttachmentsDto();
        slackAttachmentsDto.setAuthor_name(slackMessageDto.getUser());
        List<SlackAttachmentsDto> attachmentsDtos = new LinkedList<>();
        attachmentsDtos.add(slackAttachmentsDto);
        slackDto.setAttachments(attachmentsDtos);

        HttpEntity<SlackDto> entity = new HttpEntity<>(slackDto, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(SLACK_WEB_HOOK_URL, entity,String.class);

        SlackMessage slackMessage = new SlackMessage(slackMessageDto.getText(),slackMessageDto.getChannel(),
                slackMessageDto.getUser(),slackMessageDto.getTs());
        if(response.getStatusCode()== HttpStatus.OK){
            return slackMessageRepository.save(slackMessage);
        }
        throw new SendingToSlackException();

    }

    @Override
    public List<SlackMessage> getMessagesByUser(String user){
        return slackMessageRepository.findAllByUser(user);
    }

    @Override
    public void forwardMessageFromSlack(SlackDto slackDto) throws ForwardingMessageFromSlackException {
        webSocketService.forwardMessage(slackDto);
    }
}
