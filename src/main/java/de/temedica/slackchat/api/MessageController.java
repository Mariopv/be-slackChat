package de.temedica.slackchat.api;

import de.temedica.slackchat.dto.SlackDto;
import de.temedica.slackchat.dto.SlackMessageDto;
import de.temedica.slackchat.exception.ForwardingMessageFromSlackException;
import de.temedica.slackchat.exception.SendingToSlackException;
import de.temedica.slackchat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/api/slackmessage")
public class MessageController {


    @Autowired
    private MessageService messageService;

    @PostMapping(value = {"/send"},consumes = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<String> sendMessageToSlack(@RequestBody SlackMessageDto slackMessageDto)
            throws SendingToSlackException
    {
        if(messageService.sendToSlack(slackMessageDto)){
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.badRequest().body("error sending to Slack");
        }
    }

    //Endpoint configured in Slack API
    @PostMapping(value = {"/receive"},consumes = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<String> receiveMessageEventFromSlack(@RequestBody SlackDto slackDto)
            throws ForwardingMessageFromSlackException
    {
        //To verify url endpoint by Slack API
        if(slackDto.getChallenge() != null && !slackDto.getChallenge().isEmpty()){
            return ResponseEntity.ok(slackDto.getChallenge());
        }
        messageService.receiveFromSlackAndForwardMessage(slackDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = {"/{username}"})
    public List<SlackDto> getMessagesByUser(@PathVariable("username") @NotNull String username){
        return messageService.getMessagesByUser(username);
    }


}
