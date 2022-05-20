package edu.uw.tcss450.team5.holochat.ui.contacts;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class ContactListSingle implements Serializable {

    private final String mContact;
    private final String mEmail;
    private final int mContactId;

    public ContactListSingle(int contactId, String contact, String email) {
        this.mContact = contact;
        this.mEmail = email;
        this.mContactId = contactId;
    }

    /**
     * Static factory method to turn a properly formatted JSON String into a
     * ContactListSingle object.
     * @param cmAsJson the String to be parsed into a Contact Object.
     * @return a ContactListSingle Object with the details contained in the JSON String.
     * @throws JSONException when cmAsString cannot be parsed into a ChatMessage.
     */
    public static ContactListSingle createFromJsonString(final String cmAsJson) throws JSONException {
        final JSONObject msg = new JSONObject(cmAsJson);
        return new ContactListSingle(
                msg.getInt("memberid"),
                msg.getString("username"),
                msg.getString("email"));
    }

    public String getContact() {
        return mContact;
    }

    public String getContactEmail() {
        return mEmail;
    }

    public int getContactId() {
        return mContactId;
    }
}

