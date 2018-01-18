package com.example.student.diary_project;

import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by student on 2018-01-18.
 */

public class WriteNowListener implements View.OnClickListener {
    EditText editText;

    public WriteNowListener(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void onClick(View v) {
        Date today = new Date();
        Locale locale = Locale.US;
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", locale);
        String now = sdf.format(today);

        int cursorPosition = editText.getSelectionStart();
        CharSequence enteredText = editText.getText().toString();
        CharSequence startToCursor = enteredText.subSequence(0, cursorPosition);
        CharSequence cursorToEnd = enteredText.subSequence(cursorPosition, enteredText.length());

        editText.setText(startToCursor + now + " " + cursorToEnd);

        // 커서 위치 조정
        editText.setSelection(cursorPosition + 9);
    }
}
