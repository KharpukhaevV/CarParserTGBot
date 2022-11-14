package ru.kharpukhaev.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

public interface AnswerConsumer {
    void consumeMessage(SendMessage sendMessage);
    void consumeMedia(SendMediaGroup sendPhoto);
}
