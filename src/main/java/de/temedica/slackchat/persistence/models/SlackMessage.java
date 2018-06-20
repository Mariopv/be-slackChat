package de.temedica.slackchat.persistence.models;

import de.temedica.slackchat.dto.SlackDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlackMessage {
    private String text;
    private String channel;
    private String user;
    private String ts;

    public SlackMessage(String text,String channel,String user,String ts){
        this.text = text;
        this.channel = channel;
        this.user = user;
        this.ts = ts;
    }
}
