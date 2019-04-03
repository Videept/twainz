package com.example.twainz;

import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.Stack;


public class MainActivity extends AppCompatActivity{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public static Stack<String>[] appTitles;
    public int currentPage;


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
        appTitles = new Stack[5]; //Initialise the title stacks

        for (int i = 0; i < 5; i++)
            appTitles[i] = new Stack();

        //Add the titles to the stacks
        appTitles[0].push("Journey Planner");
        appTitles[1].push("Stations");
        appTitles[2].push("Favourites");
        appTitles[3].push("Twitter");
        appTitles[4].push("Map");

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

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                ActionBar actionBar = getsupportactionbar();
                currentPage = i;

                if (actionBar != null)
                    actionBar.setTitle(appTitles[i].peek());
            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

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

    public ActionBar getsupportactionbar() {
        ActionBar mActionBar = getSupportActionBar();
        return mActionBar;
    }
    public void setTitle(String title){
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setTitle(title);
    }


    @Override
    public void onBackPressed() {

        BackPressListener currentFragment = (BackPressListener) mSectionsPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());

        if (currentFragment != null) {
            currentFragment.onBackPressed();

            if (appTitles[currentPage].size() > 1)
               appTitles[currentPage].pop();

            setTitle(appTitles[currentPage].peek());

        }

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        @Override
        public Fragment getItem(int position) {
            FragmentRoot root = new FragmentRoot();
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:

                    return new FragmentJourneyPlanner();
                case 1:

                    return new FragmentStationList();
                case 2:

                    return new FragmentFavourites();
                case 3:

                    return new FragmentTwitter();
                case 4:

                    return new FragmentMaps();
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
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

    }
}
