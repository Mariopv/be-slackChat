package de.temedica.slackchat.dto;

import de.temedica.slackchat.persistence.models.SlackMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SlackDto {

    //url verification
    private String token;
    private String challenge;
    private String type;

    //message data
    private String text;
    private String channel;
    private String user;
    private String ts;

    private SlackMessageDto event;

    private List<SlackAttachmentsDto> attachments;

    public static SlackDto createFromMessage (SlackMessage slackMessage){
        SlackDto slackDto = new SlackDto();
        slackDto.setText(slackMessage.getText());
        slackDto.setChannel(slackMessage.getChannel());
        slackDto.setUser(slackMessage.getUser());
        slackDto.setTs(slackMessage.getTs());

        return slackDto;
    }
}
