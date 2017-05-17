package com.upnext.upnext;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by Steven on 5/10/2017.
 */

public class PartyMetadata implements Serializable{

    private String partyName;
    private String partyCode;
    private int partyID;
    private int numMembers;
    private int portNumber;


    public PartyMetadata(String inpPartyName, String inpPartyCode) {

        partyName = inpPartyName;
        partyCode = inpPartyCode;
        int numMembers = 1;

        Random rand = new Random();
        partyID = rand.nextInt(1000);
        portNumber = rand.nextInt(8999) + 1000;

    }

    public String getPartyName() {
        return partyName;
    }

    public String getPartyCode() {
        return partyCode;
    }

    public int getNumMembers() {
        return numMembers;
    }

    public int getPortNumber() {
        return portNumber;
    }
}
