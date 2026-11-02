package com.example.taskflow.util;

import android.text.Editable;
import android.text.TextWatcher;

/** A TextWatcher that only reports the new text, so callers can use a lambda. */
public class SimpleTextWatcher implements TextWatcher {

    public interface Callback {
        void onTextChanged(String text);
    }

    private final Callback callback;

    public SimpleTextWatcher(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        callback.onTextChanged(s.toString());
    }
}
