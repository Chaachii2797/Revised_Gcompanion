package cabiso.daphny.com.g_companion;

import android.app.SearchManager;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MarketPlaceFragment.OnListFragmentInteractionListener, CommunityFragment.OnListFragmentInteractionListener{

    private static final String TAG = MainActivity.class.getSimpleName();


    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private String mUsername;
    private String mPhotoUrl;
    private TextView name;
    private String userID;

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private TabLayout mTabLayout;

    //private Bottle_Recommend lv;

    private ImageView image;
    // private final ClarifaiClient clarifaiClient = new ClarifaiBuilder("{b7aa33dc206c40a4b9cffc09a2e72a9d}").buildSync();

    final ClarifaiClient client = new ClarifaiBuilder("b7aa33dc206c40a4b9cffc09a2e72a9d").buildSync();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        image = (ImageView) findViewById(R.id.diy_item_icon);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setViewPager();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);


        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView txtProfileName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.mUser);
        txtProfileName.setText("Daphny Cabiso");

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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // use this method when query submitted
                Toast.makeText(getApplicationContext(),"Search pls", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // use this method for auto complete search process


                return false;
            }
        });
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

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
            case R.id.nav_bm:
                Intent bm = new Intent(this,Bookmark_Activity.class);
                startActivity(bm);
                break;
            case R.id.nav_profile:
                Intent intent2=new Intent(this,MyProfileActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_chat:
                Intent chat = new Intent(MainActivity.this,Messaging.class);
                startActivity(chat);
                break;
            case R.id.nav_diy:
                Intent diy = new Intent(MainActivity.this, MyDiys.class);
                startActivity(diy);
                break;
            case R.id.nav_item:
                Intent item = new Intent(MainActivity.this,Item_Activity.class);
                startActivity(item);
                break;
            case R.id.nav_sold:
                Intent sold = new Intent(MainActivity.this,Sold_Activity.class);
                startActivity(sold);
                break;
            case R.id.nav_pending:
                Intent pending = new Intent(MainActivity.this,Pending_Activity.class);
                startActivity(pending);
                break;
            case R.id.nav_calendar:
                Intent calendar = new Intent(MainActivity.this,Calendar.class);
                startActivity(calendar);
                break;
            case R.id.nav_wishlist:
                Intent wishlist = new Intent(MainActivity.this,Wishlists.class);
                startActivity(wishlist);
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
                FirebaseAuth.getInstance().signOut();
                Intent intent1=new Intent(this,Login.class);
                startActivity(intent1);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onListFragmentInteraction(DatabaseReference ref) {
        Intent intent = new Intent(this, ProductDetailViewActivity.class);
        intent.putExtra("Product reference", ref.toString());
        startActivity(intent);
    }


    @Override
    public void onListFragmentInteractionListener(DatabaseReference ref) {
        Intent intent = new Intent(this, DIYDetailViewActivity.class);
        intent.putExtra("Community Ref", ref.toString());
        startActivity(intent);
    }

}