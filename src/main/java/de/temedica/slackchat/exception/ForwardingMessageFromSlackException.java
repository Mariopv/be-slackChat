package de.temedica.slackchat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST ,
        reason = "Error forwarding message from Slack")
public class ForwardingMessageFromSlackException extends  Exception {
}
