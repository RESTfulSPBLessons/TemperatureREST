package club;

import club.bot.Bot;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import sun.misc.BASE64Decoder;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Base64;

@SpringBootApplication
@ImportResource(value="classpath:/hsql_cfg.xml") // нужно чтобы поднять hsql сервер прямо из аппликейшн. Сервер будет связан с локальным файлом
@EnableTransactionManagement
public class Application {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Autowired
    Bot bot(Environment env) {
        Bot bot = null;
        if(env.getProperty("telegram.bot", Boolean.TYPE)) {
            TelegramBotsApi botsApi = new TelegramBotsApi();
            try {
                DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
                if (env.getProperty("telegram.proxy.set", Boolean.TYPE)) {

                    Authenticator.setDefault(new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(env.getProperty("telegram.proxy.user"), env.getProperty("telegram.proxy.pass").toCharArray());
                        }
                    });

                    botOptions.setProxyHost(env.getProperty("telegram.proxy.host"));
                    botOptions.setProxyPort(env.getProperty("telegram.proxy.port", Integer.TYPE));
                    botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
                }
                botsApi.registerBot(bot = new Bot(botOptions, env));
            } catch (TelegramApiRequestException e) {
                e.printStackTrace();
            }
        }
        return bot;
    }

    @Bean(name="SimpleRestTemplate")
    RestTemplate SimpleRestTemplate() {
        return new RestTemplate();
    }

    @Bean(name = "IgnoreSSLRestTemplate")
    RestTemplate IgnoreSSLRestTemplate() {
        try {
            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(csf)
                    .build();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            return new RestTemplate(requestFactory);
        }catch (Exception ex) {
            return null;
        }
    }

    @Bean(name = "SimpleRifRestTool")
    @Autowired
    RifRestTool SimpleRifRestTool(@Qualifier("SimpleRestTemplate") RestTemplate restTemplate) {
        return new RifRestTool(restTemplate);
    }

    @Bean(name = "IgnoreSSLRifRestTool")
    @Autowired
    RifRestTool ignoreSSLRifRestTool(@Qualifier("IgnoreSSLRestTemplate") RestTemplate restTemplate) {
        return new RifRestTool(restTemplate);
    }
}
