package ru.kharpukhaev.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.kharpukhaev.model.*;
import ru.kharpukhaev.repository.FilterRepository;
import ru.kharpukhaev.repository.MarkRepository;
import ru.kharpukhaev.repository.UserRepository;
import ru.kharpukhaev.service.ProducerService;

import java.util.ArrayList;
import java.util.List;

@Component
public class FilterHandler {

    private final ProducerService producerService;
    private final MarkRepository markRepository;
    private final UserRepository userRepository;
    private final FilterRepository filterRepository;

    public FilterHandler(ProducerService producerService, MarkRepository markRepository, UserRepository userRepository, FilterRepository filterRepository) {
        this.producerService = producerService;
        this.markRepository = markRepository;
        this.userRepository = userRepository;
        this.filterRepository = filterRepository;
    }

    public void handle(Update update) {
        User user;
        Filter filter;
        if (update.getMessage() != null) {
            user = userRepository.findByTelegramId(update.getMessage().getFrom().getId());
        } else {
            user = userRepository.findByTelegramId(update.getCallbackQuery().getFrom().getId());
        }

        if (user.getState().equals(State.NONE)) {
            filter = new Filter();
            filter.setTelegramId(user.getTelegramId());
            filterRepository.save(filter);
            setMark(update);
        } else if (user.getState().equals(State.SET_MARK)) {
            filter = filterRepository.findByTelegramIdAndColorIsNull(update.getCallbackQuery().getFrom().getId());
            filter.setMark(update.getCallbackQuery().getData().split("/")[1]);
            filterRepository.save(filter);
            setModel(update);
        } else if (user.getState().equals(State.SET_MODEL)) {
            filter = filterRepository.findByTelegramIdAndColorIsNull(update.getCallbackQuery().getFrom().getId());
            filter.setModel(update.getCallbackQuery().getData().split("/")[1]);
            filterRepository.save(filter);
            setStartPrice(update);
        } else if (user.getState().equals(State.SET_START_PRICE)) {
            filter = filterRepository.findByTelegramIdAndColorIsNull(update.getMessage().getFrom().getId());
            filter.setStartPrice(update.getMessage().getText());
            filterRepository.save(filter);
            setFinishPrice(update);
        } else if (user.getState().equals(State.SET_FINISH_PRICE)) {
            filter = filterRepository.findByTelegramIdAndColorIsNull(update.getMessage().getFrom().getId());
            filter.setFinishPrice(update.getMessage().getText());
            filterRepository.save(filter);
            setStartYear(update);
        } else if (user.getState().equals(State.SET_START_YEAR)) {
            filter = filterRepository.findByTelegramIdAndColorIsNull(update.getMessage().getFrom().getId());
            filter.setStartYear(update.getMessage().getText());
            filterRepository.save(filter);
            setFinishYear(update);
        } else if (user.getState().equals(State.SET_FINISH_YEAR)) {
            filter = filterRepository.findByTelegramIdAndColorIsNull(update.getMessage().getFrom().getId());
            filter.setFinishYear(update.getMessage().getText());
            filterRepository.save(filter);
            setStartMileage(update);
        } else if (user.getState().equals(State.SET_START_MILEAGE)) {
            filter = filterRepository.findByTelegramIdAndColorIsNull(update.getMessage().getFrom().getId());
            filter.setStartMileage(update.getMessage().getText());
            filterRepository.save(filter);
            setFinishMileage(update);
        } else if (user.getState().equals(State.SET_FINISH_MILEAGE)) {
            filter = filterRepository.findByTelegramIdAndColorIsNull(update.getMessage().getFrom().getId());
            filter.setFinishMileage(update.getMessage().getText());
            filterRepository.save(filter);
            setCrashed(update);
        } else if (user.getState().equals(State.SET_CRASHED)) {
            filter = filterRepository.findByTelegramIdAndColorIsNull(update.getCallbackQuery().getFrom().getId());
            if (update.getCallbackQuery().getData().contains("true")) {
                filter.setCrashed(true);
            }
            if (update.getCallbackQuery().getData().contains("false")) {
                filter.setCrashed(false);
            }
            filterRepository.save(filter);
            setColor(update);
        } else if (user.getState().equals(State.SET_COLOR)) {
            filter = filterRepository.findByTelegramIdAndColorIsNull(update.getCallbackQuery().getFrom().getId());
            filter.setColor(update.getCallbackQuery().getData().split("/")[1]);
            filter.setUpdates(false);
            user.setState(State.NONE);
            filterRepository.save(filter);
            userRepository.save(user);
            myFilters(update);
        }
    }

    private void setMark(Update update) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<Mark> marks = markRepository.findAll();
        for (int i = 0; i < marks.size(); i = i + 2) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText(marks.get(i).getName());
            inlineKeyboardButton1.setCallbackData("set_mark/" + marks.get(i).getName());
            rowInline.add(inlineKeyboardButton1);
            if (marks.size() > i + 1) {
                inlineKeyboardButton2.setText(marks.get(i + 1).getName());
                inlineKeyboardButton2.setCallbackData("set_mark/" + marks.get(i + 1).getName());
                rowInline.add(inlineKeyboardButton2);
            }
            rowsInline.add(rowInline);
        }
        rowsInline.add(cancelButton());

        inlineKeyboard.setKeyboard(rowsInline);

        var sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText("???????????????? ?????????? ????????????????????: ");
        sendMessage.setReplyMarkup(inlineKeyboard);

        producerService.producerAnswer(sendMessage);

        User user = userRepository.findByTelegramId(update.getMessage().getFrom().getId());
        user.setState(State.SET_MARK);
        userRepository.save(user);
    }

    private void setModel(Update update) {
        Mark mark = markRepository.findMarkByName(update.getCallbackQuery().getData().split("/")[1]);
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<Model> models = mark.getModels();

        for (int i = 0; i < models.size(); i = i + 3) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
            InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText(models.get(i).getName());
            inlineKeyboardButton1.setCallbackData("set_model/" + models.get(i).getName());
            rowInline.add(inlineKeyboardButton1);
            if (models.size() > i + 1) {
                inlineKeyboardButton2.setText(models.get(i + 1).getName());
                inlineKeyboardButton2.setCallbackData("set_model/" + models.get(i + 1).getName());
                rowInline.add(inlineKeyboardButton2);
            }
            if (models.size() > i + 2) {
                inlineKeyboardButton3.setText(models.get(i + 2).getName());
                inlineKeyboardButton3.setCallbackData("set_model/" + models.get(i + 2).getName());
                rowInline.add(inlineKeyboardButton3);
            }

            rowsInline.add(rowInline);
        }
        rowsInline.add(cancelButton());

        inlineKeyboard.setKeyboard(rowsInline);
        var sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getFrom().getId());
        sendMessage.setText("???????????????? ???????????? ????????????????????: ");
        sendMessage.setReplyMarkup(inlineKeyboard);

        producerService.producerAnswer(sendMessage);

        User user = userRepository.findByTelegramId(update.getCallbackQuery().getFrom().getId());
        user.setState(State.SET_MODEL);
        userRepository.save(user);
    }

    private void setStartPrice(Update update) {
        SendMessage sendMessage = inLineForm(update.getCallbackQuery().getFrom().getId(), "?????????????? ?????????????????? ????????: ");
        producerService.producerAnswer(sendMessage);

        User user = userRepository.findByTelegramId(update.getCallbackQuery().getFrom().getId());
        user.setState(State.SET_START_PRICE);
        userRepository.save(user);
    }

    private void setFinishPrice(Update update) {
        SendMessage sendMessage = inLineForm(update.getMessage().getFrom().getId(), "?????????????? ???????????????? ????????: ");
        producerService.producerAnswer(sendMessage);

        User user = userRepository.findByTelegramId(update.getMessage().getFrom().getId());
        user.setState(State.SET_FINISH_PRICE);
        userRepository.save(user);
    }

    private void setStartYear(Update update) {
        SendMessage sendMessage = inLineForm(update.getMessage().getFrom().getId(), "?????????????? ?????????????????? ??????: ");
        producerService.producerAnswer(sendMessage);

        User user = userRepository.findByTelegramId(update.getMessage().getFrom().getId());
        user.setState(State.SET_START_YEAR);
        userRepository.save(user);
    }

    private void setFinishYear(Update update) {
        SendMessage sendMessage = inLineForm(update.getMessage().getFrom().getId(), "?????????????? ???????????????? ??????: ");
        producerService.producerAnswer(sendMessage);

        User user = userRepository.findByTelegramId(update.getMessage().getFrom().getId());
        user.setState(State.SET_FINISH_YEAR);
        userRepository.save(user);
    }

    private void setStartMileage(Update update) {
        SendMessage sendMessage = inLineForm(update.getMessage().getFrom().getId(), "?????????????? ?????????????????? ????????????: ");
        producerService.producerAnswer(sendMessage);

        User user = userRepository.findByTelegramId(update.getMessage().getFrom().getId());
        user.setState(State.SET_START_MILEAGE);
        userRepository.save(user);
    }

    private void setFinishMileage(Update update) {
        SendMessage sendMessage = inLineForm(update.getMessage().getFrom().getId(), "?????????????? ???????????????? ????????????: ");
        producerService.producerAnswer(sendMessage);

        User user = userRepository.findByTelegramId(update.getMessage().getFrom().getId());
        user.setState(State.SET_FINISH_MILEAGE);
        userRepository.save(user);
    }

    private void setCrashed(Update update) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("??????????");
        inlineKeyboardButton2.setText("???? ??????????");
        inlineKeyboardButton1.setCallbackData("crashed/true");
        inlineKeyboardButton2.setCallbackData("crashed/false");

        rowInline1.add(inlineKeyboardButton1);
        rowInline2.add(inlineKeyboardButton2);

        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        rowsInline.add(cancelButton());

        inlineKeyboard.setKeyboard(rowsInline);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getFrom().getId());
        sendMessage.setText("???????????????? ??????????????????");
        sendMessage.setReplyMarkup(inlineKeyboard);

        producerService.producerAnswer(sendMessage);

        User user = userRepository.findByTelegramId(update.getMessage().getFrom().getId());
        user.setState(State.SET_CRASHED);
        userRepository.save(user);
    }

    private void setColor(Update update) {
        String[] colors = {"??????????", "????????????????????", "??????????", "????????????", "????????????????????", "??????????????", "??????????????", "??????????????", "????????????????", "??????????????????", "????????????", "??????????????", "??????????????", "??????????", "????????????????????", "??????????????????", "??????????????"};
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (int i = 0; i < colors.length; i = i + 3) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
            InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText(colors[i]);
            inlineKeyboardButton1.setCallbackData("color/" + colors[i]);
            rowInline.add(inlineKeyboardButton1);
            if (colors.length > i + 1) {
                inlineKeyboardButton2.setText(colors[i + 1]);
                inlineKeyboardButton2.setCallbackData("color/" + colors[i + 1]);
                rowInline.add(inlineKeyboardButton2);
            }
            if (colors.length > i + 2) {
                inlineKeyboardButton3.setText(colors[i + 2]);
                inlineKeyboardButton3.setCallbackData("color/" + colors[i + 2]);
                rowInline.add(inlineKeyboardButton3);
            }
            rowsInline.add(rowInline);
        }

        rowsInline.add(cancelButton());
        inlineKeyboard.setKeyboard(rowsInline);

        var sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getFrom().getId());
        sendMessage.setText("???????????????? ????????");
        sendMessage.setReplyMarkup(inlineKeyboard);

        User user = userRepository.findByTelegramId(update.getCallbackQuery().getFrom().getId());
        user.setState(State.SET_COLOR);
        userRepository.save(user);

        producerService.producerAnswer(sendMessage);
    }

    public void myFilters(Update update) {
        long id;
        if (update.hasCallbackQuery()) {
            id = update.getCallbackQuery().getFrom().getId();
        } else {
            id = update.getMessage().getFrom().getId();
        }
        List<Filter> filters = filterRepository.findAllByTelegramId(id);
        var sendMessage = new SendMessage();
        sendMessage.setChatId(id);
        if (filters.size() != 0) {
            for (Filter filter : filters) {
                sendMessage.setText("??????????: " + filter.getMark() +
                        "\n????????????: " + filter.getModel() +
                        "\n????????: " + filter.getStartPrice() + " - " + filter.getFinishPrice() +
                        "\n??????: " + filter.getStartYear() + " - " + filter.getFinishPrice() +
                        "\n????????????: " + filter.getStartMileage() + " - " + filter.getFinishMileage() +
                        "\n??????????: " + filter.isCrashed() +
                        "\n????????: " + filter.getColor() +
                        "\n???????????????? ???? ????????????????????: " + filter.getUpdates());
                InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
                List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
                InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
                InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
                if (filter.getUpdates()) {
                    inlineKeyboardButton3.setText("????????????????????");
                    inlineKeyboardButton3.setCallbackData("unsubscribe/" + filter.getId());
                } else {
                    inlineKeyboardButton3.setText("??????????????????????");
                    inlineKeyboardButton3.setCallbackData("subscribe/" + filter.getId());
                }
                inlineKeyboardButton2.setText("???????????????? ??????");
                inlineKeyboardButton2.setCallbackData("check/");
                inlineKeyboardButton.setText("???????????????? ??????????????????");
                inlineKeyboardButton.setCallbackData("check/last");
                inlineKeyboardButton4.setText("??????????????");
                inlineKeyboardButton4.setCallbackData("cancel/" + filter.getId());
                rowInline2.add(inlineKeyboardButton3);
                rowInline.add(inlineKeyboardButton);
                rowInline.add(inlineKeyboardButton2);
                rowInline3.add(inlineKeyboardButton4);
                rowsInline.add(rowInline2);
                rowsInline.add(rowInline);
                rowsInline.add(rowInline3);
                inlineKeyboard.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(inlineKeyboard);
            }
        } else {
            sendMessage.setText("?? ?????? ?????? ???????????????? ????????????.");
        }
        producerService.producerAnswer(sendMessage);
    }

    private List<InlineKeyboardButton> cancelButton() {
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("????????????");
        inlineKeyboardButton.setCallbackData("cancel/?");
        rowInline.add(inlineKeyboardButton);
        return rowInline;
    }

    private SendMessage inLineForm(long id, String message) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(cancelButton());
        inlineKeyboard.setKeyboard(rowsInline);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(id);
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(inlineKeyboard);
        return sendMessage;
    }
}
