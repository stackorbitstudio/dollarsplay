package com.dollarsplay.app;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(this);
        setContentView(webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        String html = "<!DOCTYPE html><html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'><style>*{margin:0;padding:0;box-sizing:border-box;font-family:sans-serif}body{background:linear-gradient(to right,#24243e,#302b63,#0f0c29);color:white;min-height:100vh}.header{padding:20px;text-align:center}.header h1{font-size:28px}.balance-card{background:rgba(255,255,255,0.1);margin:15px;border-radius:20px;padding:20px;text-align:center;border:1px solid rgba(255,255,255,0.2)}.balance-card h1{font-size:36px;margin:10px 0;color:#00ff9d}.grid{display:grid;grid-template-columns:1fr 1fr;gap:15px;padding:15px}.card{background:white;color:#302b63;border-radius:15px;padding:20px;text-align:center;font-weight:bold}.card span{font-size:30px;display:block;margin-bottom:10px}.btn{margin:20px;background:#00ff9d;color:#0f0c29;padding:15px;border-radius:30px;text-align:center;font-weight:bold;display:block}</style></head><body><div class='header'><h1>💰 DOLLARSPLAY</h1><p>Offline App - Own Theme</p></div><div class='balance-card'><p>TOTAL BALANCE</p><h1>$<span id='bal'>12.50</span></h1><p>Offline Working</p></div><div class='grid'><div class='card' onclick='addCoin(0.5)'><span>▶️</span>Watch Ad</div><div class='card' onclick='addCoin(1)'><span>🎡</span>Spin</div><div class='card' onclick='addCoin(0.25)'><span>✅</span>Check</div><div class='card' onclick='addCoin(2)'><span>🎁</span>Bonus</div></div><a class='btn' onclick='addCoin(0.1)'>TAP TO EARN + $0.10</a><script>let b=localStorage.getItem('bal')||12.5;document.getElementById('bal').innerText=b;function addCoin(v){b=parseFloat(b)+v;localStorage.setItem('bal',b.toFixed(2));document.getElementById('bal').innerText=b.toFixed(2);}</script></body></html>";
        
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }
}
