package com.upnext.upnext;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRouter;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaRouteSelector;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.common.api.GoogleApiClient;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.prefs.Preferences;

public class PartyHostActivity extends AppCompatActivity {


    private MediaRouteButton mMediaRouteButton;

    String partyName;
    String partyPass;

    PartyMetadata party;

    private UDPSender listSocket;

    PartyMember[] partyMembers = new PartyMember[10];
    private ArrayList<String> memberArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_host);

        /*
        mMediaRouteButton = (MediaRouteButton) findViewById(R.id.media_route_button);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), mMediaRouteButton);
        CastContext mCastContext = CastContext.getSharedInstance(this);
        */

        partyName = getIntent().getExtras().getString("pName");
        partyPass = getIntent().getExtras().getString("pPass");
        party = new PartyMetadata(partyName, partyPass);

        getSupportActionBar().setTitle(party.getPartyName());
        getSupportActionBar().setSubtitle(party.getPartyCode());

        TextView tA = (TextView)findViewById(R.id.textViewA);
        TextView tB = (TextView)findViewById(R.id.textViewB);

        /*
        ArrayList<String> memberArray = new ArrayList<String>();
        memberArray.add(getString(R.string.pref_title_display_name));
        ArrayAdapter memberAdapter = new ArrayAdapter<>(this, R.layout.activity_listview, memberArray);
        ListView partyListView = (ListView) findViewById(R.id.list_party);
        partyListView.setAdapter(memberAdapter);
        */

        try {
            listSocket = new UDPSender(party);
        } catch (SocketException e) {
            Log.i("UDP", "socket not established cause of error " + e.getMessage());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),menu,R.id.media_route_menu_item);
        Log.i("A","Tried to setup cast button");
        return true;
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitPrompt();
        }
        return true;
    }

    public void onClickDisband(View v) {
        exitPrompt();
    }

    public void exitPrompt() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                disband();
            }
        });
        alertDialog.setNegativeButton("No", null);
        alertDialog.setMessage("Do you want to disband the party?");
        alertDialog.show();
    }

    public void disband() {
        if(listSocket != null)
            listSocket.stopSend();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void onMemberJoin() {
        memberArray.add(getString(R.string.pref_title_display_name));
        Collections.sort(memberArray, String.CASE_INSENSITIVE_ORDER);
    }

}
