package dev.dev7.example;

import static dev.dev7.lib.v2ray.utils.V2rayConstants.*;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import dev.dev7.lib.v2ray.V2rayController;
import dev.dev7.lib.v2ray.utils.V2rayConstants;

public class MainActivity extends AppCompatActivity {

    private ScrollView pageServers, pageSettings, pageContact;
    private View sidebar, overlay;
    private LinearLayout navServers, navSettings, navContact;
    private TextView btnLangMy, btnLangEn;
    private View btnVpn, vpnPulse;
    private ImageView ivVpnIcon;
    private TextView tvVpnStatus, tvSelectedServer, tvSelectedLocation, tvCoreVersion;
    private LinearLayout serversContainer;
    private TextView btnThemeLight, btnThemeDark, btnSizeSmall, btnSizeMedium, btnSizeLarge;
    private LinearLayout layoutStats;
    private TextView tvDuration, tvDl, tvUl;
    private String currentLang = "my";
    private int selectedServerIndex = 0;
    private V2rayConstants.CONNECTION_STATES vpnState = V2rayConstants.CONNECTION_STATES.DISCONNECTED;
    private BroadcastReceiver v2rayReceiver;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final String[][] servers = new String[][]{
        {"1","SG-01","VLESS","X CDN",null,"vless://26fe5cdd-e772-4238-8adc-9bf53d4781fa@www.x.com:443?path=%2F&security=tls&encryption=none&insecure=0&host=coca.nobless.workers.dev&type=ws&allowInsecure=0&sni=coca.nobless.workers.dev#SG-01"},
        {"2","TH-02","VLESS","Thailand",null,"vless://26fe5cdd-e772-4238-8adc-9bf53d4781fa@www.dtac.com:443?path=%2F&security=tls&encryption=none&insecure=0&host=coca.nobless.workers.dev&type=ws&allowInsecure=0&sni=coca.nobless.workers.dev#TH-02"},
        {"3","Cookie","Trojan","CookieLaw CDN",null,"trojan://3413d540-942c-4763-ad39-3854a3621a2e@cdn.cookielaw.org:443?path=%2F&security=tls&insecure=0&host=galaxy-3.z-empire.workers.dev&type=ws&allowInsecure=0&sni=galaxy-3.z-empire.workers.dev#Cookie"},
        {"4","SpaceX","Trojan","USA/SpaceX",null,"trojan://3413d540-942c-4763-ad39-3854a3621a2e@www.spacex.com:443?path=%2F&security=tls&insecure=0&host=galaxy-3.z-empire.workers.dev&type=ws&allowInsecure=0&sni=galaxy-3.z-empire.workers.dev#SpaceX"},
        {"5","Copilot","VLESS","Microsoft CDN",null,"vless://8221a740-8218-4775-ab45-0bab948285ec@copilot.microsoft.com:443?path=%2F&security=tls&encryption=none&insecure=0&host=sub.galaxytunnel2026.workers.dev&type=ws&allowInsecure=0&sni=sub.galaxytunnel2026.workers.dev#Copilot"},
        {"6","Lift","VLESS","Global",null,"vless://8221a740-8218-4775-ab45-0bab948285ec@liftcreations.com:443?path=%2F&security=tls&encryption=none&insecure=0&host=sub.galaxytunnel2026.workers.dev&type=ws&allowInsecure=0&sni=sub.galaxytunnel2026.workers.dev#Lift"},
        {"7","ChatGPT","Trojan","ChatGPT CDN",null,"trojan://18616960-5953-490c-a717-5462c9c63517@www.chatgpt.com:443?path=%2F&security=tls&insecure=0&host=galaxy-2.pages.dev&type=ws&allowInsecure=0&sni=galaxy-2.pages.dev#ChatGPT"},
        {"8","GX-05","VLESS","Cloudflare",null,"vless://7777489c-9d5f-407d-81e9-3467cff92134@galaxy-5.gaxlayplanet.workers.dev:443?path=ed%3D%2F2680&security=tls&alpn=h3%2Ch2%2Chttp%2F1.1&encryption=none&host=galaxy-5.gaxlayplanet.workers.dev&fp=random&type=ws&sni=galaxy-5.gaxlayplanet.workers.dev#GX-05"},
        {"9","GT-01","VLESS","Cloudflare",null,"vless://8221a740-8218-4775-ab45-0bab948285ec@sub.galaxytunnel2026.workers.dev:443?security=tls&encryption=none&host=sub.galaxytunnel2026.workers.dev&type=ws&sni=sub.galaxytunnel2026.workers.dev#GT-01"},
        {"10","Nobles","VLESS","Cloudflare",null,"vless://26fe5cdd-e772-4238-8adc-9bf53d4781fa@coca.nobless.workers.dev:443?path=%2F&security=tls&encryption=none&host=coca.nobless.workers.dev&type=ws&sni=coca.nobless.workers.dev#Nobles"},
        {"11","Clone","Trojan","Cloudflare",null,"trojan://5a733fcb-f724-45d5-9f6f-9cd96d812409@clone.yatokami.workers.dev:443?path=%2F&security=tls&alpn=h3%2Ch2%2Chttp%2F1.1&host=clone.yatokami.workers.dev&fp=chrome&type=ws&sni=clone.yatokami.workers.dev#Clone"},
        {"12","Pages","Trojan","Global",null,"trojan://18616960-5953-490c-a717-5462c9c63517@galaxy-2.pages.dev:443?path=%2F&security=tls&host=galaxy-2.pages.dev&fp=random&type=ws&sni=galaxy-2.pages.dev#Pages"},
        {"13","Empire","Trojan","Cloudflare",null,"trojan://3413d540-942c-4763-ad39-3854a3621a2e@galaxy-3.z-empire.workers.dev:443?path=%2F&security=tls&alpn=h3&host=galaxy-3.z-empire.workers.dev&type=ws&sni=galaxy-3.z-empire.workers.dev#Empire"},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        V2rayController.init(this, R.drawable.ic_launcher, "Galaxy Tunnel");
        initViews();
        setupListeners();
        updateLanguageUI();
        renderServers();
        registerV2rayReceiver();
        tvCoreVersion.setText("Xray Core " + V2rayController.getCoreVersion());
    }

    private void initViews() {
        pageServers = findViewById(R.id.page_servers);
        pageSettings = findViewById(R.id.page_settings);
        pageContact = findViewById(R.id.page_contact);
        sidebar = findViewById(R.id.sidebar);
        overlay = findViewById(R.id.overlay);
        navServers = findViewById(R.id.nav_servers);
        navSettings = findViewById(R.id.nav_settings);
        navContact = findViewById(R.id.nav_contact);
        btnLangMy = findViewById(R.id.btn_lang_my);
        btnLangEn = findViewById(R.id.btn_lang_en);
        btnVpn = findViewById(R.id.btn_vpn);
        vpnPulse = findViewById(R.id.vpn_pulse);
        ivVpnIcon = findViewById(R.id.iv_vpn_icon);
        tvVpnStatus = findViewById(R.id.tv_vpn_status);
        tvSelectedServer = findViewById(R.id.tv_selected_server);
        tvSelectedLocation = findViewById(R.id.tv_selected_location);
        tvCoreVersion = findViewById(R.id.tv_core_version);
        serversContainer = findViewById(R.id.servers_container);
        btnThemeLight = findViewById(R.id.btn_theme_light);
        btnThemeDark = findViewById(R.id.btn_theme_dark);
        btnSizeSmall = findViewById(R.id.btn_size_small);
        btnSizeMedium = findViewById(R.id.btn_size_medium);
        btnSizeLarge = findViewById(R.id.btn_size_large);
        layoutStats = findViewById(R.id.layout_stats);
        tvDuration = new TextView(this);
        tvDl = new TextView(this);
        tvUl = new TextView(this);
    }

    private void setupListeners() {
        findViewById(R.id.btn_menu).setOnClickListener(v -> toggleSidebar());
        overlay.setOnClickListener(v -> closeSidebar());
        navServers.setOnClickListener(v -> { switchPage(0); closeSidebar(); });
        navSettings.setOnClickListener(v -> { switchPage(1); closeSidebar(); });
        navContact.setOnClickListener(v -> { switchPage(2); closeSidebar(); });
        btnVpn.setOnClickListener(v -> toggleVPN());
        btnLangMy.setOnClickListener(v -> setLanguage("my"));
        btnLangEn.setOnClickListener(v -> setLanguage("en"));
        btnThemeLight.setOnClickListener(v -> applyTheme(false));
        btnThemeDark.setOnClickListener(v -> applyTheme(true));
        btnSizeSmall.setOnClickListener(v -> setFontSize(0.85f));
        btnSizeMedium.setOnClickListener(v -> setFontSize(1.0f));
        btnSizeLarge.setOnClickListener(v -> setFontSize(1.2f));
    }

    private void toggleVPN() {
        if (vpnState == V2rayConstants.CONNECTION_STATES.DISCONNECTED) {
            V2rayController.startV2ray(this, servers[selectedServerIndex][1], servers[selectedServerIndex][5], null);
        } else {
            V2rayController.stopV2ray(this);
        }
    }

    private void updateVpnUI(V2rayConstants.CONNECTION_STATES state) {
        vpnState = state;
        handler.post(() -> {
            switch (state) {
                case CONNECTED:
                    vpnPulse.setVisibility(View.VISIBLE);
                    ivVpnIcon.setImageResource(R.drawable.ic_shield);
                    tvVpnStatus.setText("CONNECTED");
                    tvVpnStatus.setTextColor(getColor(R.color.success));
                    layoutStats.setVisibility(View.VISIBLE);
                    ScaleAnimation anim = new ScaleAnimation(0.9f, 1.4f, 0.9f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    anim.setDuration(2000);
                    anim.setRepeatCount(Animation.INFINITE);
                    vpnPulse.startAnimation(anim);
                    break;
                case CONNECTING:
                    vpnPulse.setVisibility(View.GONE);
                    ivVpnIcon.setImageResource(R.drawable.ic_power);
                    tvVpnStatus.setText("CONNECTING...");
                    tvVpnStatus.setTextColor(getColor(R.color.warning));
                    layoutStats.setVisibility(View.GONE);
                    break;
                default:
                    vpnPulse.setVisibility(View.GONE);
                    ivVpnIcon.setImageResource(R.drawable.ic_power);
                    tvVpnStatus.setText("DISCONNECTED");
                    tvVpnStatus.setTextColor(getColor(R.color.text_secondary));
                    layoutStats.setVisibility(View.GONE);
                    break;
            }
        });
    }

    private void renderServers() {
        serversContainer.removeAllViews();
        for (int i = 0; i < servers.length; i++) {
            final int index = i;
            String[] s = servers[i];
            boolean isActive = (i == selectedServerIndex);
            int dp = (int) (1 * getResources().getDisplayMetrics().density);

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp*16, dp*16, dp*16, dp*16);
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cp.setMargins(0, 0, 0, dp*12);
            card.setLayoutParams(cp);
            card.setBackgroundResource(R.drawable.card_bg);
            card.setOnClickListener(v -> selectServer(index));

            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);

            TextView nameView = new TextView(this);
            nameView.setText(s[1]);
            nameView.setTextColor(isActive ? Color.BLACK : Color.WHITE);
            nameView.setTextSize(10);
            nameView.setPadding(dp*8, dp*3, dp*8, dp*3);
            nameView.setBackgroundColor(isActive ? Color.WHITE : Color.parseColor("#FF272724"));

            TextView pingView = new TextView(this);
            pingView.setText("-- ms");
            pingView.setTextColor(getColor(R.color.text_secondary));
            pingView.setTextSize(13);

            topRow.addView(nameView);
            topRow.addView(pingView);
            ((LinearLayout.LayoutParams)pingView.getLayoutParams()).gravity = Gravity.END;
            ((LinearLayout.LayoutParams)pingView.getLayoutParams()).weight = 1;
            card.addView(topRow);

            LinearLayout midRow = new LinearLayout(this);
            midRow.setOrientation(LinearLayout.HORIZONTAL);
            midRow.setPadding(0, dp*10, 0, 0);

            TextView locView = new TextView(this);
            locView.setText(s[3]);
            locView.setTextColor(isActive ? Color.BLACK : Color.WHITE);
            locView.setTextSize(14);

            TextView tagView = new TextView(this);
            tagView.setText(s[2]);
            tagView.setTextSize(9);
            tagView.setPadding(dp*6, dp*2, dp*6, dp*2);
            if (s[2].equals("VLESS")) {
                tagView.setTextColor(getColor(R.color.vless_blue));
                tagView.setBackgroundColor(getColor(R.color.vless_blue_bg));
            } else {
                tagView.setTextColor(getColor(R.color.trojan_pink));
                tagView.setBackgroundColor(getColor(R.color.trojan_pink_bg));
            }

            LinearLayout.LayoutParams tagP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tagP.setMargins(dp*6, 0, 0, 0);
            midRow.addView(locView);
            midRow.addView(tagView, tagP);
            card.addView(midRow);

            TextView metaView = new TextView(this);
            metaView.setText("\uD83C\uDF0F " + s[3]);
            metaView.setTextColor(getColor(R.color.text_secondary));
            metaView.setTextSize(11);
            metaView.setPadding(0, dp*4, 0, dp*10);
            card.addView(metaView);

            TextView btnView = new TextView(this);
            btnView.setText(isActive ? "\u2705 " + (currentLang.equals("my") ? "\u1031\u103B\u1038\u1015\u103C\u102E\u1038" : "Selected") : (currentLang.equals("my") ? "\u1031\u103B\u1038\u1019\u1031\u1038" : "Select"));
            btnView.setTextColor(isActive ? Color.BLACK : Color.WHITE);
            btnView.setTextSize(12);
            btnView.setGravity(Gravity.CENTER);
            btnView.setPadding(0, dp*10, 0, dp*10);
            btnView.setBackgroundColor(isActive ? Color.WHITE : Color.parseColor("#FF272724"));
            btnView.setOnClickListener(v -> selectServer(index));
            card.addView(btnView);

            serversContainer.addView(card);
        }
    }

    private void selectServer(int index) {
        selectedServerIndex = index;
        String[] s = servers[index];
        tvSelectedServer.setText(s[1] + " \u00B7 " + s[2]);
        tvSelectedLocation.setText(s[3]);
        renderServers();
    }

    private void switchPage(int page) {
        pageServers.setVisibility(page == 0 ? View.VISIBLE : View.GONE);
        pageSettings.setVisibility(page == 1 ? View.VISIBLE : View.GONE);
        pageContact.setVisibility(page == 2 ? View.VISIBLE : View.GONE);
        navServers.setBackgroundResource(page == 0 ? R.drawable.sidebar_active_bg : 0);
        navSettings.setBackgroundResource(page == 1 ? R.drawable.sidebar_active_bg : 0);
        navContact.setBackgroundResource(page == 2 ? R.drawable.sidebar_active_bg : 0);
        if (page == 0) renderServers();
    }

    private void toggleSidebar() {
        int dp = (int) (280 * getResources().getDisplayMetrics().density);
        if (sidebar.getTranslationX() == 0) {
            sidebar.animate().translationX(-dp).setDuration(300);
            overlay.setVisibility(View.GONE);
        } else {
            sidebar.animate().translationX(0).setDuration(300);
            overlay.setVisibility(View.VISIBLE);
        }
    }

    private void closeSidebar() {
        int dp = (int) (280 * getResources().getDisplayMetrics().density);
        sidebar.animate().translationX(-dp).setDuration(300);
        overlay.setVisibility(View.GONE);
    }

    private void setLanguage(String lang) {
        currentLang = lang;
        boolean isMy = lang.equals("my");
        btnLangMy.setBackgroundResource(isMy ? R.drawable.btn_active_bg : R.drawable.btn_lang_bg);
        btnLangMy.setTextColor(isMy ? Color.BLACK : getColor(R.color.text_secondary));
        btnLangEn.setBackgroundResource(!isMy ? R.drawable.btn_active_bg : R.drawable.btn_lang_bg);
        btnLangEn.setTextColor(!isMy ? Color.BLACK : getColor(R.color.text_secondary));
        renderServers();
    }

    private void updateLanguageUI() {
        setLanguage(currentLang);
    }

    private void applyTheme(boolean dark) {
        btnThemeLight.setBackgroundResource(dark ? R.drawable.btn_lang_bg : R.drawable.btn_active_bg);
        btnThemeLight.setTextColor(dark ? getColor(R.color.text_secondary) : Color.BLACK);
        btnThemeDark.setBackgroundResource(dark ? R.drawable.btn_active_bg : R.drawable.btn_lang_bg);
        btnThemeDark.setTextColor(dark ? Color.BLACK : getColor(R.color.text_secondary));
    }

    private void setFontSize(float scale) {
        btnSizeSmall.setBackgroundResource(scale == 0.85f ? R.drawable.btn_active_bg : R.drawable.btn_lang_bg);
        btnSizeMedium.setBackgroundResource(scale == 1.0f ? R.drawable.btn_active_bg : R.drawable.btn_lang_bg);
        btnSizeLarge.setBackgroundResource(scale == 1.2f ? R.drawable.btn_active_bg : R.drawable.btn_lang_bg);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerV2rayReceiver() {
        v2rayReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() == null) return;
                String dur = intent.getExtras().getString(SERVICE_DURATION_BROADCAST_EXTRA);
                String ul = intent.getExtras().getString(SERVICE_UPLOAD_SPEED_BROADCAST_EXTRA);
                String dl = intent.getExtras().getString(SERVICE_DOWNLOAD_SPEED_BROADCAST_EXTRA);
                V2rayConstants.CONNECTION_STATES st = (V2rayConstants.CONNECTION_STATES) intent.getExtras().getSerializable(SERVICE_CONNECTION_STATE_BROADCAST_EXTRA);
                handler.post(() -> {
                    if (st != null) updateVpnUI(st);
                    if (vpnState == V2rayConstants.CONNECTION_STATES.CONNECTED) {
                        tvDuration.setText(dur != null ? dur : "00:00:00");
                        tvDl.setText(dl != null ? dl : "0 B/s");
                        tvUl.setText(ul != null ? ul : "0 B/s");
                    }
                });
            }
        };
        IntentFilter filter = new IntentFilter(V2RAY_SERVICE_STATICS_BROADCAST_INTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) registerReceiver(v2rayReceiver, filter, RECEIVER_EXPORTED);
        else registerReceiver(v2rayReceiver, filter);
    }

    public void openTelegram(View v) {
        try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Swnt7771"))); }
        catch (Exception e) { Toast.makeText(this, "Unable to open", Toast.LENGTH_SHORT).show(); }
    }
    public void openFacebook(View v) {
        try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/share/14bd1ThErYb/"))); }
        catch (Exception e) { Toast.makeText(this, "Unable to open", Toast.LENGTH_SHORT).show(); }
    }
    public void openPhone(View v) {
        try { startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:09674688300"))); }
        catch (Exception e) { Toast.makeText(this, "Unable to open", Toast.LENGTH_SHORT).show(); }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (v2rayReceiver != null) {
            try { unregisterReceiver(v2rayReceiver); } catch (Exception ignored) {}
        }
    }
}
