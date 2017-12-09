package com.github.bysky.charmplayer;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends NavBarActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    private AdjustDrawButton buttonLocalMusic,buttonScan,buttonFond,buttonMusicList;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =  findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //初始化
        initialUI();
    }

    @Override
    protected void initialUI() {
        int iconWH = (int)(getResources().getDisplayMetrics().widthPixels/4*0.618);
        navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        //
        buttonLocalMusic = findViewById(R.id.button_entrance_local_music);    buttonLocalMusic.setOnClickListener(this);
        buttonMusicList = findViewById(R.id.button_entrance_music_list);       buttonMusicList.setOnClickListener(this);
        buttonScan = findViewById(R.id.button_entrance_scan);                  buttonScan.setOnClickListener(this);
        buttonFond = findViewById(R.id.button_entrance_i_fond);                buttonFond.setOnClickListener(this);
        //
        buttonLocalMusic.setBackgroundSize(1,iconWH,iconWH);
        buttonMusicList.setBackgroundSize(1,iconWH,iconWH);
        buttonFond.setBackgroundSize(1,iconWH,iconWH);
        buttonScan.setBackgroundSize(1,iconWH,iconWH);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        //传入父控件
        super.onClick(v);
        switch(v.getId()){
            case R.id.button_entrance_local_music:
                intent = new Intent(this,LocalMusicActivity.class);
                startActivity(intent);
                break;
            case R.id.button_entrance_music_list:

                break;
            case R.id.button_entrance_i_fond:

                break;
            case R.id.button_entrance_scan:
                intent = new Intent(this,ScanMusicActivity.class);
                startActivity(intent);
                break;
        }
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) { //小菜单栏
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //去除选择
        navView.setCheckedItem(NavigationView.NO_ID);
        int id = item.getItemId();

        if (id == R.id.nav_skin) {
            Toast.makeText(this,"Skin~~", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_clock) {

        } else if (id == R.id.nav_settings) {

        } else if(id == R.id.nav_sound){

        }else  if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
