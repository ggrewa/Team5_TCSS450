package edu.uw.tcss450.team5.holochat.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class NewIncomingCountViewModel extends ViewModel {
    private MutableLiveData<Integer> mNewContactCount;


    public NewIncomingCountViewModel() {
        mNewContactCount = new MutableLiveData<>();
        mNewContactCount.setValue(0);
    }

    public void addMessageCountObserver(@NonNull LifecycleOwner owner,
                                        @NonNull Observer<? super Integer> observer) {
        mNewContactCount.observe(owner, observer);
    }

    public void increment() {
        mNewContactCount.setValue(mNewContactCount.getValue() + 1);
    }

    public void reset() {
        mNewContactCount.setValue(0);
    }
}