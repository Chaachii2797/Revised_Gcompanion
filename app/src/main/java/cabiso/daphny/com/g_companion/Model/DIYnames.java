package cabiso.daphny.com.g_companion.Model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class DIYnames extends SellingDIY implements Comparable<DIYnames>, Serializable{

    private static DatabaseReference databaseReference;
    public String diyName;
    public String diyUrl;
    public String user_id;
    public String productID;
    public String identity;
    public Float bookmarks;
    public Float likes;
    public String loggedInUser;
    public int matchScore;
    public int matchScoreRate;
    public int materialMatches;
    public int totalMaterialItems;
    public int incomeCount;
    public int expenseCount;
    private static DIYnames diyInfo = null;
    private ArrayList<DBMaterial> dbMaterials = new ArrayList<>();
    private String diyVideo;

    public DIYnames(String diyName, String diyUrl, String user_id, String productID, String identity,
                    Float bookmarks, Float likes, String loggedInUser, String diyVideo){
        this.diyName = diyName;
        this.diyUrl = diyUrl;
        this.user_id = user_id;
        this.productID = productID;
        this.identity = identity;
        this.bookmarks = bookmarks;
        this.likes = likes;
        this.loggedInUser = loggedInUser;
        this.dbMaterials = new ArrayList<>();
        this.matchScore = 0;
        this.matchScoreRate = 0;
        this.totalMaterialItems = 0;
        this.diyVideo = diyVideo;
    }

    public DIYnames() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("diy_by_tags");
    }

    public DIYnames(String s, String s1) {
    }

    public void copy(DIYnames diYnames){
        if(diYnames!=null){
            this.setDiyName(diYnames.getDiyName());
            this.setDiyUrl(diYnames.getDiyUrl());
            this.setUser_id(diYnames.getUser_id());
            this.setProductID(diYnames.getProductID());
            this.setIdentity(diYnames.getIdentity());
            this.setBookmarks(diYnames.getBookmarks());
        }
    }

    public void setMatchScoreRate(int rate){
        this.matchScoreRate = rate;
    }

    public double getMatchScoreRate(){
        return this.matchScoreRate;
    }

    public ArrayList<DBMaterial> getMaterialMatches() {
        return dbMaterials;
    }

    public DIYnames setMaterialMatches(int materialMatches) {
        this.materialMatches = materialMatches;
        return this;
    }

    public String getDiyName() {
        return diyName;
    }

    public DIYnames setDiyName(String diyName) {
        this.diyName = diyName;
        return this;
    }

    public String getDiyUrl() {
        return diyUrl;
    }

    public DIYnames setDiyUrl(String diyUrl) {
        this.diyUrl = diyUrl;
        return this;
    }
    public void incrementScore(){
        ++this.matchScore;
    }

    public void incrementTotalMaterial(long s){ this.totalMaterialItems = (int) s; }

    public int getTotalMaterial(){ return totalMaterialItems; }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public String getUser_id() {
        return user_id;
    }

    public DIYnames setUser_id(String user_id) {
        this.user_id = user_id;
        return this;
    }

    public String getDiyVideo() {
        return diyVideo;
    }

    public void setDiyVideo(String diyVideo) {
        this.diyVideo = diyVideo;
    }

    public String getProductID() {
        return productID;
    }

    public DIYnames setProductID(String productID) {
        this.productID = productID;
        return this;
    }

    public String getIdentity() {
        return identity;
    }

    public DIYnames setIdentity(String identity) {
        this.identity = identity;
        return this;
    }

    public Float getBookmarks() {
        return bookmarks;
    }

    public DIYnames setBookmarks(Float bookmarks) {
        this.bookmarks = bookmarks;
        return this;
    }

    public Float getLikes() {
        return likes;
    }

    public DIYnames setLikes(Float likes) {
        this.likes = likes;
        return this;
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }

    public DIYnames setLoggedInUser(String loggedInUser) {
        this.loggedInUser = loggedInUser;
        return this;
    }

    public int getIncomeCount() {
        return incomeCount;
    }

    public void setIncomeCount(int incomeCount) {
        this.incomeCount = incomeCount;
    }

    public int getExpenseCount() {
        return expenseCount;
    }

    public void setExpenseCount(int expenseCount) {
        this.expenseCount = expenseCount;
    }

    public DIYnames addDbMaterial(DBMaterial dbMaterial){
        this.dbMaterials.add(dbMaterial);
        return this;
    }

    public int getDbMaterialCount(){
        return this.dbMaterials.size();
    }

    @Override
    public int compareTo(@NonNull DIYnames o) {
        if(bookmarks.floatValue() > o.bookmarks.floatValue() && likes.floatValue() > o.likes.floatValue()) {
            return 1;
        }else if(bookmarks.floatValue() < o.bookmarks.floatValue() && likes.floatValue() < o.likes.floatValue()){
            return -1;
        }else{
            return 0;
        }
    }

    public static void getDetails(DIYnames ref,String diyID){

        databaseReference = FirebaseDatabase.getInstance().getReference("diy_by_tags").child(diyID);
        if(diyID.length() > 0) {
            if(ref!=null) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot itemSnapshot) {
                        diyInfo = itemSnapshot.getValue(DIYnames.class);
                        Log.e("diyTestDetailsOBJECT1", diyInfo.toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                ref = diyInfo;
            }
        }
    }
//
//    @Override
//    public String toString() {
//        return "product name : " + diyName;
//    }
}
