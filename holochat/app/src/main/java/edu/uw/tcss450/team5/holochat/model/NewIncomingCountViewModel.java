package edu.uw.tcss450.team5.holochat.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class NewIncomingCountViewModel extends ViewModel {
    private MutableLiveData<Integer> mContactCount;


    public NewIncomingCountViewModel() {
        mContactCount = new MutableLiveData<>();
        mContactCount.setValue(0);
    }

    public void addMessageCountObserver(@NonNull LifecycleOwner owner,
                                        @NonNull Observer<? super Integer> observer) {
        mContactCount.observe(owner, observer);
    }

    public void increment() {
        mContactCount.setValue(mContactCount.getValue() + 1);
    }

    public void reset() {
        mContactCount.setValue(0);
    }
}