package com.example.cvitae;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cvitae.Tools.ConexionSQLiteHelper;
import com.example.cvitae.interfaces.IFragmentsPrincipal;
import com.example.cvitae.principalfragments.Login;
import com.example.cvitae.principalfragments.Registro;

public class MainActivity extends AppCompatActivity implements IFragmentsPrincipal {

    private SectionsPagerAdapter sectionsPagerAdapter;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conexion();

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.containerVP);
        viewPager.setAdapter(sectionsPagerAdapter);

    }

    private void conexion() {
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this, "bd_cvitae", null, 1);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static Fragment newInstance(int sectionNumber) {
            Fragment fragment = null;

            switch (sectionNumber) {
                case 1:
                    fragment = new Login();
                    break;
                case 2:
                    fragment = new Registro();
            }

            return fragment;
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.register_login, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            return rootView;
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return PlaceholderFragment.newInstance(i + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


}
