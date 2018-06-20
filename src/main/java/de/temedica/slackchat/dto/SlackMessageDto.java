package de.temedica.slackchat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlackMessageDto {
    private String user;
    private String channel;
    private String text;
}
