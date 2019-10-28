package com.example.naber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AnaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabsAdapter tabsAdapter;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    public void init(){

        toolbar = findViewById(R.id.AnaActivity_ActionBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        viewPager = findViewById(R.id.AnaActivity_ViewPager);
        tabsAdapter = new TabsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsAdapter);

        tabLayout = findViewById(R.id.Anaactivity_Tabbar);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana);

        init();
    }

    @Override
    protected void onStart() {

        if (firebaseUser == null){
            Intent ıntent = new Intent(AnaActivity.this,MainActivity.class);
            startActivity(ıntent);
            finish();
        }

        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_items,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.Quit){

            auth.signOut();
            Intent ıntent = new Intent(AnaActivity.this,MainActivity.class);
            startActivity(ıntent);
            finish();
        }

        return true;
    }
}
