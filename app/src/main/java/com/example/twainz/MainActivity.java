package com.example.twainz;

import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.MapView;


public class MainActivity extends AppCompatActivity{

    private SectionsPagerAdapter mSectionsPagerAdapter;
//    private ViewPager mViewPager;
    private CustomViewPager mViewPager;

    final int[] ICONS = new int[]{
            R.drawable.journey,
            R.drawable.station,
            R.drawable.favourite,
            R.drawable.twitter,
            R.drawable.nearme};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //mViewPager.setId(R.id.container);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(ICONS[0]);
        tabLayout.getTabAt(1).setIcon(ICONS[1]);
        tabLayout.getTabAt(2).setIcon(ICONS[2]);
        tabLayout.getTabAt(3).setIcon(ICONS[3]);
        tabLayout.getTabAt(4).setIcon(ICONS[4]);
        mViewPager.setCurrentItem(2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

 /*   @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();

        } else {
            getFragmentManager().popBackStack();
        }

    }
*/
    @Override
    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();
       /* for (Fragment frag : fm.getFragments()) {
            if (frag.isVisible()) { //If this is the current fragment

                FragmentManager childFm = frag.getChildFragmentManager();

                if (childFm.getBackStackEntryCount() > 0) { //Check if it has any child fragments on its stack
                    if (childFm.getBackStackEntryCount() > 1) {
                        Fragment f = childFm.getFragments().get(childFm.getBackStackEntryCount() - 2);
                        f.setUserVisibleHint(true);
                    }
                    else
                        frag.setUserVisibleHint(true);

                    childFm.popBackStack();

                    return;
                }

            }
        }*/

        int index = (fm.getBackStackEntryCount() > 1) ? fm.getBackStackEntryCount() : 0;
        Fragment frag = fm.getFragments().get(index);
        frag.setUserVisibleHint(true);

        super.onBackPressed();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    return new JourneyPlanner();
                case 1:
                    return new stationList();
                case 2:
                    return new Favourites();
                case 3:
                    return new Twitter();
                case 4:
                    return new MapsActivity();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                //removed the names from these buttons since the icons should be descriptive enough
                case 0:
                    return "";//"Journey";
                case 1:
                    return "";//"Stations";
                case 2:
                    return "";//"Favourites";
                case 3:
                    return "";//"Twitter";
                case 4:
                    return "";//"Near Me";
                default:
                    return null;
            }
        }

    }
}
