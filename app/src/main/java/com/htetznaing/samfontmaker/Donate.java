package com.htetznaing.samfontmaker;

import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class Donate extends AppCompatActivity {
    AdRequest adRequest;
    AdView banner;
    InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        adRequest = new AdRequest.Builder().build();
        banner = findViewById(R.id.adView);
        banner.loadAd(adRequest);
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

    public void paypal(View view) {
        showAD();
        Toast.makeText(this, "Thanks for you support :)", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://paypal.me/Fun4Mm"));
        startActivity(i);
    }

    public void bitcoin(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bitcoin Address!");
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.btc);
        builder.setView(imageView);
        builder.setPositiveButton("Copy Address", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showAD();
                ClipboardManager copy = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                copy.setText("1AJw4XW8WGk41wTKhSmk4NrE8qy9j4uCCq");
                if (copy.hasText()){
                    Toast.makeText(Donate.this, "Copied : 1AJw4XW8WGk41wTKhSmk4NrE8qy9j4uCCq", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dev(View view) {
        showAD();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            intent.setData(Uri.parse("fb://profile/100011339710114"));
            startActivity(intent);
        } catch (Exception e) {
            intent.setData(Uri.parse("https://m.facebook.com/KHtetzNaing"));
            startActivity(intent);
        }
    }
}
