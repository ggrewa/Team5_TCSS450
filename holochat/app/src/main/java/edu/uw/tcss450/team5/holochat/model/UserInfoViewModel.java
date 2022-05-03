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
public class UserInfoViewModel extends ViewModel{

    /** The email of the user. */
    private final String mEmail;

    /** The JWT of the user. */
    private final String mJwt;

    /**
     * Constructor that initializes the user's information.
     * @param email the user's email
     * @param jwt the user's JWT
     */
    private UserInfoViewModel(String email, String jwt) {
        mEmail = email;
        mJwt = jwt;
    }

    /**
     * Getter for the user's email.
     *
     * @return the email of the user.
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Getter for the user's JWT.
     *
     * @return the JWT of the user.
     */
    public String getmJwt() {
        return mJwt;
    }

    /**
     * Factory class.
     *
     * @author Charles Bryan
     * @version Spring 2022
     */
    public static class UserInfoViewModelFactory implements ViewModelProvider.Factory {

        /** The email of the user. */
        private final String email;

        /** The JWT of the user. */
        private final String jwt;

        /**
         * Constructor that initializes the email andjwt
         * @param email email of the user
         * @param jwt JWT of the user
         */
        public UserInfoViewModelFactory(String email, String jwt) {
            this.email = email;
            this.jwt = jwt;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == UserInfoViewModel.class) {
                return (T) new UserInfoViewModel(email, jwt);
            }
            throw new IllegalArgumentException(
                    "Argument must be: " + UserInfoViewModel.class);
        }
    }
}
