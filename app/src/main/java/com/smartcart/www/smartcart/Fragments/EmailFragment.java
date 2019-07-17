package com.smartcart.www.smartcart.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.smartcart.www.smartcart.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmailFragment extends Fragment {


    private EditText mEditTextSubject;
    private EditText mEditTextMessage;

    public EmailFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {





        return inflater.inflate(R.layout.fragment_email, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeControls(view);


         Button buttonSend = view.findViewById(R.id.button_send);
         buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });
    }

    private void sendMail() {
        Uri uri = Uri.parse("smartcartsupport@gmail.com");
        String recipientList = uri.toString();
        String[] recipients = recipientList.split(",");

        String subject = mEditTextSubject.getText().toString();
        String message = mEditTextMessage.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND,uri);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "בחר את האמצעי לשליחת ההודעה"));

    }

    private void initializeControls (View rootView){
        
        mEditTextSubject = rootView.findViewById(R.id.edit_text_subject);
        mEditTextMessage = rootView.findViewById(R.id.edit_text_message);


    }
}
