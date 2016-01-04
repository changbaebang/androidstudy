package com.example.bangchangbae.helloworld;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.util.Log;

import java.util.Random;

public class MyService extends Service {
    private final IBinder mBinder = new MyBinder();
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private final String [] mGreetings = {"hello", "hi", "what's up"};
    public MyService() {
    }

    @Override
    public void onCreate() {
        Log.d("MyService", "onCreate");
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String actionName;
        if (intent == null) {
            actionName = "no intent";
        } else {
            actionName = intent.getAction();
        }
        Log.d("MyService", "onStartCommand intent : " + actionName + "flags : " + flags + " startId : " + startId);

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("MyService", "onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        String actionName;
        if (intent == null) {
            actionName = "no intent";
        } else {
            actionName = intent.getAction();
        }
        Log.d("MyService", "onBind intent : " + actionName);
        return mBinder;
    }
    public String getGreeting(){
        return mGreetings[new Random().nextInt(mGreetings.length)];
    }

    public class MyBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    private class ServiceHandler extends Handler{
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            long endTime = System.currentTimeMillis() + 5*1000;
            while(System.currentTimeMillis() < endTime){
                synchronized (this){
                    try{
                        wait(endTime - System.currentTimeMillis());
                    }catch (Exception e){
                        Log.e("MyService", e.getMessage());
                    }
                }
            }

            Intent startIntet = new Intent(MyService.this, MainActivity.class);
            startIntet.putExtra("text", getGreeting());
            startIntet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntet);

            stopSelf(msg.arg1);

        }
    }
}
