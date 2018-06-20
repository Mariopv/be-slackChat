package de.temedica.slackchat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlackMessageDto {
    private String user;
    private String channel;
    private String text;
    private String ts;
    private String channel_type;
    private String event_ts;
}
