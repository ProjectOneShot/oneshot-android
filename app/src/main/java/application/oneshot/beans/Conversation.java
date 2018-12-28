package application.oneshot.beans;

import java.io.Serializable;

import application.oneshot.models.Contact;
import application.oneshot.models.Message;

public class Conversation
        implements Serializable {

    private Contact mContact;
    private Message mMessage;

    public Contact getContact() {
        return mContact;
    }

    public void setContact(Contact contact) {
        this.mContact = contact;
    }

    public Message getMessage() {
        return mMessage;
    }

    public void setMessage(Message message) {
        this.mMessage = message;
    }
}
