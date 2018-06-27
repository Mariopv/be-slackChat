package de.temedica.slackchat.service;

import de.temedica.slackchat.dto.SlackDto;
import de.temedica.slackchat.dto.SlackMessageDto;
import de.temedica.slackchat.exception.ForwardingMessageFromSlackException;
import de.temedica.slackchat.exception.SendingToSlackException;
import de.temedica.slackchat.persistence.models.SlackMessage;

import java.util.List;

public interface MessageService {
    SlackMessage sendToSlack(SlackMessageDto slackMessageDto) throws SendingToSlackException;

    List<SlackMessage> getMessagesByUser(String username);

    void forwardMessageFromSlack(SlackDto slackDto) throws ForwardingMessageFromSlackException;
}
