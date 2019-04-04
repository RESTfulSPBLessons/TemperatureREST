package club;

import club.bot.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

@SpringBootApplication
//@ImportResource(value="classpath:/hsql_cfg.xml") // нужно чтобы поднять hsql сервер прямо из аппликейшн. Сервер будет связан с локальным файлом
@EnableTransactionManagement
public class Application {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(Application.class, args);
    }

    /*@Bean
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
    }*/

}
