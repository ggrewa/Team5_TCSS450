package edu.uw.tcss450.team5.holochat.ui.contacts;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class ContactListSingle implements Serializable {

    private final String mContactUsername;
    private final String mContactEmail;
    private final int mContactMemberID;

    public ContactListSingle(int contactId, String contact, String email) {
        this.mContactUsername = contact;
        this.mContactEmail = email;
        this.mContactMemberID = contactId;
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

    public String getContactUsername() {
        return mContactUsername;
    }

    public String getContactEmail() {
        return mContactEmail;
    }

    public int getContactMemberID() {
        return mContactMemberID;
    }
}

