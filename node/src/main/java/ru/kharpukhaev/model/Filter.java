package ru.kharpukhaev.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "filter", schema="car_parser")
public class Filter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "filter_id", unique = true, nullable = false)
    private Long id;

    private Long telegramId;

    private String mark;

    private String model;

    private String startPrice;

    private String finishPrice;

    private String startYear;

    private String finishYear;

    private String startMileage;

    private String finishMileage;

    private boolean isCrashed;

    private String color;

    private Boolean updates;

    public Boolean getUpdates() {
        return updates;
    }

    public void setUpdates(Boolean updates) {
        this.updates = updates;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(String startPrice) {
        this.startPrice = startPrice;
    }

    public String getFinishPrice() {
        return finishPrice;
    }

    public void setFinishPrice(String finishPrice) {
        this.finishPrice = finishPrice;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getFinishYear() {
        return finishYear;
    }

    public void setFinishYear(String finishYear) {
        this.finishYear = finishYear;
    }

    public String getStartMileage() {
        return startMileage;
    }

    public void setStartMileage(String startMileage) {
        this.startMileage = startMileage;
    }

    public String getFinishMileage() {
        return finishMileage;
    }

    public void setFinishMileage(String finishMileage) {
        this.finishMileage = finishMileage;
    }

    public boolean isCrashed() {
        return isCrashed;
    }

    public void setCrashed(boolean crashed) {
        isCrashed = crashed;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
