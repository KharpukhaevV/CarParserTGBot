package ru.kharpukhaev.model;

import javax.persistence.*;

@Entity
@Table(name = "advertisement", schema="car_parser")
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ad_id", unique = true, nullable = false)
    private long id;

    private long avitoId;

    private String title;

    private String price;

    private String mileage;
    @Column(columnDefinition = "varchar(1000)")
    private String description;
    @Column(columnDefinition = "varchar(2000)")
    private String image;

    private String url;

    private long filterId;

    public Advertisement() {
    }

    public Advertisement(long avitoId, String title, String price, String mileage, String description, String image, String url, long filterId) {
        this.avitoId = avitoId;
        this.title = title;
        this.price = price;
        this.mileage = mileage;
        this.description = description;
        this.image = image;
        this.url = url;
        this.filterId = filterId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getAvitoId() {
        return avitoId;
    }

    public void setAvitoId(long avitoId) {
        this.avitoId = avitoId;
    }

    public long getFilterId() {
        return filterId;
    }

    public void setFilterId(long filterId) {
        this.filterId = filterId;
    }
}
