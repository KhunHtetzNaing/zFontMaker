package com.htetznaing.samfontmaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v4.os.EnvironmentCompat;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.nononsenseapps.filepicker.Utils;
import com.stardust.autojs.apkbuilder.ApkBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Fucker fucker = new Fucker();
    String path = "/sdcard/Android/data/com.htetznaing.samfontmaker/";
    String tempPath = "/sdcard/Android/data/com.htetznaing.samfontmaker/temp/assets/";
    String data = "htetz.zip";
    String fontPath,ttfName,fontName,outPath;
    ConstraintLayout mainLayout;

    ListView listView;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> fileSize = new ArrayList<>();

    private File root;
    private ArrayList<File> fileList = new ArrayList<File>();
    myAdapter adapter;
    ProgressDialog progressDialog,proGress;
    TextView textView;

    AdRequest adRequest;
    AdView banner;
    InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("zFont Maker");
        setSupportActionBar(toolbar);

        mainLayout = findViewById(R.id.mainLayout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareApp();
            }
        });

        outPath = "LOL";
        textView = findViewById(R.id.textLOL);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iOpen = new Intent(Intent.ACTION_VIEW);
                iOpen.setData(Uri.parse("http://bit.ly/2CWauMx"));
                startActivity(iOpen);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait!!");
        progressDialog.setMessage("Working....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        proGress = new ProgressDialog(this);
        proGress.setMessage("Listing ttf font files..");
        proGress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        final File file = new File(path);
        if (!file.exists()){
            file.mkdir();
        }

        listView = (ListView) findViewById(R.id.listView);
        new listOn().execute();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Attention!");
                b.setMessage("Do you want to Create ?");
                final int o = i;
                b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startWork(fileList.get(o).toString());
                    }
                });
                b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showAD();
                    }
                });
                AlertDialog d = b.create();
                d.show();
            }
        });

        adRequest = new AdRequest.Builder().build();
        banner = findViewById(R.id.adView);
        banner.loadAd(adRequest);
        banner.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                setDefFull();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                setDefFull();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                setListFull();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                setListFull();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                setListFull();
            }
        });

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-1325188641119577/6129071537");
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                loadAD();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                loadAD();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                loadAD();
            }
        });
    }

    public void loadAD(){
        if (!interstitialAd.isLoaded()){
            interstitialAd.loadAd(adRequest);
        }
    }

    public void showAD(){
        if (interstitialAd.isLoaded()){
            interstitialAd.show();
        }else {
            interstitialAd.loadAd(adRequest);
        }
    }

    public void setListFull(){
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = mainLayout.getHeight();
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void setDefFull(){
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = mainLayout.getHeight()-55;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void shareApp() {
        Intent iShare = new Intent(Intent.ACTION_SEND);
        iShare.setType("text/plain");
        iShare.putExtra(Intent.EXTRA_TEXT,"Font Style Maker For Samsung(zFont Maker)\n" +
                "support all samsung phones version 5.0/ 6.0/ 7.0/ 8.0\n" +
                "You can convert any ttf font file to zFont APK with this app.\n" +
                "Download free here : http://bit.ly/2CWauMx\n" +
                "#zFontMaker #SamsungFontMaker");
        startActivity(Intent.createChooser(iShare,"zFont Maaker"));
    }

    class listOn extends AsyncTask<String,String,String>{

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                proGress.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                ArrayList<String> path = new ArrayList<>();
                try {
                    if (getExternalStorageDirectories().length>0){
                        String lol = getExternalStorageDirectories()[0];
                        if (lol.isEmpty()&&!lol.equals(null)){
                        }else{
                            path.add(lol);
                        }}
                }catch (Exception e){
                }

                path.add(Environment.getExternalStorageDirectory().getAbsolutePath());

                //getting SDcard root path
                for (int i =0;i<path.size();i++){
                    root = new File(path.get(i));
                    fileList = getfile(root);
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (fileList.size() <1){
                    textView.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }else {
                    textView.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    for (int i = 0; i < fileList.size(); i++) {
                        fileSize.add(getSize(fileList.get(i)));
                        arrayList.add(fileList.get(i).getName());
                        adapter = new myAdapter(MainActivity.this, arrayList, fileSize);
                        listView.setAdapter(adapter);
                    }
                }
                proGress.dismiss();
            }
        }

    public ArrayList<File> getfile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    getfile(listFile[i]);

                } else {
                    if (listFile[i].getName().endsWith(".ttf")
                            || listFile[i].getName().endsWith(".TTF"))
                    {
                        fileList.add(listFile[i]);
                    }
                }

            }
        }
        return fileList;
    }

    public String getSize(File file){
        float sizeInBytes = file.length();
        float sizeInMb = sizeInBytes / (1024 * 1024);

        String size = String.valueOf(sizeInMb);
        size = size.substring(0,4)+" MB";
        Log.d("FileSize",size);
        return size;
    }

    public String[] getExternalStorageDirectories() {
        String LOG_TAG = "SDCARD";

        List<String> results = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Method 1 for KitKat & above
            File[] externalDirs = getExternalFilesDirs(null);

            for (File file : externalDirs) {
                String path = file.getPath().split("/Android")[0];

                boolean addPath = false;

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    addPath = Environment.isExternalStorageRemovable(file);
                }
                else{
                    addPath = Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(file));
                }

                if(addPath){
                    results.add(path);
                }
            }
        }

        if(results.isEmpty()) { //Method 2 for all versions
            // better variation of: http://stackoverflow.com/a/40123073/5002496
            String output = "";
            try {
                final Process process = new ProcessBuilder().command("mount | grep /dev/block/vold")
                        .redirectErrorStream(true).start();
                process.waitFor();
                final InputStream is = process.getInputStream();
                final byte[] buffer = new byte[1024];
                while (is.read(buffer) != -1) {
                    output = output + new String(buffer);
                }
                is.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            if(!output.trim().isEmpty()) {
                String devicePoints[] = output.split("\n");
                for(String voldPoint: devicePoints) {
                    results.add(voldPoint.split(" ")[2]);
                }
            }
        }

        //Below few lines is to remove paths which may not be external memory card, like OTG (feel free to comment them out)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).toLowerCase().matches(".*[0-9a-f]{4}[-][0-9a-f]{4}")) {
                    Log.d(LOG_TAG, results.get(i) + " might not be extSDcard");
                    results.remove(i--);
                }
            }
        } else {
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).toLowerCase().contains("ext") && !results.get(i).toLowerCase().contains("sdcard")) {
                    Log.d(LOG_TAG, results.get(i)+" might not be extSDcard");
                    results.remove(i--);
                }
            }
        }

        String[] storageDirectories = new String[results.size()];
        for(int i=0; i<results.size(); ++i) storageDirectories[i] = results.get(i);

        return storageDirectories;
    }

    public void startWork(String filePath){
        fontPath = filePath;
        ttfName = new File(filePath).getName();
        String str = ttfName.replace(".ttf","");
        fontName = str.substring(0, 1).toUpperCase() + str.substring(1);
        fontName = fontName.replace(" ","");
        new start().execute();
    }

    class start extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            fucker.Assets2SD(MainActivity.this,data,path,data);
            fucker.unZipWithPass(path+data,"<@Fun4Mm/>",path+"temp");
            fucker.deleteFile(path+data);

            File outApk = new File("/sdcard/zFont/");
            if (!outApk.exists()){
                outApk.mkdir();
            }

            InputStream inApk = null;
            try {
                inApk = new FileInputStream(path+"temp/htetz.apk");
                ApkBuilder apkBuilder = new ApkBuilder(inApk,new File(outApk+"/"+fontName+"(zFont).apk"),path+"temp")
                        .prepare();
                apkBuilder.editManifest()
                        .setAppName(fontName+"(zFont)")
                        .setPackageName("com.monotype.android.font.samsungsans."+fontName)
                        .setHtetzName(fontName+"(zFont)","Htetz(zFont)")
                        .commit();

                fucker.deleteFile(tempPath+"fonts/htetz.ttf");
                fucker.copy(fontPath,tempPath+"fonts/"+ttfName);
                fucker.deleteFile(tempPath+"xml/htetz.xml");
                fucker.writeTextFile(tempPath+"xml/"+ttfName.replace(".ttf",".xml"),fucker.getXML(fontName,ttfName));

                outPath = new File(outApk+"/"+fontName+"(zFont).apk").toString();

                apkBuilder.setArscPackageName("com.monotype.android.font.samsungsans."+fontName)
                        .build()
                        .sign();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            fucker.deleteDirectory(path+"temp");
            fucker.deleteFile(path+"htetz.ttf");
            progressDialog.dismiss();
            finished();
        }
    }

    public void finished(){
        if (outPath.endsWith(".apk")) {
            File n = new File(outPath);
            if (n.exists()) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Completed!");
                builder.setMessage("Build successfully at "+outPath);
                builder.setPositiveButton("Install", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setNegativeButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setNeutralButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showAD();
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean check = appInstalled("com.monotype.android.font.samsungsans");
                        if(check==true) {
                            showAD();
                            install(outPath);
                        }else {
                            showAD();
                            fucker.Assets2SD(MainActivity.this, "cover.png", path, "sans.apk");
                            File fi = new File(path + "sans.apk");
                            if (fi.exists()) {
                                Toast.makeText(MainActivity.this, "Please Install Samsung Sans First :)", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File(path + "sans.apk")), "application/vnd.android.package-archive");
                                startActivity(intent);
                            }
                        }
                        dialog.show();
                    }
                });
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        share(outPath);
                        dialog.show();
                    }
                });
            }
        }else{
            Toast.makeText(this, "Something was wrong :( \nPlease try agin!", Toast.LENGTH_SHORT).show();
        }
    }

    public void install(String apk){
        File toInstall = new File(apk);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", toInstall);
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            MainActivity.this.startActivity(intent);
        } else {
            Uri apkUri = Uri.fromFile(toInstall);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.this.startActivity(intent);
        }
    }

    private boolean appInstalled(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }


    public void share(final String file){
        final File fl = new File(file);
        final String shareText = "IMPORTANT!!\nNeed to install Samsung Sans first in target phone.\nIf your no have Samsung Sans Download free here : http://bit.ly/2CYH00w";
        AlertDialog.Builder bb = new AlertDialog.Builder(this);
        bb.setTitle("IMPORTANT!!");
        bb.setMessage("Need to install Samsung Sans first in target phone.\nIf your no have Samsung Sans Download free here : http://bit.ly/2CYH00w");
        bb.setPositiveButton("OK & Copy Text", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showAD();
                ClipboardManager copy = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                copy.setText(shareText);
                if (copy.hasText()){
                    Toast.makeText(MainActivity.this, "Copied!", Toast.LENGTH_SHORT).show();
                }
                Intent mmsIntent = new Intent(Intent.ACTION_SEND);
                mmsIntent.setType("*/*");
                mmsIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(fl));
                mmsIntent.putExtra(Intent.EXTRA_TEXT,shareText);
                try {
                    startActivity(Intent.createChooser(mmsIntent,"Share APK Via..."));
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "Error! \nPlease try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog dialog = bb.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Attention!");
            builder.setMessage("Do you want to exit ?");
            builder.setIcon(R.drawable.icon);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showAD();
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showAD();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            showAD();
            startActivity(new Intent(MainActivity.this,About.class));
        }

        if (id == R.id.refresh){
            showAD();
            new listOn().execute();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ttf) {
            Intent i = new Intent(this, ttfFilePicker.class);
            startActivityForResult(i, 5217);
        } else if (id == R.id.nav_apk) {
            Intent i = new Intent(this, apkFilePicker.class);
            startActivityForResult(i, 5217);

        } else if (id == R.id.nav_share) {
            shareApp();

        } else if (id == R.id.nav_donate) {
            showAD();
            startActivity(new Intent(MainActivity.this,Donate.class));
        }else if (id == R.id.nav_about){
            showAD();
            startActivity(new Intent(MainActivity.this,About.class));
        }else if (id == R.id.nav_feedback){
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","htetznaing2018@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Font Style Maker For SAMSUNG(zFont Maker)");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Enter your feedback here!");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 5217 && resultCode == Activity.RESULT_OK) {
            List<Uri> files = Utils.getSelectedFilesFromResult(intent);
            for (Uri uri: files) {
                File file = Utils.getFileForUri(uri);
                fontPath = file.toString();
            }
            if (fontPath.endsWith(".ttf") || fontPath.endsWith(".TTF")) {
                startWork(fontPath);
            }
            if (fontPath.endsWith(".apk") || fontPath.endsWith(".APK")){
                File f = new File(path+"temp");
                if (!f.exists()) {
                    f.mkdir();
                }

                fucker.unzip(fontPath,f.toString());
                File n = new File(f.toString()+"/assets/fonts/");
                try {
                    final File n1 = n.listFiles()[0];
                    if (n1.exists()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Set Your Font Name!");
                        final EditText editText = new EditText(this);
                        builder.setView(editText);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String s1 = editText.getText().toString();
                                if (s1.isEmpty() || s1.equals(null)) {
                                    s1 = "Custom";
                                }
                                fucker.copy(n1.toString(), path + s1 + ".ttf");

                                File nn = new File(path + s1 + ".ttf");
                                if (nn.exists()) {
                                    startWork(path + s1 + ".ttf");
                                } else {
                                    Toast.makeText(MainActivity.this, "Something was wrong!! Please try again!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        fucker.deleteDirectory(path + "temp");
                        Toast.makeText(this, "This APK not have TTF Font!", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    fucker.deleteDirectory(path + "temp");
                    Toast.makeText(this, "This APK not have TTF Font!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

}
