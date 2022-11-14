package ru.kharpukhaev.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.kharpukhaev.handlers.AdvertisementHandler;
import ru.kharpukhaev.model.Advertisement;
import ru.kharpukhaev.model.Filter;
import ru.kharpukhaev.parser.AdParser;
import ru.kharpukhaev.repository.AdvertisementRepository;
import ru.kharpukhaev.repository.FilterRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class UpdateAdTask {
    private final AdParser adParser;
    private final FilterRepository filterRepository;
    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementHandler advertisementHandler;

    public UpdateAdTask(AdParser adParser, FilterRepository filterRepository, AdvertisementRepository advertisementRepository, AdvertisementHandler advertisementHandler) {
        this.adParser = adParser;
        this.filterRepository = filterRepository;
        this.advertisementRepository = advertisementRepository;
        this.advertisementHandler = advertisementHandler;
    }

    @Scheduled(cron = "@hourly")
    public void parseNewAd() {
        System.out.println("started");
        List<Filter> filters = filterRepository.findAllByUpdatesIsTrue();
        for (Filter filter : filters) {
            List<Advertisement> newAdvertisements = adParser.parse(filter);
            List<Advertisement> oldAdvertisements = advertisementRepository.findAllByFilterId(filter.getId());
            List<Long> avitoId = new ArrayList<>();
            for (Advertisement ad : oldAdvertisements){
                avitoId.add(ad.getAvitoId());
            }
            List<Advertisement> newAds = new ArrayList<>();
            for (Advertisement newAd : newAdvertisements) {
                if (!avitoId.contains(newAd.getAvitoId())) {
                    advertisementRepository.save(newAd);
                    newAds.add(newAd);
                }
            }
            advertisementHandler.showAd(filter.getTelegramId(), newAds);
        }
        System.out.println("finish");
    }
}
