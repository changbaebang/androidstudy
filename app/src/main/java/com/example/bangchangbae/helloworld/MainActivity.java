package com.example.bangchangbae.helloworld;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    MyService mService;
    boolean mBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder binder = (MyService.MyBinder)service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intent = getIntent();
        String text = intent.getStringExtra("text");
        if(text != null && !text.isEmpty())
            setText(text);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBound){
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "clicked settings", Toast.LENGTH_SHORT).show();
            return true;
        }else if(id == R.id.action_unbind_call){
            Intent intent = new Intent(this, MyService.class);
            startService(intent);
            return true;
        }else if(id == R.id.action_unbind_stop){
            Intent intent = new Intent(this, MyService.class);
            stopService(intent);
            return true;
        }
        else if(id == R.id.action_bind){
            Intent intent = new Intent(this, MyService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            return true;
        }
        else if(id == R.id.action_unbind){
            if(mBound){
                unbindService(mConnection);
                mBound = false;
            }
        }
        else if(id == R.id.action_bind_call){
            if(!mBound)
                Toast.makeText(MainActivity.this, "Need to Bind", Toast.LENGTH_SHORT).show();
            else
                setText(mService.getGreeting());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void setText(String text){
        TextView textView = (TextView)findViewById(R.id.textview);
        textView.setText(text);
        ContentValues values = new ContentValues();
        values.put(MyContentProvider.name, text);
        Uri uri = getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
        if(uri != null)
            Toast.makeText(MainActivity.this, "insert greeting to my content provider", Toast.LENGTH_SHORT).show();
    }
}
