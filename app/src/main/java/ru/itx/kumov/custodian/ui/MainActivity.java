package ru.itx.kumov.custodian.ui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ru.itx.kumov.custodian.R;
import ru.itx.kumov.custodian.ui.fragment.AddFrag;
import ru.itx.kumov.custodian.ui.fragment.AnketaFrag;
import ru.itx.kumov.custodian.ui.fragment.ExportFrag;
import ru.itx.kumov.custodian.ui.fragment.GraphFrag;
import ru.itx.kumov.custodian.ui.fragment.ListFra;
import ru.itx.kumov.custodian.ui.fragment.InfoFrag;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    NavigationView nvDrawer;
    ActionBarDrawerToggle drawerToggle;
    public LinearLayout chartContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sPref = getSharedPreferences(getString(R.string.shared_preference), MODE_PRIVATE);
        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        chartContainer = (LinearLayout) findViewById(R.id.chart);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
        FrameLayout fm = (FrameLayout) findViewById(R.id.flContent);
        ImageView img = new ImageView(this);
        img.setImageResource(R.drawable.caduceus);
        fm.addView(img);
    }
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
   }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        finish();
        return;
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.m_anketa:
                fragmentClass = AnketaFrag.class;
                break;
            case R.id.m_add:
                fragmentClass = AddFrag.class;
                break;
            case R.id.m_graph:
                fragmentClass = GraphFrag.class;
                break;
            case R.id.m_list:
                fragmentClass = ListFra.class;
                break;
            case R.id.m_export:
                fragmentClass = ExportFrag.class;
                break;
            case R.id.m_information:
                fragmentClass = InfoFrag.class;
                break;
            case R.id.m_exit:
                finish();
                return;
            default:
                fragmentClass = AddFrag.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }


}
