# Galaxy Tunnel - Android V2Ray Client

**Galaxy Tunnel** သည် WebView-based Android V2Ray VPN Client ဖြစ်ပြီး [V2ray-Android](https://github.com/vonica-ai/V2ray-Android) library ကို အသုံးပြုထားပါတယ်။
HTML UI ကို WebView ထဲမှာ load လုပ်ပြီး JavaScript Bridge ကနေတစ်ဆင့် Native V2Ray/Xray core ကို control လုပ်ပါတယ်။

## Architecture

```
┌─────────────────────────────────────┐
│         Android App (Java)         │
│  ┌───────────────────────────────┐  │
│  │          WebView             │  │
│  │  ┌─────────────────────────┐ │  │
│  │  │  galaxy_tunnel.html     │ │  │
│  │  │  (Tailwind CSS + JS)    │ │  │
│  │  └────────┬────────────────┘ │  │
│  │           │ VpnBridge        │  │
│  │  ┌────────▼────────────────┐ │  │
│  │  │  @JavascriptInterface   │ │  │
│  │  │  connect()/disconnect() │ │  │
│  │  └────────┬────────────────┘ │  │
│  └───────────┼──────────────────┘  │
│              │                      │
│  ┌───────────▼──────────────────┐  │
│  │    V2rayController           │  │
│  │    (dev7 V2Ray Library)      │  │
│  └───────────┬──────────────────┘  │
│              │                      │
│  ┌───────────▼──────────────────┐  │
│  │    Xray Core (Native .so)    │  │
│  └──────────────────────────────┘  │
└─────────────────────────────────────┘
```

## Setup Guide

### 1. Clone this repo
```bash
git clone https://github.com/winnyeintun004-eng/V2ray-Android-Galaxy.git
cd V2ray-Android-Galaxy
```

### 2. Add Real V2Ray Configs
`app/src/main/assets/galaxy_tunnel.html` ထဲက server list မှာ ကိုယ့်ရဲ့ V2Ray Config URI တွေထည့်ပါ:

```javascript
const servers = [
  { id: 1, config: "vless://your-uuid@server.com:443?..." },
  // ...
];
```

### 3. Build APK

#### Local (Android Studio):
Android Studio နဲ့ဖွင့်ပြီး `Build > Build APK`

#### GitHub Actions (Auto):
`main` branch ကို push လုပ်တိုင်း auto-build။
Actions tab မှာ APK download လုပ်လို့ရ။

## Features

- 🎨 **Beautiful WebView UI** — Tailwind CSS, Dark/Light mode, Burmese/English
- ⚡ **Real V2Ray/Xray Core** — VLESS, Trojan protocols
- 📊 **Live Stats** — Upload/Download speed, duration, traffic
- 🌐 **Server Selection** — Multiple servers with ping test
- 📱 **Full Screen Immersive** — No Android chrome visible
- 🔄 **GitHub Actions CI/CD** — Auto-build APK on push

## JavaScript Bridge API

HTML ကနေ Native ကိုခေါ်ဖို့ `VpnBridge` object ကိုသုံးပါ:

```javascript
VpnBridge.connect(configUri, serverName, serverIndex);
VpnBridge.disconnect();
VpnBridge.getConnectionState();
VpnBridge.checkServerDelay(configUri, serverIndex);
```

Native ကနေ HTML ကို ပြန်ခေါ်ဖို့ callback functions:

```javascript
onVpnStatus(status)          // Connection state changed
onVpnStats(dur, ul, dl, ...)  // Real-time traffic stats
onServerDelay(index, ms)      // Ping result
onCoreVersion(version)        // Core version
```

## Requirements

- Android 5.0 (API 21) and above
- JDK 17 for building
- Android SDK 34

## Credits

- [dev7dev/V2ray-Android](https://github.com/dev7dev/V2ray-Android)
- [XTLS/Xray-core](https://github.com/xtls/xray-core)
- [Tailwind CSS](https://tailwindcss.com)
- [Font Awesome](https://fontawesome.com)

---

© 2026 Galaxy Tunnel Team. All rights reserved.
