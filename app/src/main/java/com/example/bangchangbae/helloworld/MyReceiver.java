package com.example.bangchangbae.helloworld;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.example.bangchangbae.changetext")){
            String text = intent.getStringExtra("text");

            Intent startIntet = new Intent(context, MainActivity.class);
            startIntet.putExtra("text", text);
            startIntet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntet);
        }
    }
}
