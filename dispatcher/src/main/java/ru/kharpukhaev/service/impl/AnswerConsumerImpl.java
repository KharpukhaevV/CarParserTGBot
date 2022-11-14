package ru.kharpukhaev.service.impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import ru.kharpukhaev.controller.UpdateController;
import ru.kharpukhaev.service.AnswerConsumer;

import static ru.kharpukhaev.model.RabbitQueue.ANSWER_MEDIA;
import static ru.kharpukhaev.model.RabbitQueue.ANSWER_MESSAGE;

@Service
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consumeMessage(SendMessage sendMessage) {
        updateController.setViewMessage(sendMessage);
    }

    @Override
    @RabbitListener(queues = ANSWER_MEDIA)
    public void consumeMedia(SendMediaGroup sendPhoto) {
        updateController.setViewMedia(sendPhoto);
    }
}
