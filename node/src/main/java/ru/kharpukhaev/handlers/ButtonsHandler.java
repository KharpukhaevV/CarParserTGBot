package ru.kharpukhaev.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.kharpukhaev.model.Advertisement;
import ru.kharpukhaev.model.Filter;
import ru.kharpukhaev.model.State;
import ru.kharpukhaev.model.User;
import ru.kharpukhaev.repository.AdvertisementRepository;
import ru.kharpukhaev.repository.FilterRepository;
import ru.kharpukhaev.repository.UserRepository;
import ru.kharpukhaev.service.ProducerService;

import java.util.ArrayList;
import java.util.List;

@Component
public class ButtonsHandler {

    private final ProducerService producerService;
    private final AdvertisementRepository advertisementRepository;
    private final FilterRepository filterRepository;
    private final UserRepository userRepository;


    public ButtonsHandler(ProducerService producerService, AdvertisementRepository advertisementRepository, FilterRepository filterRepository, UserRepository userRepository) {
        this.producerService = producerService;
        this.advertisementRepository = advertisementRepository;
        this.filterRepository = filterRepository;
        this.userRepository = userRepository;
    }

    public void mainMenu(Update update) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<KeyboardRow>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();

        replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        keyboardFirstRow.add("Мои фильтры");
        keyboardSecondRow.add("Создать фильтр");
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText("Get start");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        producerService.producerAnswer(sendMessage);
    }

    public void cancel(Update update) {
        CallbackQuery callBackQuery = update.getCallbackQuery();
        User user = userRepository.findByTelegramId(callBackQuery.getFrom().getId());
        SendMessage sendMessage = new SendMessage();
        if (!callBackQuery.getData().contains("?")) {
            Long filterId = Long.parseLong(callBackQuery.getData().split("/")[1]);
            List<Advertisement> advertisements = advertisementRepository.findAllByFilterId(filterId);
            advertisementRepository.deleteAll(advertisements);
            filterRepository.delete(filterRepository.findFirstById(filterId));
        } else {
            Filter filter = filterRepository.findByTelegramIdAndColorIsNull(update.getCallbackQuery().getFrom().getId());
            filterRepository.delete(filter);
        }
        user.setState(State.NONE);
        userRepository.save(user);
        sendMessage.setChatId(update.getCallbackQuery().getFrom().getId());
        sendMessage.setText("Фильтр удален.");
        producerService.producerAnswer(sendMessage);
    }

    public void subscribe(Update update) {
        SendMessage sendMessage = new SendMessage();
        Long id = Long.parseLong(update.getCallbackQuery().getData().split("/")[1]);
        Filter filter = filterRepository.findFirstById(id);
        filter.setUpdates(true);
        filterRepository.save(filter);

        sendMessage.setChatId(update.getCallbackQuery().getFrom().getId());
        sendMessage.setText("Вы подписались на обновления.");
        producerService.producerAnswer(sendMessage);
    }

    public void unsubscribe(Update update) {
        SendMessage sendMessage = new SendMessage();
        Filter filter = filterRepository.findFirstById(Long.parseLong(update.getCallbackQuery().getData().split("/")[1]));
        filter.setUpdates(false);
        filterRepository.save(filter);

        sendMessage.setChatId(update.getCallbackQuery().getFrom().getId());
        sendMessage.setText("Вы отписались от обновлений.");
        producerService.producerAnswer(sendMessage);
    }
}
