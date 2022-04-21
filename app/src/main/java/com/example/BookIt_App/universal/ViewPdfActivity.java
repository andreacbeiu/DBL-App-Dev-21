package com.example.BookIt_App.universal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.BookIt_App.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ViewPdfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        String url = getIntent().getStringExtra("url");
        System.out.println(url);

        WebView wv = (WebView) findViewById(R.id.wvViewPdf);
        wv.setWebViewClient(new WebViewClient());
        wv.getSettings().setJavaScriptEnabled(true);
        try {
            url = URLEncoder.encode(url,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        wv.getSettings().supportZoom();
        wv.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + url);
    }
}