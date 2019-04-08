package com.antonromanov.temprest.bot;

//@Component
public class Bot2  {
//public class Bot2 extends TelegramLongPollingBot {
/*
	private static final Logger logger = LoggerFactory.getLogger(Bot2.class);
	private ExecutorService pool = Executors.newCachedThreadPool();

	@Value("${telegram.bot.token}")
	private String token;

	@Value("${telegram.bot.user.name}")
	private String username;

	@Override
	public String getBotToken() {
		return token;
	}

	@Override
	public String getBotUsername() {
		return username;
	}


	*//*private void fireFielLog(Long chartId) {
		pool.submit(() -> {

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
		}
	*//*

	*//*public void fireMessage(String msg) {
		fireMessage(chanelId, msg);
	}*//*

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

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage()) {
			Message message = update.getMessage();



			if(message != null && message.getText().startsWith("/")) {
				String command = message.getText().toLowerCase();
				if (command.startsWith("/status")) {

					fireMessage(message.getChatId(), "Здесь будет JSON");

				}
			}




			SendMessage response = new SendMessage();
			Long chatId = message.getChatId();
			response.setChatId(chatId);
			String text = message.getText();
			response.setText(text + " - ТЫ ОХУЕЛ!!!");
			try {
				execute(response);
				logger.info("Sent message \"{}\" to {}", text, chatId);
			} catch (TelegramApiException e) {
				logger.error("Failed to send message \"{}\" to {} due to error: {}", text, chatId, e.getMessage());
			}
		}
	}

	@PostConstruct
	public void start() {
		logger.info("username: {}, token: {}", username, token);
	}*/

}
