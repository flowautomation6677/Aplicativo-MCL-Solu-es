package br.com.mclsolucoes.cliente;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        View webView = this.bridge.getWebView();
        if (webView != null) {
            // Este código ouve o sistema Android e descobre o tamanho EXATO das barras de
            // cada modelo de celular
            ViewCompat.setOnApplyWindowInsetsListener(webView, (v, insets) -> {

                // Pega a altura da barra do topo (bateria/hora) e da barra de baixo (botões
                // virtuais)
                int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
                int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;

                // Aplica essas medidas nativamente como margem no WebView, impedindo a
                // sobreposição
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                params.topMargin = top;
                params.bottomMargin = bottom;
                v.setLayoutParams(params);

                return insets;
            });
        }
    }

    // Intercepta o botão "voltar" nativo do Android para voltar a página web do
    // chat
    @Override
    public void onBackPressed() {
        if (this.bridge != null && this.bridge.getWebView() != null && this.bridge.getWebView().canGoBack()) {
            this.bridge.getWebView().goBack();
        } else {
            super.onBackPressed();
        }
    }
}
