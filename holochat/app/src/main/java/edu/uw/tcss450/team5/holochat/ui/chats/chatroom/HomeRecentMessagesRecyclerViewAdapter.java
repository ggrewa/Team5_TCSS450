package edu.uw.tcss450.team5.holochat.ui.chats.chatroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentMessageCardBinding;
import edu.uw.tcss450.team5.holochat.ui.chats.chatroom_list.MessagePost;
import edu.uw.tcss450.team5.holochat.ui.home.HomeFragmentDirections;

/**
 * Puts messages from the view model in a tangible form for display
 * Used by the home page to show recent chatrooms the user has used.
 *
 * TODO: actually have it sorted by message timestamps, currently it is same as message list
 */
public class HomeRecentMessagesRecyclerViewAdapter extends
        RecyclerView.Adapter<HomeRecentMessagesRecyclerViewAdapter.MessageViewHolder> {

    private final List<MessagePost> mMessages;
    private final FragmentManager mFragMan;

    /**
     * A constructor for the message recycler view.
     *
     * @param items a list of message posts.
     */
    public HomeRecentMessagesRecyclerViewAdapter(List<MessagePost> items, FragmentManager fm) {
        this.mMessages = items;
        mFragMan = fm;
    }

    /**
     * Creates a message view holder.
     *
     * @param parent   the parent
     * @param viewType the view type.
     * @return a message view holder
     */
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_message_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.setMessage(mMessages.get(position));
    }

    /**
     * Get the cont of messages.
     *
     * @return the number of messages.
     */
    public int getItemCount() {
        return mMessages.size();
    }

    /**
     * A message view holder class
     */
    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public FragmentMessageCardBinding binding;
        public MessagePost mPost;

        /**
         * contructs a message view holder.
         *
         * @param view the view.
         */
        public MessageViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentMessageCardBinding.bind(view);

            //may need to change
            view.setOnClickListener(v -> {
                navigateToChat();
            });
        }

        /**
         * navigates to a chat room associated with the message post.
         * Passes in the argument from post: chatId and chatroomName
         */
        private void navigateToChat() {
            System.out.println(mPost.getChatID());
            HomeFragmentDirections.ActionNavigationHomeToChatRoomFragment directions =
                    HomeFragmentDirections.
                            actionNavigationHomeToChatRoomFragment(mPost.getChatID(),
                                    mPost.getMessageName());

            Navigation.findNavController(mView).navigate(directions);
        }

        /**
         * Sets the message for the view holder.
         *
         * @param message the message.
         */
        void setMessage(final MessagePost message) {
            binding.textMessageName.setText(message.getMessageName());
            mPost = message;
            binding.buttonDelete.setVisibility(View.GONE); //dont have delete option here
        }

        /**
         * Remove the chatroom and update the recycler view
         */
        public void deleteRequest(){
            mMessages.remove(mPost);
            notifyDataSetChanged();
        }
    }
}
