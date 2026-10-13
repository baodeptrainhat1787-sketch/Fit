package com.fitplan.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends Activity {
    private WebView webView;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ReminderReceiver.createChannel(this);
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
        }
        webView = new WebView(this);
        webView.setBackgroundColor(0xFFF4F6F3);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new FitPlanBridge(), "FitPlanNative");
        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
    }
    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack(); else super.onBackPressed();
    }
    public class FitPlanBridge {
        @JavascriptInterface public void setReminder(String kind, int hour, int minute, boolean enabled) {
            if (!enabled) { ReminderReceiver.cancel(MainActivity.this, kind); runOnUiThread(() -> Toast.makeText(MainActivity.this, "Đã tắt nhắc " + label(kind), Toast.LENGTH_SHORT).show()); return; }
            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) return;
            ReminderReceiver.schedule(MainActivity.this, kind, hour, minute);
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Đã đặt nhắc " + label(kind) + " lúc " + String.format(java.util.Locale.getDefault(), "%02d:%02d", hour, minute), Toast.LENGTH_LONG).show());
        }
        @JavascriptInterface public String getReminder(String kind) {
            SharedPreferences p = getSharedPreferences(ReminderReceiver.PREFS, MODE_PRIVATE);
            return p.getBoolean(kind + "_enabled", false) ? p.getInt(kind + "_hour", kind.equals("workout") ? 17 : 22) + ":" + p.getInt(kind + "_minute", 0) : "off";
        }
        @JavascriptInterface public void openExactAlarmSettings() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Intent i = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + getPackageName()));
                startActivity(i);
            }
        }
        private String label(String kind) { return kind.equals("sleep") ? "giờ ngủ" : "tập luyện"; }
    }
}
