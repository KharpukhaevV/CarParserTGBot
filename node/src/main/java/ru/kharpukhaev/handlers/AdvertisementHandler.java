package ru.kharpukhaev.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import ru.kharpukhaev.model.Advertisement;
import ru.kharpukhaev.model.Filter;
import ru.kharpukhaev.parser.AdParser;
import ru.kharpukhaev.repository.AdvertisementRepository;
import ru.kharpukhaev.repository.FilterRepository;
import ru.kharpukhaev.service.ProducerService;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdvertisementHandler {

    private final ProducerService producerService;
    private final FilterRepository filterRepository;
    private final AdParser adParser;
    private final AdvertisementRepository advertisementRepository;

    public AdvertisementHandler(ProducerService producerService, FilterRepository filterRepository, AdParser adParser, AdvertisementRepository advertisementRepository) {
        this.producerService = producerService;
        this.filterRepository = filterRepository;
        this.adParser = adParser;
        this.advertisementRepository = advertisementRepository;
    }

    public void handle(Update update) {
        List<Filter> filters = filterRepository.findAllByTelegramId(update.getCallbackQuery().getFrom().getId());
        for (Filter filter : filters) {
            List<Advertisement> advertisements;
            if (update.getCallbackQuery().getData().contains("last")) {
                advertisements = advertisementRepository.findTop5ByFilterId(filter.getId());
            } else {
                advertisements = advertisementRepository.findAllByFilterId(filter.getId());
            }
            var sendMessage = new SendMessage();
            sendMessage.setChatId(update.getCallbackQuery().getFrom().getId());
            sendMessage.disableWebPagePreview();

            if (advertisements.size() == 0) {
                sendMessage.setText("Идет поиск. Пожалуйста подождите");
                producerService.producerAnswer(sendMessage);

                List<Advertisement> newAds = adParser.parse(filter);
                if (!newAds.isEmpty()) {
                    advertisementRepository.saveAll(newAds);
                    showAd(update.getCallbackQuery().getFrom().getId(), newAds);
                } else {
                    sendMessage.setText("Ничего не найдено, попробуйте изменить фильтр.");
                    producerService.producerAnswer(sendMessage);
                }
            } else {
                showAd(update.getCallbackQuery().getFrom().getId(), advertisements);
            }
        }
    }

    public void showAd(Long id, List<Advertisement> advertisements) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setChatId(id);
        for (Advertisement ad : advertisements) {
            List<InputMedia> inputFiles = new ArrayList<>();
            int counter = 1;
            for (String img : ad.getImage().split("\n")) {
                InputMediaPhoto inputMediaPhoto = new InputMediaPhoto(img);
                if (counter == 1) {
                    String caption = ad.getUrl() +
                            "\n" + ad.getTitle() +
                            "\nЦена: " + ad.getPrice() +
                            "\nЦена: " + ad.getPrice() +
                            "\nПробег: " + ad.getMileage() +
                            "\nОписание: " + ad.getDescription();
                    if (caption.length() > 1000) {
                        caption = caption.substring(0, 996) + "...";
                    }
                    inputMediaPhoto.setCaption(caption);
                }
                inputFiles.add(inputMediaPhoto);
                counter++;
            }
            sendMediaGroup.setMedias(inputFiles);
            producerService.producerAnswer(sendMediaGroup);
        }
    }
}
