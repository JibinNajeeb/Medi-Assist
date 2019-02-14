package com.hack.android.medassist.chatBot.model;

/**
 * Created by deathcode on 26/01/18.
 */

public class ResponseMessage {

    String text;
    String imageUrl;
    boolean isMe;

    public ResponseMessage(String text, boolean isMe) {
        this.text = text;
        this.isMe = isMe;
    }

    public ResponseMessage(String text, boolean isMe, String imageUrl) {
        this.text = text;
        this.isMe = isMe;
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
