package cabiso.daphny.com.g_companion.Recommend;

import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Bidding.ToBidProduct;
import cabiso.daphny.com.g_companion.Geofencing.LocationService;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.User_Profile;

/**
 * Created by cicctuser on 9/28/2018.
 */

public class Recommend {
    private ArrayList<DBMaterial> imgRecogMaterials;
    private ArrayList<DIYnames> recommendedItems;

    private RecommendDIYAdapter adapter;
    private DatabaseReference databaseReference;
    private DatabaseReference userProfile;
    private User_Profile currentUser;
    private boolean isMatched;

    private boolean hasItemMatched = false;
    private ListView lv;

    private int DISTANCE_RANGE = 10000;
    private View messageView;

    public Recommend(ArrayList<DBMaterial> dbMaterials, User_Profile user) {
        this.imgRecogMaterials = dbMaterials;
        this.currentUser = user;
        this.recommendedItems = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("diy_by_tags");
        userProfile = FirebaseDatabase.getInstance().getReference("userdata");

        this.proccess();

    }

    public Recommend setRecommendedItems(ArrayList<DIYnames> recommendedItems) {
        this.recommendedItems = recommendedItems;
        return this;
    }

    public Recommend setListView(ListView  lv){
        this.lv = lv;
        return this;
    }

    public Recommend setMessageView(View v){
        this.messageView = v;
        return this;

    }


    public Recommend setAdapter(RecommendDIYAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    private void proccess() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final DIYnames toBeRecommendedDIY = dataSnapshot.getValue(DIYnames.class);
                isMatched = false;
                int score = 0;
                if (!toBeRecommendedDIY.getUser_id().equals(currentUser.getUserID())) {
                    for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
                        for (DBMaterial irMaterial : imgRecogMaterials) {
                            DataSnapshot dbMaterialNode = postSnapshot; // Material from DB
                            String dbMaterialName = dbMaterialNode.child("name").getValue(String.class).toLowerCase();
                            String dbMaterialUnit = dbMaterialNode.child("unit").getValue(String.class);
                            long dbMaterialQuantity = dbMaterialNode.child("quantity").getValue(Long.class);
                            if (dbMaterialName.equals(irMaterial.getName())) {
                                if (irMaterial.getQuantity() <= dbMaterialQuantity) {
                                    if (dbMaterialUnit.equals(irMaterial.getUnit())) {
                                        score++;
                                        isMatched = true;
                                    }
                                }
                            }
                        }
                    }
                    if (isMatched && (!toBeRecommendedDIY.getUser_id().equals(currentUser.getUserID()))) {
                        isMatched = false;
                        final double scoreRate = ((float) score / (float) imgRecogMaterials.size()) * 100;
                        toBeRecommendedDIY.setMatchScore(score);
                        toBeRecommendedDIY.setMatchScoreRate((int) scoreRate);
                        Log.e("RecommendItem", toBeRecommendedDIY.getDiyName() + " = " + score + " = " + scoreRate);

                        userProfile.child(toBeRecommendedDIY.getUser_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                double current_lat = dataSnapshot.child("current_lat").getValue(double.class);
                                double current_long = dataSnapshot.child("current_lng").getValue(double.class);

                                Location other = new Location("other");
                                other.setLatitude(current_lat);
                                other.setLongitude(current_long);

                                Location myLocation = currentUser.getLocation();

                                double RECOMMENDATION_RATE = 0;
                                double distance;
                                double distanceRate = 0;

                                double tempScoreRate = (scoreRate * 0.5);

                                toBeRecommendedDIY.setRecommendationScore(tempScoreRate);

                                if (currentUser.getLocation() != null) {
                                    distance = other.distanceTo(myLocation);
                                    if (distance >= 0 && distance < DISTANCE_RANGE) {
                                        distanceRate = 100 - ((distance / DISTANCE_RANGE) * 100);

                                        double tempDistanceRate = (distanceRate * 0.5);
                                        Log.e("DISTANCERATE", distanceRate+" "+tempDistanceRate);

                                        RECOMMENDATION_RATE = tempDistanceRate + tempScoreRate;
                                        Log.e("RECOMMENDATION_RATE", tempDistanceRate+" "+tempScoreRate+ toBeRecommendedDIY.getDiyName());

                                        Log.e("RECOMMENDATIONRATE", RECOMMENDATION_RATE + " " + toBeRecommendedDIY.getDiyName());
                                        toBeRecommendedDIY.setRecommendationScore(RECOMMENDATION_RATE);
                                    }
                                }
                                if (toBeRecommendedDIY.getRecommendationScore() >= 60) {
                                    recommendedItems.add(toBeRecommendedDIY);
                                    if(hasItemMatched == false){
                                        hasItemMatched = true;
                                        if(lv!=null && messageView !=null){

                                            lv.setVisibility(View.VISIBLE);
                                            messageView.setVisibility(View.INVISIBLE);
                                        }

                                        // DIRI NGA PART IHIDE ANG TEXT VIEW PARA SA " NO RESULTS FOUND"

                                    }

                                    sort();
                                    if (adapter != null) {
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }else{
                        //DIRI KO MAG BUTANG IF WALA DIBA?

                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sort() {
        for (int x = 0; x < recommendedItems.size(); x++) {
            for (int y = 0; y < (recommendedItems.size() - x) - 1; y++) {
                if (recommendedItems.get(y).getRecommendationScore() < recommendedItems.get(y + 1).getRecommendationScore()) {
                    DIYnames holder = recommendedItems.get(y);
                    recommendedItems.set(y, recommendedItems.get(y + 1));
                    recommendedItems.set(y + 1, holder);
                }
            }
        }
    }

    public ArrayList<DIYnames> get() {
        return recommendedItems;
    }

    public String toString() {
        String response = "";
        for (DIYnames item : recommendedItems) {
            response += item.getDiyName() + " -- ";
        }
        return response;
    }

}
