package com.teammoviealley.moviealleyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.teammoviealley.moviealleyapp.fragment.FragmentAccount;
import com.teammoviealley.moviealleyapp.fragment.FragmentHomeMovie;
import com.teammoviealley.moviealleyapp.fragment.FragmentSearchMovie;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        setContentView(R.layout.activity_home);
        setUpNavView();
    }


    void setUpNavView(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    Fragment selectedFragment = null;
                    switch(item.getItemId()){
                        case R.id.fragmentHomeMovie:
                            selectedFragment = new FragmentHomeMovie();
                            break;
                        case R.id.fragmentSearchMovie:
                            selectedFragment = new FragmentSearchMovie();
                            break;
                        case R.id.fragmentAccount:
                            selectedFragment = new FragmentAccount(intent.getStringExtra("DISPLAY NAME"),
                                    intent.getStringExtra("EMAIL"),
                                    intent.getStringExtra("AVATAR"));
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                            , selectedFragment).commit();

                    return true;
                }
            };



}