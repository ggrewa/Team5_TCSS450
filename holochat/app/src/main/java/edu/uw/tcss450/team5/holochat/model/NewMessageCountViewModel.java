package edu.uw.tcss450.team5.holochat.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class NewMessageCountViewModel extends ViewModel {

    private MutableLiveData<Integer> mNewMessageCount;
    private HashMap<Integer,Integer> notifMap;

    public NewMessageCountViewModel() {
        notifMap = new HashMap<Integer,Integer>();
        mNewMessageCount = new MutableLiveData<>();
        mNewMessageCount.setValue(0);
    }

    public void addMessageCountObserver(@NonNull LifecycleOwner owner,
                                        @NonNull Observer<? super Integer> observer) {
        mNewMessageCount.observe(owner, observer);
    }

    public void increment(int ID) {
        notifMap.put(ID,notifMap.getOrDefault(ID,0)+1);
        mNewMessageCount.setValue(mNewMessageCount.getValue().intValue() + 1);
    }

    public void reset(int ID) {
        try {
            mNewMessageCount.setValue(mNewMessageCount.getValue() - notifMap.get(ID));
            notifMap.put(ID, 0);
        } catch (Exception e)
        {
            
        }
    }

}
