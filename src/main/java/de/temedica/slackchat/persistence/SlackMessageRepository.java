package de.temedica.slackchat.persistence;

import de.temedica.slackchat.persistence.models.SlackMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlackMessageRepository extends MongoRepository<SlackMessage,String> {

    List<SlackMessage> findAllByUser(String user);

}
