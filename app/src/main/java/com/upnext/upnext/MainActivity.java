package com.upnext.upnext;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private UDPListener listSocket;

    ArrayList<String> fillArray;
    ArrayAdapter fillAdapter;
    ArrayList<String> partyArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        fillArray = new ArrayList<String>();
        fillArray.add("No Local Parties Found");
        fillAdapter = new ArrayAdapter<>(this, R.layout.activity_listview, fillArray);

        partyArray = new ArrayList<String>();

        refreshPartyList();

        try {
            listSocket = new UDPListener();
        } catch (SocketException e) {
            Log.i("UDP", "no longer sending UDP broadcasts cause of error " + e.getMessage());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void joinPartyDialog(View v) {
        final Intent startJoinPartyActivity = new Intent(this, PartyClientActivity.class);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Join Party");
        alertBuilder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(startJoinPartyActivity);
            }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertBuilder.show();
    }

    public void createPartyDialog(View v) {
        View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.user_input_create, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setView(view);

        final EditText pName = (EditText) view.findViewById(R.id.user_input_partyname);
        final EditText pPass = (EditText) view.findViewById(R.id.user_input_partypass);

        final Intent startCreatePartyActivity = new Intent(this, PartyHostActivity.class);
        alertBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startCreatePartyActivity.putExtra("pName", pName.getText().toString());
                startCreatePartyActivity.putExtra("pPass", pPass.getText().toString());
                listSocket.stopReceive();
                startActivity(startCreatePartyActivity);
            }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertBuilder.show();

    }

    public void onClickSettingsButton(View v) {
        Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
        startActivity(startSettingsActivity);
    }

    public void onClickRefreshButton(View v) {
        refreshPartyList();
    }

    public void refreshPartyList() {

        if(listSocket != null) {
            listSocket.listenForUDPBroadcast();
            if(listSocket.getPartyName() != null)
             partyArray.add(listSocket.getPartyName()); }

        ListView partyListView;

        partyListView = (ListView) findViewById(R.id.list_party);

        if(!partyArray.isEmpty()) {
            ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, R.layout.activity_listview, partyArray);
            partyListView.setAdapter(arrayAdapter);
        } else {
            partyListView.setAdapter(fillAdapter);
        }

    }


}
