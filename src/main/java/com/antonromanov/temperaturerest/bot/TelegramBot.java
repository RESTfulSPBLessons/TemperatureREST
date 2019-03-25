package com.antonromanov.temperaturerest.bot;

/*import org.apache.commons.io.input.ReversedLinesFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;*/
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.File;

public class TelegramBot {
	//public class Bot extends TelegramLongPollingBot {


/*
	private static Logger logger = LoggerFactory.getLogger(Bot.class);
	private final String token = "833516765:AAEqVBbecQ1gYgJb8TcfGxPrOSjrjdj_DB8";

	private int maxLines = 50;
	private String logFileName = "logs/rif.log";

	public Bot(DefaultBotOptions options) {
		super(options);
	}

	public void onUpdateReceived(Update update) {
		Message inMessage = getMessage(update);

		if(inMessage != null && inMessage.getText().startsWith("/")) {
			String outText = "Я такой команды не знаю";
			String command = inMessage.getText().toLowerCase();
			if(command.startsWith("/log")) {
				if(command.equals("/log+")) {
					fireDocument(inMessage.getChatId(), new File(logFileName));
				}else {
					try {
						int lines = Integer.valueOf(command.substring(4));
						if (lines < 1 || lines > maxLines)
							fireMessage(inMessage.getChatId(), "Могу передать не более "+maxLines+" строк и не менее 1");
						else fireMessage(inMessage.getChatId(), getLog(lines));
					} catch (NumberFormatException ex) {
						fireMessage(inMessage.getChatId(), "Неверный формат команды");
					}
				}
			}
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

	private Message getMessage(Update update) {
		if(update.hasChannelPost() && update.getChannelPost().hasText())
			return update.getChannelPost();
		if(update.hasMessage() && update.getMessage().hasText())
			return update.getMessage();
		return null;
	}

	public void fireDocument(Long chanelId, File file) {
		try {
			SendDocument outMessage = new SendDocument();
			outMessage.setChatId(chanelId);
			outMessage.setDocument(file);
			execute(outMessage);
		} catch (TelegramApiException e) {
			logger.error("Не получилось отправить сообщение в канал Риф ошибочки ", e);
		}
	}

	public void fireMessage(Long chanelId, String msg) {
		try {
			SendMessage outMessage = new SendMessage();
			outMessage.enableHtml(true);
			outMessage.setChatId(chanelId);
			outMessage.setText("<b>"+getBotUsername()+"</b>: \n\n"+msg);
			execute(outMessage);
		} catch (TelegramApiException e) {
			logger.error("Не получилось отправить сообщение в канал Риф ошибочки ", e);
		}
	}

	public String getBotUsername() {
		return "RusicoBot";
	}

	public String getBotToken() {
		return token;
	}

*/


}
