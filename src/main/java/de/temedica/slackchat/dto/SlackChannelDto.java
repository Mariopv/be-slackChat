package de.temedica.slackchat.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SlackChannelDto {
    private String text;
    private List<SlackAtachmentsDto> attachments;
}
