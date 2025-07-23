package com.example.trivia.websocket;

import com.example.trivia.dto.MultiplayerQuestionBroadcast;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class QuestionBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;

    public QuestionBroadcaster(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastQuestion(Long sessionId, MultiplayerQuestionBroadcast question) {
        String destination = "/topic/session/" + sessionId + "/question";
        messagingTemplate.convertAndSend(destination, question);
    }
}
