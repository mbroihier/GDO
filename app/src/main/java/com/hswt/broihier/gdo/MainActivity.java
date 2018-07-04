package com.hswt.broihier.gdo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView status;
    private Button button;
    private CommandThread commandThread;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        commandThread = new CommandThread( new Handler());
        commandThread.setListener(new CommandThread.Listener<String>(){
            public void onCommandComplete(String result) {
                status.setText(result);
            }
        });
        commandThread.start();
        commandThread.getLooper();

    }
    /* Method initializeViews - initializes views of the activity object                                             */
    /* ============================================================================================================= */
    /* ------------------------------------------------------------------------------------------------------------- */
    /*                                                                                                               */
    /* Description:                                                                                                  */
    /*     This method creates the objects accessed by this main activity object: a status label and a command       */
    /* button.                                                                                                       */
    /* ------------------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------------------------------------------------------------------------- */
    /*                                                                                                               */
    /* Inputs:                                                                                                       */
    /*     Mnemonic      Parameter                      Source                                                       */
    /*   ___________    ___________                     ____________________________________________________________ */
    /*                                                                                                               */
    /* ------------------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------------------------------------------------------------------------- */
    /*                                                                                                               */
    /* Processing:                                                                                                   */
    /*  create a status label;                                                                                       */
    /*  create a button to command an open or close command;                                                         */
    /*                                                                                                               */
    /* ------------------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------------------------------------------------------------------------- */
    /*                                                                                                               */
    /* Outputs:                                                                                                      */
    /*     Mnemonic      Parameter                      Destination                                                  */
    /*   ___________    ___________                     ____________________________________________________________ */
    /*     status       Text view that holds the status Used internally                                              */
    /*                  of the command sent                                                                          */
    /*     button       Button that triggers the action Used internally                                              */
    /*                  to command the door                                                                          */
    /*                                                                                                               */
    /* ------------------------------------------------------------------------------------------------------------- */
    /* ============================================================================================================= */
    public void initializeViews() {

        button = (Button) findViewById(R.id.button);
        status = (TextView) findViewById(R.id.status);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                button.setClickable(false);
                status.setText("Processing .....");
                commandThread.queueCommand("command door");
                button.setClickable(true);
            }
        });

    }


}
