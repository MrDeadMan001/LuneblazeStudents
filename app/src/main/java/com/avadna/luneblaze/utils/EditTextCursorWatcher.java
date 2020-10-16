package com.avadna.luneblaze.utils;

import android.content.Context;

import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class EditTextCursorWatcher extends androidx.appcompat.widget.AppCompatEditText {

    public interface onSelectionChangedListener {
        public void onSelectionChanged(int selStart, int selEnd);
    }


    private List<onSelectionChangedListener> listeners=new ArrayList<>();

    public EditTextCursorWatcher(Context context) {
        super(context);
        listeners = new ArrayList<onSelectionChangedListener>();
    }

    public EditTextCursorWatcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        listeners = new ArrayList<onSelectionChangedListener>();
    }

    public EditTextCursorWatcher(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        listeners = new ArrayList<onSelectionChangedListener>();
    }

    public void addOnSelectionChangedListener(onSelectionChangedListener o) {
        listeners.add(o);
    }


    protected void onSelectionChanged(int selStart, int selEnd) {
        if(listeners!=null){
            for (onSelectionChangedListener l : listeners)
                l.onSelectionChanged(selStart, selEnd);
        }
    }

}