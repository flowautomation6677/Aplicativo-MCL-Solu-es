# Lições Aprendidas: PWA em Capacitor com WebViews que usam `viewport-fit=cover`

## O Problema
Ao encapsular um site externo (como o Chatwoot) usando o Capacitor (`server.url`), se o site contiver a meta tag HTML `<meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">`, o Capacitor (WebView) vai obedecer a essa regra. 

O comportamento esperado de `viewport-fit=cover` é ignorar as *Safe Areas* do sistema, forçando o site a renderizar *por baixo* da Status Bar (bateria e hora) e da Navigation Bar (botões virtuais do Android ou barra de gesto). 
Isso frequentemente sobrepõe os elementos clicáveis do site com os elementos do sistema operacional, tornando o app inutilizável nas bordas.

### Tentativas Incorretas (A Evitar)
1. **Gambiarra em CSS no Java:** Tentar injetar Javascript no `onStart` adicionando variáveis CSS ou paddings fixos (ex: `padding-top: 35px;`). O tamanho da barra varia de aparelho para aparelho, logo a margem nunca ficará perfeita em todos eles.
2. **`WindowCompat.setDecorFitsSystemWindows(getWindow(), false);`:** Isso diz ao próprio Android inteiro que sua Activity (janela) quer se desenhar por baixo do sistema, ignorando o próprio insets. Como o site dentro do WebView *também* quer fazer a mesma coisa por conta do HTML, a sobreposição apenas piora.

## A Solução Definitiva
Para sobrepor o comando HTML do site sem precisar alterar o código do site remoto, devemos usar a **API nativa do Android baseada em Window Insets (`WindowInsetsCompat`)** para descobrir a altura métrica exata em pixels da Status bar e da Navigation bar do telefone *onde o app está rodando naquele momento*.

Em seguida, aplicamos esses valores de altura exatos como margem física (`MarginLayoutParams`) diretamente no container nativo da WebView, espremendo o site pro meio da tela segura.

### Código Java em `MainActivity.java` (Android API 21+ compatível via AndroidX)

```java
package br.com.mclsolucoes.cliente; // Trocar pelo seu pacote

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
            // Escuta o sistema Android para pegar o tamanho EXATO atualizado das margens seguras (insets)
            ViewCompat.setOnApplyWindowInsetsListener(webView, (v, insets) -> {
                
                // .top mapeia a altura da Status Bar (Relógio/Bateria). .bottom a Navigation Bar
                int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
                int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
                
                // Aplica nas LayoutParams nativas do contêiner da WebView
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                params.topMargin = top;
                params.bottomMargin = bottom;
                v.setLayoutParams(params);
                
                return insets;
            });
        }
    }

    // Intercepta o botão "voltar" físico gerado pelo Android e mapeia pro botão voltar "browser"
    @Override
    public void onBackPressed() {
        if (this.bridge != null && this.bridge.getWebView() != null && this.bridge.getWebView().canGoBack()) {
            this.bridge.getWebView().goBack();
        } else {
            super.onBackPressed();
        }
    }
}
```

## Benefícios dessa Abordagem
- Abstrato de marca e modelo: A margem respeita dinamicamente aparelhos com "Notch" (furo da câmera na tela) grande ou pequeno, bordas arredondadas e barras de navegação virtuais finas (gestos Samsung) ou largas (3 botões on-screen tradicionais).
- A cor da sua Status bar agora provém 100% da sua configuração no Capacitor SDK (ex: `capacitor.config.ts`), impedindo que o site tente colorir as bordas de branco ou sobrepondo conteúdo.
