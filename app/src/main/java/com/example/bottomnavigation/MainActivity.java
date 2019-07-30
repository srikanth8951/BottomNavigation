package com.example.bottomnavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static com.example.bottomnavigation.R.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    ListView myListView;
    String[] items;
    TextView notFound;
    MediaPlayer player;
    int i = 0,j=0;
    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        myListView =  findViewById(id.songs);
        runtimePermissio();

        drawer = findViewById(id.drawer_layout);

        Toolbar toolbar=findViewById(id.toolbar);
        NavigationView navigationView=findViewById(id.nav_view);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                string.navigation_drawer_open, string.navigation_drawer_close );
        drawer.addDrawerListener(toggle);
        toggle.syncState();




    }

    public void runtimePermissio(){

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        display();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findsong(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        try {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(findsong(singleFile));
                } else {
                    if (singleFile.getName().endsWith(".mp3") ||
                            singleFile.getName().endsWith(".wav")) {
                        arrayList.add(singleFile);
//                    notFound.setVisibility(View.GONE);
                    }
                }
            }
        }catch (Exception e){

        }
        return arrayList;
    }

    void display(){
        final ArrayList<File> mysongs = findsong(Environment.getExternalStorageDirectory());
        final ArrayList<String> mysongs1 = new ArrayList<String>();
        items = new String[mysongs.size()];

        Field[] field = R.raw.class.getFields();
        for(j=0;j<field.length;j++){
            mysongs1.add(field[j].getName());
        }

        for (i=0;i<mysongs.size();i++){
            items[i] = mysongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }
        if(i==0){
            notFound = (TextView) findViewById(id.filesNotFound);
            notFound.setText("No files found");
        }
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mysongs1);
        myListView.setAdapter(myAdapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//                String songName = myListView.getItemAtPosition(i).toString();
                if(player != null ){
                    player.release();
                }
                int id = getResources().getIdentifier(mysongs1.get(0),"raw",getOpPackageName());
                startActivity(new Intent(getApplicationContext(),PlayerActivity.class)
                        .putExtra("songs",mysongs1).putExtra("songname", "Haalu Jenu")
                        .putExtra("pos",id));
            }
        });
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case id.nav_adhyaya:
                getSupportFragmentManager().beginTransaction().replace(id.fragment_container,
                        new Chapter1()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }

    }




}
