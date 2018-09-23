package cabiso.daphny.com.g_companion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.BuyingProcess.ForMeetUpActivity;
import cabiso.daphny.com.g_companion.BuyingProcess.Pending_Activity;
import cabiso.daphny.com.g_companion.BuyingProcess.Sold_Activity;
import cabiso.daphny.com.g_companion.GCAdmin.AdminActivity;
import cabiso.daphny.com.g_companion.GCAdmin.LogsOfAllTransactionsActivity;
import cabiso.daphny.com.g_companion.InstantMessaging.ui.activities.ChatSplashActivity;
import cabiso.daphny.com.g_companion.MainDIYS.DiysFragment;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.Promo.PromoFragment;
import cabiso.daphny.com.g_companion.Search.SearchActivity;
import cabiso.daphny.com.g_companion.UserSalesReport.SalesReport;
import cabiso.daphny.com.g_companion.YouItemsFragment.OnListFragmentInteractionListener;
import cabiso.daphny.com.g_companion.notifications.PushNotification;
import cabiso.daphny.com.g_companion.notifications.VolleyApp;
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CommunityFragment.OnListFragmentInteractionListener,
        OnListFragmentInteractionListener, PromoFragment.OnListFragmentInteractionListener,
        DiysFragment.OnListFragmentInteractionListener{


    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference user_reference;
    private DatabaseReference databaseReference;
    private DatabaseReference itemReference, ratingReference;

    private User_Profile loggedInUser = null;
    private String mUsername;
    private String mPhotoUrl;
    private TextView name;
    private String userID;
    private String txt_logout;

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private ArrayList<User_Profile> list_user_profile;
    private TabLayout mTabLayout;
    private String searchString;
    private ImageView image;
    // private final ClarifaiClient clarifaiClient = new ClarifaiBuilder("{b7aa33dc206c40a4b9cffc09a2e72a9d}").buildSync();

    final ClarifaiClient client = new ClarifaiBuilder("b7aa33dc206c40a4b9cffc09a2e72a9d").buildSync();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        VolleyApp volleyApp = new VolleyApp();
        this.searchString = "";

        itemReference =  FirebaseDatabase.getInstance().getReference("diy_by_tags");
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userID = mFirebaseUser.getUid();

        final String name = mFirebaseUser.getDisplayName();
        final String uid = mFirebaseUser.getUid();

        image = (ImageView) findViewById(R.id.diy_item_icons);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setViewPager();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.syncState();
        drawer.setDrawerListener(toggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final TextView txtAddress = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_Address);
        final TextView txtProfileName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_userName);
        final CircleImageView txtProfileImg = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.user_prof_pic);
        final RatingBar ratingBar = (RatingBar) navigationView.getHeaderView(0).findViewById(R.id.ratingBar);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user_reference = databaseReference.child("userdata");
        user_reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loggedInUser = dataSnapshot.getValue(User_Profile.class);
                mUsername = loggedInUser.getF_name()+" "+loggedInUser.getL_name();
                txtProfileName.setText(mUsername);
                txtAddress.setText(loggedInUser.getAddress());

                Log.e("userRole", loggedInUser.getRole());
                if (loggedInUser.getRole().equalsIgnoreCase("user")){
                    //invisible admin nav item
                    hideItem();
                }
                Glide.with(MainActivity.this)
                        .load(loggedInUser.getUserProfileUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter().crossFade()
                        .into(txtProfileImg);

                Log.e("userProfff", "" + loggedInUser.getUserProfileUrl());

                ratingBar.setRating(loggedInUser.getUserRating());
                Log.e("userRate", String.valueOf(loggedInUser.getUserRating()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Notification
        user_reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                if(loggedInUser!=null){
                    PushNotification pushNotification = new PushNotification(getApplicationContext());
                    pushNotification.title("Notification")
                            .message(loggedInUser.getF_name()+" "+loggedInUser.getL_name()+" is now active!")
                            .accessToken(user_profile.getAccess_token())
                            .send();
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

    private void getCurrentUser(){
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mFirebaseUser!=null){
            String name = mFirebaseUser.getDisplayName();
            String email = mFirebaseUser.getEmail();
            Uri photoUrl = mFirebaseUser.getPhotoUrl();
            String uid = mFirebaseUser.getUid();
        }
    }

    private void setViewPager() {

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());


        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.getCurrentItem();

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout = (TabLayout) findViewById(R.id.tab);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                // use this method when query submitted
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                intent.putExtra("searchString",query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // use this method for auto complete search process
                Toast.makeText(getApplicationContext(),"Entered: "+newText, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        display(item.getItemId());
        return true;
    }
    public void display(int itemID){
        android.support.v4.app.FragmentTransaction ft;
        switch (itemID){

            case R.id.nav_admin:
                Intent admin = new Intent(this, AdminActivity.class);
                startActivity(admin);
                break;

            case R.id.nav_logs:
                Intent logs = new Intent(this, LogsOfAllTransactionsActivity.class);
                startActivity(logs);
                break;

            case R.id.nav_profile:
                Intent intent2=new Intent(this,MyProfileActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_bookmark:
                Intent wishlist = new Intent(MainActivity.this,Wishlists.class);
                startActivity(wishlist);
                break;
            case R.id.nav_chat:
                Intent chat = new Intent(MainActivity.this, ChatSplashActivity.class);
                startActivity(chat);
                break;
            case R.id.nav_pending:
                Intent pending = new Intent(MainActivity.this,Pending_Activity.class);
                startActivity(pending);
                break;
            case R.id.nav_meetup:
                Intent meetup = new Intent(MainActivity.this,ForMeetUpActivity.class);
                startActivity(meetup);
                break;
            case R.id.nav_sold:
                Intent sold = new Intent(MainActivity.this,Sold_Activity.class);
                startActivity(sold);
                break;
            case R.id.nav_calendar:
                Intent calendar = new Intent(MainActivity.this,CalendarActivity.class);
                startActivity(calendar);
                break;
            case R.id.nav_report:
                Intent sales = new Intent(MainActivity.this,SalesReport.class);
                startActivity(sales);
                break;
            case R.id.nav_about:
                Intent about = new Intent(MainActivity.this,About.class);
                startActivity(about);
                break;
            case R.id.nav_logout:
                if(mFirebaseUser!=null){
                    mFirebaseAuth.getInstance().signOut();
                    Intent logout = new Intent(MainActivity.this,Login.class);
                    startActivity(logout);
                }else{
                    Toast.makeText(MainActivity.this, "Already signed out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                }
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    @Override
    public void onListFragmentInteractionListener(DatabaseReference ref) {
        Intent intent_commu = new Intent(this, DIYDetailViewActivity.class);
        intent_commu.putExtra("Community Ref", ref.toString());
        startActivity(intent_commu);
    }

    private void hideItem()
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_admin).setVisible(false);
        nav_Menu.findItem(R.id.nav_logs).setVisible(false);

    }


}