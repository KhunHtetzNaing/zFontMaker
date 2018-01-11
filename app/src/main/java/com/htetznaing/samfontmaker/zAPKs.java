package com.htetznaing.samfontmaker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.util.ArrayList;

public class zAPKs extends AppCompatActivity {
    ListView listView;
    ImageView imageView;
    ProgressDialog progressDialog;
    String path = "/sdcard/zFont/";
    ArrayList<String> zapks = new ArrayList<>();
    ArrayList<String> zapkSize = new ArrayList<>();
    ZapkAdapter zapkAdapter;
    File [] zApkFiles;
    Fucker fucker = new Fucker();
    RelativeLayout mainLayout;

    AdRequest adRequest;
    AdView banner;
    InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_z_apks);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait!!");
        progressDialog.setMessage("Listing z Font APK Files....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        listView = findViewById(R.id.listView);
        imageView = findViewById(R.id.imageView);
        mainLayout = findViewById(R.id.relayt);

        new start().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                finished(zApkFiles[i].toString());
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

    public String getSize(File file){
        float sizeInBytes = file.length();
        float sizeInMb = sizeInBytes / (1024 * 1024);

        String size = String.valueOf(sizeInMb);
        size = size.substring(0,4)+" MB";
        Log.d("FileSize",size);
        return size;
    }

    class start extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                File file = new File(path);
                File [] files = file.listFiles();
                if (files.length >= 0){

                    for (int i=0;i<files.length;i++){
                        zApkFiles = files;
                        zapks.add(files[i].getName());
                        zapkSize.add(getSize(files[i]));
                        Log.d("Fucker",files[i].toString()+" "+getSize(files[i]));
                    }
                }
            }catch (Exception e){
                Log.d("Fucker","Failed");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (zapks.size()>=0) {
                listView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                zapkAdapter = new ZapkAdapter(zAPKs.this, zapks,zapkSize);
                listView.setAdapter(zapkAdapter);
            }else {
                imageView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
            progressDialog.dismiss();
            super.onPostExecute(s);
        }
    }

    public void finished(final String outPath){
            final File n = new File(outPath);
            if (n.exists()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Attention!");
                builder.setMessage("What do you want ?");
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
                            install(outPath);
                        }else {
                            showAD();
                            File nn = new File("/sdcard/Android/data/com.htetznaing.samfontmaker/");
                            if (!nn.exists()){
                                nn.mkdir();
                            }
                            String pPath = nn.toString();
                            fucker.Assets2SD(zAPKs.this, "cover.png", pPath, "sans.apk");
                            File fi = new File(pPath+"sans.apk");
                            if (fi.exists()) {
                                Toast.makeText(zAPKs.this, "Please Install Samsung Sans First :)", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File(pPath+"sans.apk")), "application/vnd.android.package-archive");
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
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.zmain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.refresh){
            new start().execute();
        }

        return super.onOptionsItemSelected(item);
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
                ClipboardManager copy = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                copy.setText(shareText);
                if (copy.hasText()){
                    Toast.makeText(zAPKs.this, "Copied!", Toast.LENGTH_SHORT).show();
                }
                Intent mmsIntent = new Intent(Intent.ACTION_SEND);
                mmsIntent.setType("*/*");
                mmsIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(fl));
                mmsIntent.putExtra(Intent.EXTRA_TEXT,shareText);
                try {
                    startActivity(Intent.createChooser(mmsIntent,"Share APK Via..."));
                }catch (Exception e){
                    showAD();
                    Toast.makeText(zAPKs.this, "Error! \nPlease try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog dialog = bb.create();
        dialog.show();
    }

    public void install(String apk){
        showAD();
        File toInstall = new File(apk);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(zAPKs.this, BuildConfig.APPLICATION_ID + ".provider", toInstall);
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            zAPKs.this.startActivity(intent);
        } else {
            Uri apkUri = Uri.fromFile(toInstall);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            zAPKs.this.startActivity(intent);
        }
    }
}
