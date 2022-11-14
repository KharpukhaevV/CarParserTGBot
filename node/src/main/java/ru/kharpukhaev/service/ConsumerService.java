package ru.kharpukhaev.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService {
    void consumeTextMessageUpdates(Update update);
    void consumeCallBackQueryUpdates(Update update);
}
