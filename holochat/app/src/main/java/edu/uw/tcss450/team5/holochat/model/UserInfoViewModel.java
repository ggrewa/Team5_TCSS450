/*
 * TCSS450
 * Mobile Application Programming
 * Spring 2022
 */
package edu.uw.tcss450.team5.holochat.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/*
 * Class that stores the user's information.
 *
 * @author Charles Bryan
 * @version Spring 2022
 */
public class UserInfoViewModel extends ViewModel {
    private final String mEmail;
    private final String mJwt;
    private final String mFName;
    private final String mLName;
    private final String mUsername;
    private final int mMemberID;

    /**
     * A constructor for the User Info View model which takes a email first name, last name,
     * username, memebrId, and  a json web token
     *
     * @param email user email
     * @param fName user first name
     * @param lName user last name
     * @param username users username
     * @param memberID users memberID
     * @param jwt users jwt
     */
    private UserInfoViewModel(String email, String fName, String lName,
                              String username, int memberID, String jwt) {

        //May want to store location information about users?
        mEmail = email;
        mJwt = jwt;
        mFName = fName;
        mLName = lName;
        mUsername = username;
        mMemberID = memberID;
    }

    /**
     * A method for getting a used email.
     *
     * @return a String representing the user email.
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * A method for obtaining the json web token for the user.
     *
     * @return A string representing a json web token.
     */
    public String getJwt() { return mJwt; }

    public String getFName(){ return mFName; }

    public String getLName(){ return mLName; }

    public String getUsername() { return mUsername; }

    public int getMemberID() { return mMemberID; }


    /**
     * An inner class used to create user info View Models
     */
    public static class UserInfoViewModelFactory implements ViewModelProvider.Factory {
        private final String email;
        private final String jwt;
        private final String fName;
        private final String lName;
        private final String username;
        private final int memberID;

        /**
         * A constructor for the User Info View model factory which takes a name and a
         * json web token.
         *
         * @param email a string representing an email.
         * @param jwt a string representing a json web token
         */
        public UserInfoViewModelFactory(String email, String fName, String lName,
                                        String username, int memberID, String jwt) {
            this.email = email;
            this.jwt = jwt;
            this.fName = fName;
            this.lName = lName;
            this.username = username;
            this.memberID = memberID;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == UserInfoViewModel.class) {
                return (T) new UserInfoViewModel(email, fName, lName, username, memberID, jwt);
            }
            throw new IllegalArgumentException(
                    "Argument must be: " + UserInfoViewModel.class);
        }
    }
}
