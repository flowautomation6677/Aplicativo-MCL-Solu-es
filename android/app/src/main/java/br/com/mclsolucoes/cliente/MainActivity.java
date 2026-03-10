package br.com.mclsolucoes.cliente;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.core.view.WindowCompat;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Permite que o Android cuide das áreas seguras sem "espremer" o WebView
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }

    @Override
    public void onStart() {
        super.onStart();

        WebView webView = this.bridge.getWebView();
        if (webView != null) {
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    // Injeta CSS no Chatwoot para afastar o conteúdo do topo (Status Bar) e do
                    // fundo (Navigation)
                    String css = "var style = document.createElement('style'); " +
                            "style.innerHTML = 'body { padding-top: 35px !important; padding-bottom: 20px !important; box-sizing: border-box !important; height: 100vh !important; }'; "
                            +
                            "document.head.appendChild(style);";
                    view.evaluateJavascript(css, null);
                }
            });
        }
    }

    // Intercepta o botão "voltar" nativo do Android
    @Override
    public void onBackPressed() {
        if (this.bridge != null && this.bridge.getWebView() != null && this.bridge.getWebView().canGoBack()) {
            this.bridge.getWebView().goBack();
        } else {
            super.onBackPressed();
        }
    }
}
