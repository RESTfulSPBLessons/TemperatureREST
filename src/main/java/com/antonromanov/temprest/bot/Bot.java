package com.antonromanov.temprest.bot;


import org.apache.commons.io.input.ReversedLinesFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//public class Bot  {
public class Bot extends TelegramLongPollingBot {
    private ExecutorService pool = Executors.newCachedThreadPool();
    private static Logger logger = LoggerFactory.getLogger("console_logger");

    private String token;
    private String user;
    private Long chanelId;
    private String logFileName;

    private Environment env;



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
               // String command = inMessage.getText().toLowerCase();
                if (command.startsWith("/status")) {

                    fireMessage(inMessage.getChatId(), "Здесь будет JSON");

                }
            }



           /* if(command.startsWith("/log")) {
                if(command.equals("/log+")) {
                    fireFielLog(inMessage.getChatId());
                }else {
                    try {
                        int lines = Integer.valueOf(command.substring(4));
                        if (lines > 0)
                            fireFielLogLines(inMessage.getChatId(), lines);
                        else fireMessage(inMessage.getChatId(),"некорректное колличество строк");
                    } catch (NumberFormatException ex) {
                        fireMessage(inMessage.getChatId(), "Неверный формат команды");
                    }
                }
            }else if(command.equals("/stat")) {
                pool.submit(() -> {
                    try {
                        //Long sum = accountRefillRepository.getSumOfAllTransactions();
                        Long sum = 145L;
                        sum = sum == null ? 0 : sum;

                        SendMessage outMessage = new SendMessage();
                        outMessage.setChatId(inMessage.getChatId());
                        outMessage.enableHtml(true);
                        outMessage.setText("<b>"+getBotUsername()+"</b>: \n\n"+"Счетов всего:         "+1545+"\n"
                                +"Транзакций всего:     "+564654564+"\n"
                                +"Сумма всех переводов: "+sum/100);
                        execute(outMessage);
                    }catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                });
            }*/
        }
    }

    private String getLog(int lines) {
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(new File(logFileName))) {
            StringBuffer sb = new StringBuffer();
            while(lines > 0) {
                String line = reader.readLine();
                if(line == null)
                    break;
                sb.append(line).append("\n");
                lines--;
            }
            return sb.toString();
        }catch (Exception ex) {
            logger.error("Ошибка при чтении лога", ex);
            return "Ошибка при чтении лога";
        }
    }

    private void fireFielLogLines(Long chartId, int lines) {
        pool.submit(() -> {
            File file = new File(logFileName.substring(0,logFileName.lastIndexOf("."))+"-"+lines+".log");
            try (FileOutputStream out = new FileOutputStream(file)) {
                List<String> list = Arrays.asList(getLog(lines).split("\n"));
                Collections.reverse(list);
                out.write(String.join("\n",list).getBytes());
            }catch (Exception ex) {
                logger.error("Не получилось записать log файл из "+lines+" строк", ex);
            }finally {
                try {
                    SendDocument outMessage = new SendDocument();
                    outMessage.setChatId(chartId);
                    outMessage.setDocument(file);
                    execute(outMessage);
                    Files.deleteIfExists(file.toPath());
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        });
    }

    private Message getMessage(Update update) {
        if(update.hasChannelPost() && update.getChannelPost().hasText())
            return update.getChannelPost();
        if(update.hasMessage() && update.getMessage().hasText())
            return update.getMessage();
        return null;
    }

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

    public void fireMessage(String msg) {
        fireMessage(chanelId, msg);
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
}