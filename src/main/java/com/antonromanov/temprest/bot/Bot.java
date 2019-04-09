package com.antonromanov.temprest.bot;

import com.antonromanov.temprest.service.MainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static com.antonromanov.temprest.utils.Utils.createBotStatus;


public class Bot extends TelegramLongPollingBot {

    private ExecutorService pool = Executors.newCachedThreadPool();
    private static Logger logger = LoggerFactory.getLogger("console_logger");
    private String token;
    private String user;
    private Long chanelId;
    private String logFileName;
    private Environment env;

    /**
     * Инжектим сервис.
     */
    @Autowired
    MainService mainService;




    public Bot(DefaultBotOptions options) {
        super(options);
    }

    public Bot(DefaultBotOptions options, Environment env) {
        super(options);
        this.env = env;
        this.token       = env.getProperty("telegram.bot.token");
        this.user        = env.getProperty("telegram.bot.user.name");
        this.chanelId    = env.getProperty("telegram.chanel.id", Long.TYPE);
        this.logFileName = env.getProperty("telegram.bot.app.log.file");
    }

    public void onUpdateReceived(Update update) {
        Message inMessage = getMessage(update);

        if(inMessage != null && inMessage.getText().startsWith("/")) {
            String command = inMessage.getText().toLowerCase();


            if(inMessage != null && inMessage.getText().startsWith("/")) {

                if (command.startsWith("/status")) {
                    fireMessage(inMessage.getChatId(), createBotStatus(mainService.getGlobalStatus()));

                }
                if (command.startsWith("/ts")) { //temp status
                    fireMessage(inMessage.getChatId(), mainService.getLastTemp().getStatus());

                }

            // Выдача лог-файла
                if(command.startsWith("/log")) {
                    if (command.equals("/log+")) {
                        fireFielLog(inMessage.getChatId());
                    }
                }
            }
        }
    }

    private Message getMessage(Update update) {
        if(update.hasChannelPost() && update.getChannelPost().hasText())
            return update.getChannelPost();
        if(update.hasMessage() && update.getMessage().hasText())
            return update.getMessage();
        return null;
    }

    /**
     * Отправить файл лога как сообщение
     *
     * @param chartId
     */
    private void fireFielLog(Long chartId) {
        pool.submit(() -> {
            File file = new File(logFileName.substring(0,logFileName.lastIndexOf("."))+"-t.log");
            file.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(file)) {
                Files.copy(new File(logFileName).toPath(), out);
                SendDocument outMessage = new SendDocument();
                outMessage.setChatId(chartId);
                outMessage.setDocument(file);
                execute(outMessage);
            } catch (Exception e) {
                logger.error("Не получилось отправить log файл ", e);
            }finally {
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (IOException e) {
                    logger.error("Не получилось удалить временный log файл ", e);
                }
            }
        });
    }


    public void fireMessage(Long chanelId, String msg) {
        pool.submit(() -> {
            try {
                SendMessage outMessage = new SendMessage();
                outMessage.enableHtml(true);
                outMessage.setChatId(chanelId);
                outMessage.setText("<b>"+getBotUsername()+"</b>: \n\n"+msg);
                execute(outMessage);
            } catch (TelegramApiException e) {
                logger.error("Не получилось отправить сообщение в канал Риф ошибочки ", e);
            }
        });
    }

    public String getBotUsername() {
        return user;
    }

    public String getBotToken() {
        return token;
    }

    public void fireMessage(String msg) {
        fireMessage(chanelId, msg);
    }
}