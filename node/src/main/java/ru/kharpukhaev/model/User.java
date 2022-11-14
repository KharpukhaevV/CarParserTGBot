package ru.kharpukhaev.model;

import javax.persistence.*;

@Entity
@Table(name = "user", schema="car_parser")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    private Long telegramId;

    private String userName;

    @Enumerated(EnumType.STRING)
    private State state;

    public User() {
    }

    public User(Long telegramId, String userName, State state) {
        this.telegramId = telegramId;
        this.userName = userName;
        this.state = state;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
