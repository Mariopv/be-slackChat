package de.temedica.slackchat.service;

import de.temedica.slackchat.dto.SlackAtachmentsDto;
import de.temedica.slackchat.dto.SlackChannelDto;
import de.temedica.slackchat.dto.SlackDto;
import de.temedica.slackchat.dto.SlackMessageDto;
import de.temedica.slackchat.persistence.SlackMessageRepository;
import de.temedica.slackchat.persistence.models.SlackMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private SlackMessageRepository slackMessageRepository;

    private static final String SLACK_WEB_HOOK_URL = "https://hooks.slack.com/services/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";

    public boolean sendToSlack(SlackMessageDto slackMessageDto){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        SlackChannelDto slackChannelDto = new SlackChannelDto();
        slackChannelDto.setText(slackMessageDto.getText());
        SlackAtachmentsDto slackAtachmentsDto= new SlackAtachmentsDto();
        slackAtachmentsDto.setAuthor_name(slackMessageDto.getUser());
        List<SlackAtachmentsDto> atachmentsDtos = new LinkedList<>();
        atachmentsDtos.add(slackAtachmentsDto);
        slackChannelDto.setAttachments(atachmentsDtos);

        HttpEntity<SlackChannelDto> entity = new HttpEntity<>(slackChannelDto, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(SLACK_WEB_HOOK_URL, entity,String.class);

        if(response.getStatusCode()== HttpStatus.OK){
            //SlackMessage slackMessage = new SlackMessage(slackDto.getText(),slackDto.getChannel(),slackDto.getUser(),slackDto.getTs());
            //slackMessageRepository.save(slackMessage);
            return true;
        }
        return false;
    }

    public List<SlackDto> getMessagesByUser(String user){
        List<SlackDto> slackDtoList = new LinkedList<>();
        List<SlackMessage> slackMessages = slackMessageRepository.findAllByUser(user);
        slackMessages.forEach(slackMessage -> slackDtoList.add(SlackDto.createFromMessage(slackMessage)));
        return slackDtoList;
    }

    public void receiveMessageFromSlack(SlackDto slackDto){
        //TO-DO:Send to socket
    }
}
