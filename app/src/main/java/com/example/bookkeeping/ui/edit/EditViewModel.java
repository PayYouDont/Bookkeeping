package com.example.bookkeeping.ui.edit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public EditViewModel() {
        mText = new MutableLiveData<> ();
        mText.setValue ("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}