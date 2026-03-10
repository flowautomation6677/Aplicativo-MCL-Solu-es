import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'br.com.mclsolucoes.cliente',
  appName: 'MCL Soluções',
  webDir: 'www',
  server: {
    url: 'https://chat.mclsolucoes.com.br',
    cleartext: true,
    allowNavigation: [
      'chat.mclsolucoes.com.br',
      '*.mclsolucoes.com.br'
    ]
  },
  android: {
    // Evita que o teclado cubra o campo de texto do chat
    resizeOnFullScreen: true,
    backgroundColor: '#1C3A6E'
  },
  plugins: {
    StatusBar: {
      // Azul marinho MCL na barra de status
      backgroundColor: '#1C3A6E',
      style: 'LIGHT',
      overlaysWebView: false
    },
    SplashScreen: {
      launchShowDuration: 2000,
      backgroundColor: '#1C3A6E',
      showSpinner: false
    }
  }
};

export default config;
