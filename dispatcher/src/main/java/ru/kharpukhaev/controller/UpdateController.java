package ru.kharpukhaev.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kharpukhaev.service.UpdateProducer;
import ru.kharpukhaev.utils.MessageUtils;

import static ru.kharpukhaev.model.RabbitQueue.*;

@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }
        if (update.hasMessage() || update.hasCallbackQuery()) {
            distributeMessagesByType(update);
        } else {
            log.error("Unsupported message type is received: " + update);
        }
    }

    public void distributeMessagesByType(Update update) {
        if (update.hasCallbackQuery()) {
            processCallBackMessage(update);
        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                processTextMessage(update);
            } else {
                setUnsupportedMessageTypeView(update);
            }
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Unsupported message type!");
        setViewMessage(sendMessage);
    }

    public void setViewMessage(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    public void setViewMedia(SendMediaGroup sendPhoto) {
        telegramBot.sendAnswerMessage(sendPhoto);
    }

    public void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }

    private void processCallBackMessage(Update update) {
        updateProducer.produce(CALLBACK_MESSAGE_UPDATE, update);
    }
}
