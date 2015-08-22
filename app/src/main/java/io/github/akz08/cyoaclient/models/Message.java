package io.github.akz08.cyoaclient.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Message extends RealmObject {

    // Attributes

    @PrimaryKey
    private int id;
    private String text;
    private boolean fromCharacter;
    private int delay;
    private Date exchangeDate;

    public int getId() { return id; }
    public void setId(final int id) { this.id = id; }

    public String getText() { return text; }
    public void setText(final String text) { this.text = text; }

    public boolean getFromCharacter() { return fromCharacter; }
    public void setFromCharacter(final boolean fromCharacter) { this.fromCharacter = fromCharacter; }

    public int getDelay() { return delay; }
    public void setDelay(final int delay) { this.delay = delay; }

    public Date getExchangeDate() { return exchangeDate; }
    public void setExchangeDate(final Date exchangeDate) { this.exchangeDate = exchangeDate; }

    // Relationships

    private Character character;
    private Scene scene;
    private Message parent;

    public Character getCharacter() { return character; }
    public void setCharacter(final Character character) { this.character = character; }

    public Scene getScene() { return scene; }
    public void setScene(final Scene scene) { this.scene = scene; }

    public Message getParent() { return parent; }
    public void setParent(final Message parent) { this.parent = parent; }
}