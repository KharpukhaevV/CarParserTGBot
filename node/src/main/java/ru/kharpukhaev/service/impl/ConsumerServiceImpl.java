package ru.kharpukhaev.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kharpukhaev.handlers.AdvertisementHandler;
import ru.kharpukhaev.handlers.FilterHandler;
import ru.kharpukhaev.handlers.ButtonsHandler;
import ru.kharpukhaev.model.State;
import ru.kharpukhaev.model.User;
import ru.kharpukhaev.repository.UserRepository;
import ru.kharpukhaev.service.ConsumerService;
import ru.kharpukhaev.service.ProducerService;

import static ru.kharpukhaev.model.RabbitQueue.*;

@Service
@Log4j
public class ConsumerServiceImpl implements ConsumerService {
    private final ProducerService producerService;
    private final ButtonsHandler buttonsHandler;
    private final FilterHandler filterHandler;
    private final UserRepository userRepository;
    private final AdvertisementHandler advertisementHandler;

    public ConsumerServiceImpl(ProducerService producerService, ButtonsHandler buttonsHandler, FilterHandler filterHandler, UserRepository userRepository, AdvertisementHandler advertisementHandler) {
        this.producerService = producerService;
        this.buttonsHandler = buttonsHandler;
        this.filterHandler = filterHandler;
        this.userRepository = userRepository;
        this.advertisementHandler = advertisementHandler;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdates(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getFrom().getId());
        User user = userRepository.findByTelegramId(update.getMessage().getFrom().getId());
        if (user == null) {
            user = new User(update.getMessage().getFrom().getId(), update.getMessage().getFrom().getUserName(), State.NONE);
            userRepository.save(user);
        }
        switch (update.getMessage().getText()) {
            case ("/start"):
                buttonsHandler.mainMenu(update);
                break;
            case ("Мои фильтры"):
                filterHandler.myFilters(update);
                break;
            case ("Создать фильтр"):
                filterHandler.handle(update);
                break;
            default:
                if (!user.getState().equals(State.NONE)) {
                    filterHandler.handle(update);
                } else {
                    sendMessage.setText("Неизвестная команда!");
                    producerService.producerAnswer(sendMessage);
                }
                break;
        }
    }

    @Override
    @RabbitListener(queues = CALLBACK_MESSAGE_UPDATE)
    public void consumeCallBackQueryUpdates(Update update) {
        User user = userRepository.findByTelegramId(update.getCallbackQuery().getFrom().getId());
        switch (update.getCallbackQuery().getData().split("/")[0]) {
            case ("cancel"):
                buttonsHandler.cancel(update);
                break;
            case ("check"):
                advertisementHandler.handle(update);
                break;
            case ("subscribe"):
                buttonsHandler.subscribe(update);
                break;
            case ("unsubscribe"):
                buttonsHandler.unsubscribe(update);
                break;
            default:
                if (!user.getState().equals(State.NONE)) {
                    filterHandler.handle(update);
                }
                break;
        }

    }
}
