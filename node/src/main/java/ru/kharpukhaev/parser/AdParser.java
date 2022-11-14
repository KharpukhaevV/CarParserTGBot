package ru.kharpukhaev.parser;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Component;
import ru.kharpukhaev.model.Advertisement;
import ru.kharpukhaev.model.Filter;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdParser {

    public List<Advertisement> parse(Filter filter) {
        WebDriver webDriver = webDriverGetConnect();

        List<Advertisement> advertisements = new ArrayList<>();
        try {
            setFilter(filter, webDriver);

            List<WebElement> elements = webDriver.findElements(By.className("iva-item-root-_lk9K"));

            long adsSize = Long.parseLong(webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[2]/div/span")).getText());
            for (int i = 0; i < adsSize; i++) {
                long id = Long.parseLong(elements.get(i).getAttribute("data-item-id"));
                Actions action = new Actions(webDriver);
                action.moveToElement(elements.get(i).findElement(By.xpath("//*[@id=\"i" + id + "\"]/div/div[1]/a/div/div/ul/li/div")));
                action.perform();
                List<WebElement> imageEl = elements.get(i).findElement(By.xpath("//*[@id=\"i" + id + "\"]/div/div[1]/a/div/div/ul")).findElements(By.tagName("li"));

                StringBuilder images = new StringBuilder();
                for (WebElement el : imageEl) {
                    String url = el.getAttribute("data-marker").substring(19);
                    images.append(url).append("\n");
                }
                String description = elements.get(i).findElement(By.className("iva-item-description-FDgK4")).getText();
                if (description.length() > 999) {
                    description = description.substring(0, 999);
                }
                advertisements.add(new Advertisement(
                        id,
                        elements.get(i).findElement(By.xpath("//*[@id=\"i" + id + "\"]/div/div[2]/div[2]/a/h3")).getText(),
                        elements.get(i).findElement(By.xpath("//*[@id=\"i" + id + "\"]/div/div[2]/div[3]/span/span/span")).getText(),
                        elements.get(i).findElement(By.className("iva-item-text-Ge6dR")).getText().split(",")[0],
                        description,
                        images.toString(),
                        elements.get(i).findElement(By.xpath("//*[@id=\"i" + id + "\"]/div/div[2]/div[2]/a")).getAttribute("href"),
                        filter.getId()));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webDriver.quit();
        }
        return advertisements;
    }

    private WebDriver webDriverGetConnect() {
        System.setProperty("webdriver.chrome.driver", "selenium\\chromedriver.exe");
        WebDriver webDriver = new ChromeDriver();
        webDriver.get("https://www.avito.ru/samara/avtomobili?cd=1&radius=200");
        return webDriver;
    }

    private void setFilter(Filter filter, WebDriver webDriver) {
        try {
            //Выбор марки
            WebElement allMark = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[2]/div/div[2]/div/div/div/div/label/input"));
            allMark.click();
            Thread.sleep(2000);
            allMark.sendKeys(filter.getMark());
            Thread.sleep(2000);
            webDriver.findElement(By.className("default-item-item-qrNup")).click();
            Thread.sleep(2000);

            //Выбор модели
            WebElement allModel = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[3]/div/div[2]/div/div/div/div/label/input"));
            allModel.click();
            Thread.sleep(2000);
            allModel.sendKeys(filter.getModel());
            webDriver.findElement(By.className("default-item-item-qrNup")).click();
            Thread.sleep(2000);

            //Выбор цены
            WebElement startPrice = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[5]/div/div[2]/div/div/div/div/div/div/label[1]/input"));
            startPrice.sendKeys(filter.getStartPrice());
            Thread.sleep(2000);
            WebElement finishPrice = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[5]/div/div[2]/div/div/div/div/div/div/label[2]/input"));
            finishPrice.sendKeys(filter.getFinishPrice());
            Thread.sleep(2000);

            //Выбор года
            WebElement startYear = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[7]/div/div[2]/div/div/div/div/div[1]/div/input"));
            startYear.sendKeys(filter.getStartYear());
            Thread.sleep(2000);
            WebElement finishYear = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[7]/div/div[2]/div/div/div/div/div[2]/div/input"));
            finishYear.sendKeys(filter.getFinishYear());
            Thread.sleep(2000);

            //Выбор пробега
            WebElement startMileage = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[11]/div/div[2]/div/div/div/div/div[1]/div/input"));
            startMileage.sendKeys(filter.getStartMileage());
            Thread.sleep(2000);
            WebElement finishMileage = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[11]/div/div[2]/div/div/div/div/div[2]/div/input"));
            finishMileage.sendKeys(filter.getFinishMileage());
            Thread.sleep(2000);

            //Битые не битые
            JavascriptExecutor executor = (JavascriptExecutor) webDriver;
            WebElement isCrashed;
            if (filter.isCrashed()) {
                isCrashed = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[16]/div/div[2]/div/div/div/div/div/div[3]/label/input"));
            } else {
                isCrashed = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[16]/div/div[2]/div/div/div/div/div/div[2]/label/input"));
            }
            executor.executeScript("arguments[0].click();", isCrashed);
            Thread.sleep(2000);

            //Цвет
            WebElement color = null;
            switch (filter.getColor()) {
                case ("Белый") :
                    color = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[19]/div/div/div/div/div/div/ul/li[1]/div/span"));
                    break;
                case ("Серебряный"):
                    color = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[19]/div/div/div/div/div/div/ul/li[2]/div/span"));
                    break;
                case ("Серый"):
                    color = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[19]/div/div/div/div/div/div/ul/li[3]/div/span"));
                    break;
                case ("Чёрный") :
                    color = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[19]/div/div/div/div/div/div/ul/li[4]/div/span"));
                    break;
                case ("Коричневый"):
                    color = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[19]/div/div/div/div/div/div/ul/li[5]/div/span"));
                    break;
                case ("Золотой"):
                    color = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[19]/div/div/div/div/div/div/ul/li[6]/div/span"));
                    break;
                case ("Бежевый"):
                    color = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[19]/div/div/div/div/div/div/ul/li[7]/div/span"));
                    break;
                case ("Красный"):
                    color = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[19]/div/div/div/div/div/div/ul/li[8]/div/span"));
                    break;
                case ("Бордовый"):
                    color = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[19]/div/div/div/div/div/div/ul/li[9]/div/span"));
                    break;
                case ("Оранжевый"):
                    color = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[19]/div/div/div/div/div/div/ul/li[10]/div/span"));
                    break;
                case ("Жёлтый"):
                    color = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[19]/div/div/div/div/div/div/ul/li[11]/div/span"));
                    break;
                case ("Зелёный"):
                    color = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[19]/div/div/div/div/div/div/ul/li[12]/div/span"));
                    break;
                case ("Голубой"):
                    color = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[19]/div/div/div/div/div/div/ul/li[13]/div/span"));
                    break;
                case ("Синий"):
                    color = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[19]/div/div/div/div/div/div/ul/li[14]/div/span"));
                    break;
                case ("Фиолетовый"):
                    color = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[19]/div/div/div/div/div/div/ul/li[15]/div/span"));
                    break;
                case ("Пурпурный"):
                    color = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[19]/div/div/div/div/div/div/ul/li[16]/div/span"));
                    break;
                case ("Розовый"):
                    color = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[4]/div[3]/div[1]/div/div[2]/div[1]/form/div[19]/div/div/div/div/div/div/ul/li[17]/div/span"));
                    break;
                default:
                    break;
            }
            executor.executeScript("arguments[0].click();", color);
            Thread.sleep(2000);

            //Принять
            webDriver.findElement(By.className("styles-box-Up_E3")).click();
            Thread.sleep(4000);
        } catch (Exception e) {
            e.printStackTrace();
            webDriver.quit();
        }
    }
}
