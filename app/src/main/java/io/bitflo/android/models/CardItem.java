package io.bitflo.android.models;

/**
 *
 * Created by Vishal on 10/25/15.
 */
public class CardItem {

    public String from_facebook;
    public String from_picture;
    public String from_name;
    public String to_facebook;
    public String to_picture;
    public String to_name;
    public float amount;
    public String message;

    public CardItem(String from_facebook, String from_picture, String from_name, String to_facebook, String to_picture, String to_name, float amount, String message) {
        this.from_facebook = from_facebook;
        this.from_picture = from_picture;
        this.from_name = from_name;
        this.to_facebook = to_facebook;
        this.to_picture = to_picture;
        this.to_name = to_name;
        this.amount = amount;
        this.message = message;
    }
}
