package com.motivus.ece.motivus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
    private final long LOCATION_REFRESH_TIME = 10000;
    private final float LOCATION_REFRESH_DISTANCE = 0;
    private final float LOCATION_THRESHOLD_DISTANCE = 100;
    private LocationManager mLocationManager;
    private Database db;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up the database
        db = Database.getInstance(this);

        //Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //Constantly monitoring the GPS location
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);

        //Set up the ViewPager with the sections adapter.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
            public void onPageScrollStateChanged(int state) {
            }
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            ArrayList<Appointment> appointments = Database.getInstance(getApplicationContext()).getAllAppointments();
            for(int i = 0; i <  appointments.size(); i++) {
                double diffDistance = GoogleMaps.checkDistance(latitude, longitude, appointments.get(i).latitude, appointments.get(i).longitude);
                if (diffDistance <= LOCATION_THRESHOLD_DISTANCE) {
                    appointments.get(i).check = true;
                    Toast.makeText(getApplicationContext(),
                            "\"" + appointments.get(i).title + "\" appointment DONE!" , Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
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
            switch(position) {
                case 0:
                    return AppointmentFragment.newInstance(position + 1, db);
                case 1:
                    return CalendarFragment.newInstance(position + 1);
                case 2:
                    return ReportFragment.newInstance(position + 1);
                default:
                    return AppointmentFragment.newInstance(position + 1, db);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public int getItemPosition(Object object) {
            if(object instanceof  AppointmentFragment ) {
                AppointmentFragment f = (AppointmentFragment) object;
                if (f != null) {
                    f.update();
                }
            }
            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    public static class AppointmentFragment extends ListFragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public Database db;
        public List<Appointment> appointmentList = new ArrayList<Appointment>();
        public AppointmentAdapter mAdapter;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static AppointmentFragment newInstance(int sectionNumber, Database database) {
            AppointmentFragment fragment = new AppointmentFragment();
            fragment.db = database;
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public AppointmentFragment() {
        }

        public void update() {
            appointmentList = db.getAllAppointments();
            mAdapter = new AppointmentAdapter(getActivity(), appointmentList);
            setListAdapter(mAdapter);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_appointment, container, false);

            //Get all the appointment and put them into
            appointmentList = db.getAllAppointments();
            mAdapter = new AppointmentAdapter(getActivity(), appointmentList);
            setListAdapter(mAdapter);

            //Add new appointment button
            Button newAppointment = (Button) rootView.findViewById(R.id.button_newappointment);
            newAppointment.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent newActivity = new Intent(v.getContext(), NewAppointment.class);
                            startActivity(newActivity);
                        }
                    }
            );

            return rootView;
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Appointment appointment = (Appointment)l.getItemAtPosition(position);
            DetailFragment detailFragment = new DetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("appointment", appointment);
            detailFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction =
                    getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, detailFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    public static class CalendarFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static CalendarFragment newInstance(int sectionNumber) {
            CalendarFragment fragment = new CalendarFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public CalendarFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
            return rootView;
        }
    }

    public static class ReportFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ReportFragment newInstance(int sectionNumber) {
            ReportFragment fragment = new ReportFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ReportFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_report, container, false);
            return rootView;
        }
    }

    public static class DetailFragment extends Fragment {
        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Bundle args = getArguments();
            Appointment appointment  = args.getParcelable("appointment");

            TextView editText_name = (TextView)(rootView.findViewById(R.id.textView_title));
            editText_name.setText(appointment.title, TextView.BufferType.EDITABLE);

            TextView editText_detail = (TextView)(rootView.findViewById(R.id.textView_detail));
            editText_detail.setText(appointment.detail, TextView.BufferType.EDITABLE);

            TextView editText_latitude = (TextView)(rootView.findViewById(R.id.textView_latitude));
            editText_latitude.setText("" + appointment.latitude, TextView.BufferType.EDITABLE);

            TextView editText_longitude = (TextView)(rootView.findViewById(R.id.textView_longitude));
            editText_longitude.setText("" + appointment.longitude, TextView.BufferType.EDITABLE);
            /*
            ImageView imageView_pic = (ImageView)(rootView.findViewById(R.id.imageView_pic));
            byte[] imageByteArray = appointment.pic;
            ByteArrayInputStream imageStream = new ByteArrayInputStream(imageByteArray);
            Bitmap image = BitmapFactory.decodeStream(imageStream);
            imageView_pic.setImageBitmap(image);
            Button button_back = (Button) rootView.findViewById(R.id.button_back);
            button_back.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
            */

            return rootView;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }
}