package io.github.akz08.cyoaclient.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Scene extends RealmObject {

    // Atributes

    @PrimaryKey
    private int id;
    private String information;

    public int getId() { return id; }
    public void setId(final int id) { this.id = id; }

    public String getInformation() { return information; }
    public void setInformation(final String information) { this.information = information; }

    // Relationships

    private Character character;
    private RealmList<Message> messages;

    public Character getCharacter() { return character; }
    public void setCharacter(final Character character) { this.character = character; }

    public RealmList<Message> getMessages() { return messages; }
    public void setMessages(final RealmList<Message> messages) { this.messages = messages; }
}