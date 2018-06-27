package de.temedica.slackchat.api;

import de.temedica.slackchat.dto.SlackDto;
import de.temedica.slackchat.dto.SlackMessageDto;
import de.temedica.slackchat.exception.ForwardingMessageFromSlackException;
import de.temedica.slackchat.exception.SendingToSlackException;
import de.temedica.slackchat.persistence.models.SlackMessage;
import de.temedica.slackchat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/slackmessage")
public class MessageController {


    @Autowired
    private MessageService messageService;

    @PostMapping(value = {"/send"},consumes = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<SlackDto> sendMessageToSlack(@RequestBody SlackMessageDto slackMessageDto)
            throws SendingToSlackException
    {
        SlackDto slackDto = SlackDto.createFromMessage(messageService.sendToSlack(slackMessageDto));
        return ResponseEntity.ok(slackDto);
    }

    //Endpoint configured in Slack API
    @PostMapping(value = {"/receive"},consumes = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<SlackDto> receiveMessageEventFromSlack(@RequestBody SlackDto slackDto)
            throws ForwardingMessageFromSlackException
    {
        //To verify url endpoint by Slack API
        if(slackDto.getChallenge() != null && !slackDto.getChallenge().isEmpty()){
            return ResponseEntity.ok(slackDto);
        }
        messageService.forwardMessageFromSlack(slackDto);
        return ResponseEntity.ok(slackDto);
    }

    @GetMapping(value = {"/{username}"})
    public ResponseEntity<List<SlackDto>> getMessagesByUser(@PathVariable("username") @NotNull String username){
        List<SlackDto> slackDtoList = new LinkedList<>();
        List<SlackMessage> slackMessages = messageService.getMessagesByUser(username);
        slackMessages.forEach(slackMessage -> slackDtoList.add(SlackDto.createFromMessage(slackMessage)));
        return ResponseEntity.ok(slackDtoList);
    }


}
