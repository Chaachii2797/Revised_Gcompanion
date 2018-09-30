package cabiso.daphny.com.g_companion.Recommend;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.DIYnames;

/**
 * Created by cicctuser on 9/28/2018.
 */

public class Recommend {
    private ArrayList<DBMaterial> imgRecogMaterials;
    private ArrayList<DIYnames> recommendedItems;
    private RecommendDIYAdapter adapter;
    private DatabaseReference databaseReference;
    private String userId;
    private boolean isMatched;

    public Recommend (ArrayList<DBMaterial> dbMaterials,String userId){
        this.imgRecogMaterials = dbMaterials;
        this.userId = userId;
        this.recommendedItems = new ArrayList<>();
        this.proccess();
    }

    public Recommend setRecommendedItems(ArrayList<DIYnames> recommendedItems){
        this.recommendedItems = recommendedItems;
        return this;
    }

    public Recommend setAdapter(RecommendDIYAdapter adapter){
        this.adapter = adapter;
        return this;
    }

    private void proccess() {
        databaseReference = FirebaseDatabase.getInstance().getReference("diy_by_tags");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DIYnames toBeRecommendedDIY = dataSnapshot.getValue(DIYnames.class);
                isMatched = false;
                int score = 0;
                if (!toBeRecommendedDIY.getUser_id().equals(userId)) {
                    for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
                        for (DBMaterial irMaterial : imgRecogMaterials) {
                            DataSnapshot dbMaterialNode = postSnapshot; // Material from DB
                            String dbMaterialName = dbMaterialNode.child("name").getValue(String.class).toLowerCase();
                            String dbMaterialUnit = dbMaterialNode.child("unit").getValue(String.class);
                            long dbMaterialQuantity = dbMaterialNode.child("quantity").getValue(Long.class);
                            if (dbMaterialName.equals(irMaterial.getName())) {
                                if (irMaterial.getQuantity() >= dbMaterialQuantity) {
                                    if (dbMaterialUnit.equals(irMaterial.getUnit())) {
                                        score++;
                                        isMatched = true;
                                    }
                                }
                            }
                        }
                    }
                }
                if (isMatched) {
                    isMatched = false;
                    double scoreRate = ((float)score/(float)imgRecogMaterials.size())*100;
                    toBeRecommendedDIY.setMatchScore(score);
                    toBeRecommendedDIY.setMatchScoreRate((int) scoreRate);
                    Log.e("RecommendItem",toBeRecommendedDIY.getDiyName()+" = "+score+" = "+scoreRate);
                    if(scoreRate >= 60) {
                        Log.e("AccRecommendItem",toBeRecommendedDIY.getDiyName()+" = "+score+" = "+scoreRate);
                        recommendedItems.add(toBeRecommendedDIY);
                        sort();
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
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

    public void sort(){
        for(int x = 0; x < recommendedItems.size(); x++){
            for(int y = 0; y < (recommendedItems.size()-x)-1; y++){
                if(recommendedItems.get(y).getMatchScoreRate() < recommendedItems.get(y+1).getMatchScoreRate()){
                    DIYnames holder = recommendedItems.get(y);
                    recommendedItems.set(y,recommendedItems.get(y+1));
                    recommendedItems.set(y+1,holder);
                }
            }
        }
    }

    public ArrayList<DIYnames> get(){
        return recommendedItems;
    }

    public String toString(){
        String response = "";
        for(DIYnames item : recommendedItems){
            response+= item.getDiyName()+" -- ";
        }
        return response;
    }

}
