package dev.dev7.example;

import static dev.dev7.lib.v2ray.utils.V2rayConstants.*;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import dev.dev7.lib.v2ray.V2rayController;
import dev.dev7.lib.v2ray.interfaces.LatencyDelayListener;
import dev.dev7.lib.v2ray.utils.V2rayConstants;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private BroadcastReceiver v2rayBroadCastReceiver;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        webView = new WebView(this);
        setContentView(webView);
        V2rayController.init(this, R.drawable.ic_launcher, "Galaxy Tunnel");
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.addJavascriptInterface(new VpnBridge(), "VpnBridge");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                evaluateJs("if(typeof onCoreVersion === 'function') onCoreVersion('" + escapeJs(V2rayController.getCoreVersion()) + "')");
                syncConnectionStateToWebView();
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("tg://") || url.startsWith("tel:")) {
                    try { startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))); } catch (Exception ig) {}
                    return true;
                }
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/galaxy_tunnel.html");
        registerV2rayReceiver();
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerV2rayReceiver() {
        v2rayBroadCastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() == null) return;
                String dur = intent.getExtras().getString(SERVICE_DURATION_BROADCAST_EXTRA);
                String ul = intent.getExtras().getString(SERVICE_UPLOAD_SPEED_BROADCAST_EXTRA);
                String dl = intent.getExtras().getString(SERVICE_DOWNLOAD_SPEED_BROADCAST_EXTRA);
                V2rayConstants.CONNECTION_STATES st = (V2rayConstants.CONNECTION_STATES) intent.getExtras().getSerializable(SERVICE_CONNECTION_STATE_BROADCAST_EXTRA);
                mainHandler.post(() -> {
                    evaluateJs("if(typeof onVpnStats === 'function') onVpnStats('" + escapeJs(dur) + "','" + escapeJs(ul) + "','" + escapeJs(dl) + "','','')");
                    if (st != null) syncConnectionStateToWebView();
                });
            }
        };
        IntentFilter filter = new IntentFilter(V2RAY_SERVICE_STATICS_BROADCAST_INTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) registerReceiver(v2rayBroadCastReceiver, filter, RECEIVER_EXPORTED);
        else registerReceiver(v2rayBroadCastReceiver, filter);
    }

    private void syncConnectionStateToWebView() {
        V2rayConstants.CONNECTION_STATES st = V2rayController.getConnectionState();
        String s = "DISCONNECTED";
        if (st == V2rayConstants.CONNECTION_STATES.CONNECTED) s = "CONNECTED";
        else if (st == V2rayConstants.CONNECTION_STATES.CONNECTING) s = "CONNECTING";
        evaluateJs("if(typeof onVpnStatus === 'function') onVpnStatus('" + s + "')");
    }

    public class VpnBridge {
        @JavascriptInterface public void connect(String cfg, String name, int idx) { mainHandler.post(() -> { if (V2rayController.getConnectionState() == V2rayConstants.CONNECTION_STATES.DISCONNECTED) V2rayController.startV2ray(MainActivity.this, name, cfg, null); }); }
        @JavascriptInterface public void disconnect() { mainHandler.post(() -> { if (V2rayController.getConnectionState() != V2rayConstants.CONNECTION_STATES.DISCONNECTED) V2rayController.stopV2ray(MainActivity.this); }); }
        @JavascriptInterface public String getConnectionState() { switch (V2rayController.getConnectionState()) { case CONNECTED: return "CONNECTED"; case CONNECTING: return "CONNECTING"; default: return "DISCONNECTED"; } }
        @JavascriptInterface public String getCoreVersion() { return V2rayController.getCoreVersion(); }
        @JavascriptInterface public void checkServerDelay(String cfg, int idx) { new Thread(() -> { long d = V2rayController.getV2rayServerDelay(cfg); mainHandler.post(() -> evaluateJs("if(typeof onServerDelay === 'function') onServerDelay("+idx+","+d+")")); }).start(); }
        @JavascriptInterface public void checkConnectedDelay() { V2rayController.getConnectedV2rayServerDelay(MainActivity.this, d -> mainHandler.post(() -> evaluateJs("if(typeof onConnectedDelay === 'function') onConnectedDelay("+d+")"))); }
        @JavascriptInterface public void showToast(String msg) { mainHandler.post(() -> Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show()); }
    }

    private void evaluateJs(String js) { webView.post(() -> webView.evaluateJavascript(js, null)); }
    private String escapeJs(String s) { if (s == null) return ""; return s.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", ""); }
    private void hideSystemUI() { View d = getWindow().getDecorView(); d.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY); }
    @Override public void onWindowFocusChanged(boolean f) { super.onWindowFocusChanged(f); if (f) hideSystemUI(); }
    @Override protected void onDestroy() { super.onDestroy(); if (v2rayBroadCastReceiver != null) { try { unregisterReceiver(v2rayBroadCastReceiver); } catch (Exception ig) {} } if (webView != null) webView.destroy(); }
}
