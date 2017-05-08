package com.rxjava.nan.rxjava_demo01.bean;

import java.io.Serializable;

/**
 * Created by Nan on 2017/5/5.
 */

public class Record implements Serializable {
    private int id;
    private String cardId;
    private String cardName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
}
