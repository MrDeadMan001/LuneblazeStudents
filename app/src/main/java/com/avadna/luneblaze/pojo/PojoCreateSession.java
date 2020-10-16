package com.avadna.luneblaze.pojo;

import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoTopic;
import com.avadna.luneblaze.pojo.pojoVenueList.PojoGetVenueListResponseData;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class PojoCreateSession {
    public String userId;
    public String title;
    public ArrayList<PojoTopic> topics;
    public ArrayList<PojoTopic> prerequisite;
    public String description;
    public String cover_photo_path;

    public ArrayList<String> interest_ids;
    public ArrayList<String> venues;

    public ArrayList<String> venue1Dates;
    public ArrayList<String> venue2Dates;
    public ArrayList<String> venue3Dates;

    public String ans1;
    public String ans2;
    public String people_allowed;


    public PojoCreateSession(int a) {
        userId = "";
        title = "";
        topics = new ArrayList<>();
        prerequisite = new ArrayList<>();
        description = "";
        interest_ids = new ArrayList<>();
        venues = new ArrayList<>();
        venue1Dates = new ArrayList<>();
        venue2Dates = new ArrayList<>();
        venue3Dates = new ArrayList<>();

        ans1 = "";
        ans2 = "";
        people_allowed = "";
    }
}

