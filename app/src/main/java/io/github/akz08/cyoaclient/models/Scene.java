package io.github.akz08.cyoaclient.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Scene extends RealmObject {
    @PrimaryKey
    private int id;
    private Character character;
    private String information;

    public int getId() { return id; }
    public void setId(final int id) { this.id = id; }

    public Character getCharacter() { return character; }
    public void setCharacter(final Character character) { this.character = character; }

    public String getInformation() { return information; }
    public void setInformation(final String information) { this.information = information; }
}