package com.hswt.broihier.gdo;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;


/**
 * Created by broihier on 4/7/18.
 */

public class CommandThread <Token> extends HandlerThread {
        Handler mHandler;
        Handler mResponseHandler;
        private String result = "not set";

        public interface Listener<Token> {
            void onCommandComplete(String value);
        }

        Listener<Token> mListener;

        public void setListener(Listener<Token> listener) {
            mListener = listener;
        }

        private static String TAG = "commandThread";
        public CommandThread(Handler responseHandler) {
            super(TAG);
            // do command
            mResponseHandler = responseHandler;
        }

        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            mHandler = new Handler() {
                public void handleMessage(Message message) {
                    Token token = (Token)message.obj;
                    Log.d(TAG,"looper got request for: "+message);
                    handleRequest(token);
                }
            };
        }

        public void queueCommand (String ticker) {
            mHandler.obtainMessage(1,ticker).sendToTarget();
        }
        private void handleRequest(final Token message){

            CommandDoor command = new CommandDoor();
            Log.d(TAG,"sending message to controller ");
            command.pushButton();
            // send message
            result = command.getResult();
            try {
                if (result.contains("Communication Failed")){
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                Log.e(TAG,e.getMessage());
            }
            Log.d(TAG,"setting the status to: " +  result);
            

            mResponseHandler.post(new Runnable() {
                public void run () {
                    Log.d(TAG,"got reply from controller - sending back to user interface the message: "+result);
                    mListener.onCommandComplete(result);
                }
            });
        }

        @Override
        public void run() {
            super.run();
            Log.d(TAG,"background thread running");
        }

    }
