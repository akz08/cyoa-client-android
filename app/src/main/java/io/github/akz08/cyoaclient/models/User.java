package io.github.akz08.cyoaclient.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    // Attributes

    @SerializedName("api_key")
    private String apiKey;
    private String email;
    @SerializedName("first_name")
    private String firstName;
    private String gender;
    @PrimaryKey
    private long id;
    @SerializedName("last_name")
    private String lastName;
    private String link;
    private String locale;
    private String name;
    private float timezone;
    @SerializedName("updated_time")
    private Date updatedTime;
    private boolean verified;

    public String getApiKey() { return apiKey; }
    public void setApiKey(final String apiKey) { this.apiKey = apiKey; }

    public String getEmail() { return email; }
    public void setEmail(final String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(final String firstName) { this.firstName = firstName; }

    public String getGender() { return gender; }
    public void setGender(final String gender) { this.gender = gender; }

    public long getId() { return id; }
    public void setId(final long id) { this.id = id; }

    public String getLastName() { return lastName; }
    public void setLastName(final String lastName) { this.lastName = lastName; }

    public String getLink() { return link; }
    public void setLink(final String link) { this.link = link; }

    public String getLocale() { return locale; }
    public void setLocale(final String locale) { this.locale = locale; }

    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }

    public float getTimezone() { return timezone; }
    public void setTimezone(final float timezone) { this.timezone = timezone; }

    public Date getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(final Date updatedTime) { this.updatedTime = updatedTime; }

    public boolean isVerified() { return verified; }
    public void setVerified(final boolean verified) { this.verified = verified; }

    // Relationships

    private RealmList<Character> characters;
    private RealmList<Scene> scenes;
    private RealmList<Message> messages;

    public RealmList<Character> getCharacters() { return characters; }
    public void setCharacters(final RealmList<Character> characters) {
        this.characters = characters;
    }

    public RealmList<Scene> getScenes() { return scenes; }
    public void setScenes(final RealmList<Scene> scenes) { this.scenes = scenes; }

    public RealmList<Message> getMessages() { return messages; }
    public void setMessages(final RealmList<Message> messages) { this.messages = messages; }
}