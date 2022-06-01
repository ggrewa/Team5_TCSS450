package edu.uw.tcss450.team5.holochat.ui.chats.new_chatroom;
import edu.uw.tcss450.team5.holochat.R;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.team5.holochat.databinding.AddContactCardBinding;
import edu.uw.tcss450.team5.holochat.ui.contacts.info.ContactListSingle;


/**
 * A Recycler View for the New chat view model.
 *
 * @author Ken
 */
public class NewChatRecyclerViewAdapter extends
        RecyclerView.Adapter<NewChatRecyclerViewAdapter.NewChatViewHolder>{

    private final List<ContactListSingle> mContacts;
    private ArrayList<Integer> mMemberID;

    /**
     * A constructor for the new contact recycler view.
     *
     * @param items a list of contacts.
     */
    public NewChatRecyclerViewAdapter(List<ContactListSingle> items) {
        this.mContacts = items;
        this.mMemberID = new ArrayList<>();
    }

    public ArrayList<Integer> getSelectedList(){
        return mMemberID;
    }

    /**
     * Cerates a view holder.
     *
     * @param parent the parent.
     * @param viewType the view type
     *
     * @return a contact view holder.
     */
    public NewChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewChatViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.add_contact_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewChatViewHolder holder, int position) {
        holder.setContact(mContacts.get(position));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    /**
     * An inner class which hold the view for a contact.
     */
    public class NewChatViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public AddContactCardBinding binding;
        public int memberID;

        /**
         * Constructor for the contact view holder.
         *
         * @param view the view.
         */
        public NewChatViewHolder(View view) {
            super(view);
            mView = view;
            binding = AddContactCardBinding.bind(view);

            view.setOnClickListener(v -> {
                updateSelected();
            });
        }

        /**
         * navigates to a contacts profile.
         */
        private void updateSelected(){
            if(mMemberID.contains(memberID)){
                binding.imageSelected.setImageResource(R.drawable.ic_not_selected_24dp);
                int index = mMemberID.indexOf(memberID);
                mMemberID.remove(index);
            }else{
                binding.imageSelected.setImageResource(R.drawable.ic_selected_24dp);
                mMemberID.add(memberID);
            }
        }

        /**
         * Sets the contact.
         *
         * @param contact the contact
         */
        void setContact(final ContactListSingle contact) {
            final Resources res = mView.getContext().getResources();
            final CardView card = binding.cardRoot;

            binding.textUsername.setText(contact.getContactUsername());
            memberID = contact.getContactMemberID();

            card.requestLayout();

        }

    }

}
