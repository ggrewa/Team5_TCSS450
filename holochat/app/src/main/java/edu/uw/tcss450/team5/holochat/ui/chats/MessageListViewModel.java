package edu.uw.tcss450.team5.holochat.ui.chats;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageListViewModel extends AndroidViewModel {
    private MutableLiveData<List<MessagePost>> mMessageList;
    private final MutableLiveData<JSONObject> mResponse;

    public MessageListViewModel(@NonNull Application application) {
        super(application);
        mMessageList = new MutableLiveData<>(new ArrayList<>());
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }
}
