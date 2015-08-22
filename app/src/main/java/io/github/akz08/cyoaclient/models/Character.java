package io.github.akz08.cyoaclient.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Character extends RealmObject {

    // Attributes

    @PrimaryKey
    private int id;
    private String name;
    private int age;
    private String gender;
    private String description;

    public int getId() { return id; }
    public void setId(final int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(final int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(final String gender) { this.gender = gender; }

    public String getDescription() { return description; }
    public void setDescription(final String description) { this.description = description; }

    // Relationships

    private RealmList<Scene> scenes;

    public RealmList<Scene> getScenes() { return scenes; }
    public void setScenes(final RealmList<Scene> scenes) { this.scenes = scenes; }
}