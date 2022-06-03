package edu.uw.tcss450.team5.holochat.ui.contacts.info;

/**
 * Stores information about a contact such as email and name
 */
public class Contact {
    private final String mEmail;
    private final String mFirstName;
    private final String mLastName;
    private final String mUserName;
    private final int mMemberID;

    /**
     * A class that represents a contact.
     *
     * @param email the contacts email
     * @param fName contacts first name
     * @param lName contacts last name
     * @param uName contacts username
     * @param id contacts id
     */
    public Contact(String email, String fName, String lName, String uName, int id) {
        this.mEmail = email;
        this.mFirstName = fName;
        this.mLastName = lName;
        this.mUserName = uName;
        this.mMemberID = id;
    }

    /**
     * Get the contacts email.
     *
     * @return the user email.
     */
    public String getContactEmail(){ return mEmail; }

    /**
     * Get the contacts first name.
     *
     * @return returns the contacts first name.
     */
    public String getContactFirstName(){ return mFirstName; }

    /**
     * Get the contacts last name.
     *
     * @return the contacts last name.
     */
    public String getContactLastName(){ return mLastName; }

    /**
     * Get the contacts username.
     *
     * @return the contacts username.
     */
    public String getContactUsername(){ return mUserName; }

    /**
     * Get the contacts username.
     *
     * @return the contacts username.
     */
    public String getContactFullname(){ return mFirstName + " " + mLastName; }

    /**
     * Get the contact memberID.
     *
     * @return the contact memeberID.
     */
    public int getContactMemberID(){ return mMemberID; }

}
