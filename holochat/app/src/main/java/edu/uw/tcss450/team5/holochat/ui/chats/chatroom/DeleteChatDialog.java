package edu.uw.tcss450.team5.holochat.ui.chats.chatroom;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import org.json.JSONObject;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentMessageListBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.ui.chats.chatroom_list.MessageListRecyclerViewAdapter;
import edu.uw.tcss450.team5.holochat.ui.chats.new_chatroom.MessageListViewModel;
import edu.uw.tcss450.team5.holochat.ui.chats.chatroom_list.MessagePost;

/**
 * Dialog prompt to delete a chat
 *
 * @author Ken
 */
public class DeleteChatDialog extends DialogFragment {
    private UserInfoViewModel mUserModel;
    private MessageListViewModel mMessageModel;
    private final MessageListRecyclerViewAdapter.MessageViewHolder mUpdater;
    private final FragmentManager mFragMan;
    private final MessagePost mMessagePost;
    private final String mChatName;
    private final int mChatID;


    /**
     * Constructor for the delete chat dialog
     *
     * @param mp MessagePost the name of chat and id
     * @param fm the fragment manager
     * @param updater messagelistrecyclerviewadpter
     */
    public DeleteChatDialog(MessagePost mp, FragmentManager fm,  MessageListRecyclerViewAdapter.MessageViewHolder updater){
        mMessagePost = mp;
        mChatName = mp.getMessageName();
        mChatID = mp.getChatID();
        mUpdater = updater;
        mFragMan = fm;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserModel = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);

        mMessageModel = new ViewModelProvider(getActivity()).get(MessageListViewModel.class);
    }

    /**
     * The view created for dialog.
     *
     * @param view the view
     * @param savedInstanceState the saved instance state.
     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentMessageListBinding binding = FragmentMessageListBinding.bind(getView());

        mMessageModel.addMessageListObserver(getViewLifecycleOwner(), messageList -> {
            binding.listRoot.setAdapter(
                    new MessageListRecyclerViewAdapter(messageList, mFragMan)
            );
            binding.layoutWait.setVisibility(View.GONE);
        });
    }

    /**
     * Create an alert dialog to prompt the user to delete the chatroom
     * source: https://gist.github.com/tobiasstraub/a7039064903886584901
     * @param savedInstanceState
     * @return built dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_delete_chat_dialog, null);
        String deleteTitle = getResources().getString(R.string.text_chat_delete_dialog_title) +
                "\n\"" + mChatName + "\"?";
        builder.setTitle(deleteTitle);
        builder.setMessage(R.string.text_chat_delete_dialog_prompt);
        builder.setIcon(R.drawable.ic_trash_red24dp);
        builder.setView(view)
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {

                    @Override
                    //call webservice to delete the chatroom
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mMessageModel.connectDelete(mUserModel.getJwt(), mChatID,
                                mUserModel.getEmail());
                        mUpdater.deleteRequest(); //update recycler view
                    }
                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

        return builder.create();
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to SignInViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                System.out.println("Failed to delete chat");
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }
}