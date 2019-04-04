package club;

import club.bot.Bot;
import club.model.AccountRefillLog;
import club.model.RifLog;
import club.repository.RifLogsRepository;
import club.service.AccountRefillService;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import static org.apache.commons.lang3.StringUtils.isBlank;


/**
 * Контроллер, курирующий пополнение карты.
 */
@Controller
public class P2PController {

	@Value("${DBlogging}")
	private Boolean dbLogMode;
	@Value("${DBValidating}")
	private Boolean dbValidationMode;
	@Value("${urlForBank}")
	private String urlForBank;
	@Value("${send.push}") //Отключаем все пуши партнеру
	private Boolean sendPush;


	/**
	 * Логгеры
	 */
	private static Logger logger = LoggerFactory.getLogger(P2PController.class); // в файл
	private static Logger consoleLogger = LoggerFactory.getLogger("console_logger"); //консольный

	private String bank_url = "https://rif.creditural.ru";
	private String service = "registerorderfromcardtocard";

	private AccountRefillService dbService; // Сервис, для управления взаимодействием с БД в плане пополнения карты
	private RifLogsRepository rifRepository; // Репозиторий для валидации EAN/RUNET-ID

	private RifRestTool rifRestTool;
	private RifRestTool ignoreSSLRifRestTool;

	private Bot bot;
	private final ExecutorService pool = Executors.newFixedThreadPool(5);

	@Autowired
	public P2PController(AccountRefillService dbService,
						 RifLogsRepository rifRepository,
						 @Qualifier("SimpleRifRestTool") RifRestTool rifRestTool,
						 @Qualifier("IgnoreSSLRifRestTool") RifRestTool ignoreSSLRifRestTool,
						 Bot bot) {
		this.dbService = dbService;
		this.rifRepository = rifRepository;
		this.rifRestTool = rifRestTool;
		this.ignoreSSLRifRestTool = ignoreSSLRifRestTool;
		this.bot = bot;
	}

	@RequestMapping(value = "/p2p/test", method = {RequestMethod.GET, RequestMethod.POST})
	private String testp2p() {
		logger.warn("TEST-warn");
		logger.info("TEST-info");
		logger.error("TEST-error");
		consoleLogger.info("TEST log to console");
		return "testp2p";
	}

	/**
	 * Основной метод, который дергает Рунет для пополнения карты
	 * @param runetId
	 * @param Partner
	 * @param Service
	 * @param ToEAN
	 * @param Amount
	 * @param ReturnUrl
	 * @param request
	 * @param model
	 */
	@RequestMapping(value = "/p2p", method = {RequestMethod.GET, RequestMethod.POST})
	private String p2p(
			@RequestParam(required = false, defaultValue = "123456", value = "runet-id") String runetId,
			@RequestParam(required = false, defaultValue = "RIF", value = "Partner") String Partner,
			@RequestParam(required = false, defaultValue = "registerorderfromcardtocard", value = "Service") String Service,
			@RequestParam(required = false, defaultValue = "1000101593509", value = "ean") String ToEAN,
			@RequestParam(required = false, defaultValue = "100", value = "amount") long Amount,
			@RequestParam(required = false, defaultValue = "https://2019.rif.ru/", value = "url") String ReturnUrl,
			HttpServletRequest request,
			Model model) {


		/** Логгируем то, что пришло от партнера
		 *
        */

		consoleLogger.info("########### P2P ВХОДЩИЙ ЗАПРОС ###########");
		consoleLogger.info("runet-id:   " + runetId);
		consoleLogger.info("Partner:   " + Partner);
		consoleLogger.info("Service:   " + Service);
		consoleLogger.info("ean:   " + ToEAN);
		consoleLogger.info("amount:   " + Amount);
		consoleLogger.info("url:   " + ReturnUrl);




		/**
		 * Общий отлавливатель ошибок. Если во внутренних эксепшенах что-то падает, это все пробрасывается сюда и мы уже
		 * на фронте (со странички passport) редиректим на страничку error и там показываем ошибку.
		 *
		 */

		try {

			Long validation = null; // переменная для валидации RUNETID и EAN
			final String orderId = generateOrderId();

			// Проверка валидности Orderid и EAN
			if (dbValidationMode) {
				validation = rifRepository.validateP2P(runetId, ToEAN);
				if(validation == 0) {
					consoleLogger.debug("Связка RUNET-ID, EAN не валидна ["+runetId+":"+ToEAN+"]");
					logger.debug("Связка RUNET-ID, EAN не валидна ["+runetId+":"+ToEAN+"]");
					throw new Exception("[RUNET-ID:"+runetId+"] Ошибка входных параметров");
				}
			} else validation = 1L;

			consoleLogger.info("########### VALIDATION ###########");
			consoleLogger.info("ЗАПИСЕЙ НАЙДЕНО:   " + validation);

			if (dbLogMode) {
				try {
					dbService.add(new AccountRefillLog(runetId, ToEAN, Amount, null, orderId, null, null, ReturnUrl));
				} catch (Exception ex) {
					throw new Exception("[RUNET-ID:"+runetId+"] Ошибка при логгировании данных в базу данных", ex);
				}
			}

			HttpHeaders headers = ignoreSSLRifRestTool.createHeaderWithoutToken(MediaType.APPLICATION_JSON);
			headers.add("HOST", "rif.creditural.ru");

			try {

				String scheme = request.getHeader("X-Forwarded-Proto");
				String url = request.getRequestURL().toString().substring(0,request.getRequestURL().toString().lastIndexOf("/"))+"/absrequest";
				if(scheme != null)
					url = scheme+url.substring(request.getScheme().length());


				JsonObject res = ignoreSSLRifRestTool.getResponse(
						urlForBank,
						HttpMethod.POST,
						headers,
						JSONTemplate.create()
								.add("Service", Service)
								.add("OrderID", orderId)
								.add("Amount", Amount)
								//.add("ReturnUrl", request.getRequestURL().toString().substring(0,request.getRequestURL().toString().lastIndexOf("/"))+"/absrequest")
								.add("ReturnUrl", url)
								.add("ToEAN", ToEAN)
								.add("Partner", Partner).toString());

				return "redirect:" + res.get("FormURL").getAsString();
			}catch (Exception ex) {
				throw new Exception("[RUNET-ID:"+runetId+"] Внутренняя ошибка системы",ex);
			}
		} catch (Exception ex) {
			return errorPage(model, ex, request.getHeader("Referer"));
		}
	}

	private String errorPage(Model model, Exception ex, String redirectUrl) {
		redirectUrl = redirectUrl == null ? "http://yandex.ru" : redirectUrl;
		logger.error(ex.getMessage(), ex);
		consoleLogger.error(ex.getMessage(), ex);
		model.addAttribute("fatalError", ex.getMessage());
		model.addAttribute("errorMessage", ex.getMessage());
		model.addAttribute("redirectUrl", redirectUrl);
		model.addAttribute("startUrl", redirectUrl);
		pool.submit(() -> bot.fireMessage("P2P: "+ex.getMessage()));
		return "passport";
	}

	@PreDestroy
	private void close() {
		pool.shutdown();
	}



	/**
	 * Получаем пуш от банка
	 *
	 * @param pushFromAbs
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/absrequest", method = {RequestMethod.POST, RequestMethod.GET})
	public String postController(
			//@RequestBody(required = false) String body,
			@RequestParam(required = false, value = "name") String pushFromAbs,
			HttpServletRequest request,
			HttpServletResponse resp,
			Model model) {

		String redirectUrl = ""; // url редиректа, который вытащим из БД и перенаправим юзера
		String runetId = ""; // runet-id, который вытащим из БД, чтобы сделать пуш
		String ean = ""; // ean, который вытащим из БД, чтобы сделать пуш
		HashMap<String, String> hashMapwithParametrs = new HashMap<>();

		try {
			//consoleLogger.info("ALL BODY"+(body == null ? "null" : body.toString()));
			//logger.info("ALL BODY"+(body == null ? "null" : body.toString()));

			consoleLogger.info("ABSREQUEST BODY - " + pushFromAbs);
			logger.warn("ABSREQUEST BODY - " + pushFromAbs);
			String requestParams = null;

			// Парсим orderInfo
			requestParams = decodeRequest(pushFromAbs);
			hashMapwithParametrs = logABSPush(requestParams);

			/**
			 * Проверка переключателя в application.properties (DBlogging = true).
			 * Если он выставлен в false, то никаких операций с базой не делаем по всему коду вообще.
 			 */
			if (dbLogMode) {
				try {

					logger.info("Ищем URL (и остальные параметры) по ORDERID = " +hashMapwithParametrs.get("ORDERID") );
					consoleLogger.info("Ищем URL  (и остальные параметры)  по ORDERID = " +hashMapwithParametrs.get("ORDERID") );

					//Запрашиваем ранее сохраненный в бд для данного OrderId URL-а, куда мы будем редеректить после АБС клиента
					redirectUrl = dbService.getUrlByOrderId(hashMapwithParametrs.get("ORDERID"));

					logger.info("Нашли URL  в БД = " + redirectUrl);
					consoleLogger.info("Нашли URL  в БД = " + redirectUrl);


					/**
					 * Запрашиваем ранее сохраненный в бд Runet-Id для данного OrderId URL-а,
					 *  куда мы будем редеректить после АБС клиента.
					 */
					runetId = dbService.getRunetIdByOrderId(hashMapwithParametrs.get("ORDERID"));

					logger.info("Нашли runet-id  в БД = " + runetId);
					consoleLogger.info("Нашли runet-id  в БД = " + redirectUrl);

					/**
					 *Запрашиваем ранее сохраненный в бд EAN для данного OrderId URL-а,
					 * куда мы будем редеректить после АБС клиента.
					 *
					 */
					ean = dbService.getEanByOrderId(hashMapwithParametrs.get("ORDERID"));

					logger.info("Нашли ean  в БД = " + ean);
					consoleLogger.info("Нашли ean  в БД = " + ean);

					if ((redirectUrl==null) || (runetId==null) || (ean==null)){
						throw new Exception("[RUNET-ID:"+runetId+"] Ошибка базы данных: запрос redirectUrl, runetID или EAN ил БД вернул ноль");
					}

				} catch (Exception ex) {
					logger.error("Ошибка базы данных при запросе ранее сохраненного  URL редиректа и других параметров", ex);
					consoleLogger.error("Ошибка базы данных при запросе ранее сохраненного  URL редиректа и других параметров", ex);
					throw new Exception("[RUNET-ID:"+runetId+"] Внутренняя ошибка системы",ex);
				}
			} else {
				redirectUrl = "http://yandex.ru";
			}


			/**
			 * Обновляем запись в БД новыми данными, пришедшими от АБС.
			 */
			if (dbLogMode) { // делаем это, только если отладный переключатель DBlogging = true
				try {
					// Проставляем текущее время сервера
					Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
					// Обновляем запись в БД
					dbService.updateDBEntry(
							hashMapwithParametrs.get("ORDERID"),
							hashMapwithParametrs.get("STATE"),
							hashMapwithParametrs.get("STATEDESCRIPTION"),
							currentTimestamp);

				} catch (Exception ex) {
					logger.error("Ошибка при логгировании полученных от банка данных в базу данных", ex);
					consoleLogger.error("Ошибка при логгировании полученных от банка данных в базу данных", ex);
					throw new Exception("[RUNET-ID:"+runetId+"] Внутренняя ошибка системы",ex);
				}
			}


			/**
			 * Отправляем Пуш партнеру
			 */


			/**
			 * 27.03.2019 @ant_ch попросил сделать проверку на DECLINED раньше вызова метода отправки Пуша. И если банк
			 * нам вернул DECLINED пуш вообще не дергать и в лог кидать другое
			 */

			if (hashMapwithParametrs.get("STATE").equals("APPROVED")) {

				// Для стейджа надо было отключить пуши на партнера (включение через application.properties)
				if (sendPush) {
					pushAfterRefillAccount(hashMapwithParametrs.get("STATE"), runetId, ean, hashMapwithParametrs.get("AMOUNT"));
				} else {
					consoleLogger.warn("Пуши выключены - не отправляем");
				}

			} else {
				// Иначе кидаем в лог, что у нас Деклайн, ничего не дергаем, никаких пушей не шлем:

				consoleLogger.info("########### STATE:  DECLINED ###########");
				consoleLogger.info("runetId:   " + runetId);
				consoleLogger.info("ean:   " + ean);

				MDC.put("runetid", runetId);
				MDC.put("ean", ean);

				logger.info(JSONTemplate.create()
						.add("stage", "P2P")
						.add("method", "postController {/absrequest}")
						.add("runetId", runetId)
						.add("ean", ean)
						.add("state", "declibed").toString());

				MDC.remove("runetid");
				MDC.remove("ean");

			}

		} catch(NullPointerException ex){
			return errorPage(model, new Exception("Ошибка входных параметров"), redirectUrl == null || "".equals(redirectUrl) ? "https://2019.rif.ru/" : redirectUrl);
		} catch (Exception ex) {
			if (!hashMapwithParametrs.containsKey("STATE") || !hashMapwithParametrs.get("STATE").equals("APPROVED"))
				return errorPage(model, ex, redirectUrl == null || "".equals(redirectUrl) ? "https://2019.rif.ru/" : redirectUrl);
			logger.error(ex.getMessage(), ex);
			consoleLogger.error(ex.getMessage(), ex);
		}


		if (hashMapwithParametrs.get("STATE").equals("APPROVED")){

			model.addAttribute("okMessage","Перевод совершен успешно!");
			model.addAttribute("p2pOkMessage","Перевод совершен успешно!");
			model.addAttribute("p2pOkAmount","Сумма перевода: " + hashMapwithParametrs.get("AMOUNT"));
			model.addAttribute("p2pOkDateTime","Дата/Время перевода: " + hashMapwithParametrs.get("DATETIME"));
			model.addAttribute("p2pOkToCardNumber","Карта пополнения: " +hashMapwithParametrs.get("TOCARDNUMBER"));
			String finalRedirectUrl = redirectUrl == null ? "https://2019.rif.ru/": redirectUrl;
			model.addAttribute("redirectUrl", finalRedirectUrl);
			model.addAttribute("startUrl", finalRedirectUrl);

		} else {
			model.addAttribute("errorMessage", "Ошибка при совершении перевода!");
			model.addAttribute("fatalError", "Ошибка при совершении перевода!");


			String finalRedirectUrl = redirectUrl == null ? "https://2019.rif.ru/": redirectUrl;
			model.addAttribute("redirectUrl", finalRedirectUrl);
			model.addAttribute("startUrl", finalRedirectUrl);

		}

		return "passport";
	}

	private String generateOrderId() {
		return "P2P-" + String.valueOf(System.currentTimeMillis());
	}

	/**
	 * Выбросить удачный пуш на партнера, после пополнения счета.
	 *
	 * @throws Exception
	 */
	private void pushAfterRefillAccount(String state, String runetId, String ean, String amount) throws Exception {

		String clientId;

		consoleLogger.info("########### PUSH TO PARTNER ###########");
		consoleLogger.info("runetId:   " + runetId);
		consoleLogger.info("ean:   " + ean);
		consoleLogger.info("amount:   " + amount);

		MDC.put("runetid", runetId);
		MDC.put("ean", ean);

		logger.info(JSONTemplate.create()
				.add("stage", "P2P")
				.add("method", "pushAfterRefillAccount")
				.add("methodDescription", "PUSH TO PARTNER")
				.add("runetId", runetId)
				.add("ean", ean)
				.add("amount", amount).toString());

		MDC.remove("runetid");
		MDC.remove("ean");


		/**
		 * Вытаскиваем client-id
		 * Берем первую запись, а вообще там должна быть только одна
		 */
		Optional<RifLog> onRifLog = rifRepository.getRifLogEntryBuRunetAndEan(runetId, ean).stream().findFirst();

		try {
			/**
			 * Если БД энтри нулевая и client-id не ноль - бросаем эксепшн
			 */
			if ((onRifLog.isPresent()) && (!isBlank(onRifLog.get().getClientID()))) {

				clientId = onRifLog.get().getClientID();
				consoleLogger.info("CLIENT ID:   " + clientId);
				logger.info("CLIENT ID:   " + clientId);

			} else {
				consoleLogger.info("CLIENT ID:   DB ERROR");
				logger.info("CLIENT ID:   DB ERROR");
				throw new Exception();

			}

		} catch (Exception ex) {
			throw new Exception("Ошибка при получении ранее сохраненного clientId в БД", ex);
		}

		try {

			if (state.equals("APPROVED")) {
				rifRestTool.pushToRunet(JSONTemplate.create()
						.add("TYPE", "P2P")
						.add("RUNET-ID", runetId)
						.add("client_id", clientId)
						.add("ean", ean)
						.add("amount", amount).getJson());
				consoleLogger.info("RESULT:   SUCCESS ");
				logger.info("RESULT:   SUCCESS ");
			}
		} catch (Exception ex) {
			logger.error("Ошибка отправки PUSH партнёру о подтверждении пополнения счета", ex);
			consoleLogger.error("Ошибка отправки PUSH партнёру о подтверждении пополнения счета", ex);
			throw new Exception("Внутренняя ошибка системы при отправке удачного пуша партнеру",ex);
		}
	}

	private String decodeRequest(String value) throws UnsupportedEncodingException {

		//String code = "eyJvcmRlckluZm8iOnsib3JkZXJJRCI6IlAyUC0xNTUzMTY3Njk1NjczIiwib3JkZXJOdW1iZXIiOiIxIiwic3RhdGUiOiJBUFBST1ZFRCIsInN0YXRlRGVzY3JpcHRpb24iOiJcdTA0MjNcdTA0MzRcdTA0MzBcdTA0NDdcdTA0M2RcdTA0M2VcdTA0MzUgXHUwNDMyXHUwNDRiXHUwNDNmXHUwNDNlXHUwNDNiXHUwNDNkXHUwNDM1XHUwNDNkXHUwNDM4XHUwNDM1IFx1MDQ0Mlx1MDQ0MFx1MDQzMFx1MDQzZFx1MDQzN1x1MDQzMFx1MDQzYVx1MDQ0Nlx1MDQzOFx1MDQzOCIsInR5cGUiOiJcdTA0MWZcdTA0MzVcdTA0NDBcdTA0MzVcdTA0MzJcdTA0M2VcdTA0MzQiLCJhbW91bnQiOiIxLjAwIiwiZGF0ZVRpbWUiOiIyMVwvMDNcLzIwMTkgMTY6Mjg6NDkifSwib3JkZXJBZGRpdGlvbmFsSW5mbyI6eyJDYXJkUmVmSUQiOm51bGwsIkNhcmRFeHBZZWFyIjoiIiwiQ2FyZEV4cE1vbnRoIjoiIiwiQ2FyZElzc3VpbmdCYW5rIjoiIiwiQ2FyZEJyYW5kIjoiVklTQSIsIkNhcmRUeXBlIjoiIiwiQ2FyZExldmVsIjoiIiwiTGFzdFN0YXRlRGF0ZSI6IiIsIkNhcmROdW1iZXIiOiI0MDgzOTdYWFhYWFg1NTc5IiwiQ2FyZE51bWJlckhhc2giOiIiLCJDYXJkSG9sZGVyIjoiIiwiVG9DYXJkTnVtYmVyIjoiNDE5OTk5WFhYWFhYNTU4MiJ9fQ%3D%3D";
		String result = null;

		logger.info("СТРОКА ОТ БАНКА В BASE64:   " + value);
		consoleLogger.warn("СТРОКА ОТ БАНКА В BASE64:   " + value);

		//try {

		//	value = code;
			String urlDecode = java.net.URLDecoder.decode(value, StandardCharsets.UTF_8.name());
			byte[] bb = Base64Utils.decodeFromString(urlDecode);
			result = new String(bb);
			consoleLogger.warn("############ DECODE STRING ##################");
			logger.info("DECODE RESULT:   " + result);
			consoleLogger.warn("RESULT:   " + result);


		//} catch (UnsupportedEncodingException e) {
		//}
		return result;
	}

	private String decodeJsonPartinUTF(JsonObject requestJsonPart, String name) {

		if (requestJsonPart.get(name) != null) {

			return new String(requestJsonPart.get(name).getAsString().getBytes(Charset.forName("UTF-8")));

		} else {
			return "";
		}
	}


	private HashMap<String, String> logABSPush(String requestJson) {

		HashMap<String, String> hashMap = new HashMap<>();

		// Парсим OrderInfo:
		JsonObject orderInfo = JSONTemplate.fromString(requestJson).get("orderInfo").getAsJsonObject();
		String orderId = decodeJsonPartinUTF(orderInfo, "orderID");
		hashMap.put("ORDERID", orderId);
		String orderNumber = decodeJsonPartinUTF(orderInfo, "orderNumber");
		String state = decodeJsonPartinUTF(orderInfo, "state");
		hashMap.put("STATE", state);
		String stateDescription = decodeJsonPartinUTF(orderInfo, "stateDescription");
		hashMap.put("STATEDESCRIPTION", stateDescription);
		String type = decodeJsonPartinUTF(orderInfo, "type");
		String amount = decodeJsonPartinUTF(orderInfo, "amount");
		hashMap.put("AMOUNT", amount);
		String dateTime = decodeJsonPartinUTF(orderInfo, "dateTime");
		hashMap.put("DATETIME", dateTime);



		// Парсим orderAdditionalInfo:
		JsonObject orderAdditionalInfo = JSONTemplate.fromString(requestJson).get("orderAdditionalInfo").getAsJsonObject();
		String cardRefID = decodeJsonPartinUTF(orderAdditionalInfo, "cardRefID");
		String cardExpYear = decodeJsonPartinUTF(orderAdditionalInfo, "cardExpYear");
		String cardExpMonth = decodeJsonPartinUTF(orderAdditionalInfo, "cardExpMonth");
		String cardIssuingBank = decodeJsonPartinUTF(orderAdditionalInfo, "cardIssuingBank");
		String cardBrand = decodeJsonPartinUTF(orderAdditionalInfo, "cardBrand");
		String cardType = decodeJsonPartinUTF(orderAdditionalInfo, "cardType");
		String cardLevel = decodeJsonPartinUTF(orderAdditionalInfo, "cardLevel");
		String lastStateDate = decodeJsonPartinUTF(orderAdditionalInfo, "lastStateDate");
		String cardNumber = decodeJsonPartinUTF(orderAdditionalInfo, "cardNumber");
		String cardNumberHash = decodeJsonPartinUTF(orderAdditionalInfo, "cardNumberHash");
		String cardHolder = decodeJsonPartinUTF(orderAdditionalInfo, "cardHolder");
		String toCardNumber = decodeJsonPartinUTF(orderAdditionalInfo, "ToCardNumber");
		hashMap.put("TOCARDNUMBER", toCardNumber);


		consoleLogger.debug("########### PUSH FROM ABS ###########");

		consoleLogger.info("++++++++++++++++++++++++ orderInfo часть +++++++++++++++++++++++++++");
		consoleLogger.info("orderId:   " + orderId);
		consoleLogger.info("orderNumber:   " + orderNumber);
		consoleLogger.info("state:   " + state);
		consoleLogger.info("stateDescri:   " + stateDescription);
		consoleLogger.info("type:   " + type);
		consoleLogger.info("amount:   " + amount);
		consoleLogger.info("dateTime:   " + dateTime);
		consoleLogger.info("++++++++++++++++++++++++ orderAdditionalInfo часть +++++++++++++++++++++++++++");
		consoleLogger.info("cardRefID:   " + cardRefID);
		consoleLogger.info("cardExpYear:   " + cardExpYear);
		consoleLogger.info("cardExpMonth:   " + cardExpMonth);
		consoleLogger.info("cardIssuingBank:   " + cardIssuingBank);
		consoleLogger.info("cardBrand:   " + cardBrand);
		consoleLogger.info("cardType:   " + cardType);
		consoleLogger.info("cardLevel:   " + cardLevel);
		consoleLogger.info("lastStateDate:   " + lastStateDate);
		consoleLogger.info("cardNumber:   " + cardNumber);
		consoleLogger.info("cardNumberHash:   " + cardNumberHash);
		consoleLogger.info("cardHolder:   " + cardHolder);
		consoleLogger.info("toCardNumber:   " + toCardNumber);

		MDC.put("ORDERID", orderId);

		logger.info(JSONTemplate.create()
				.add("stage","P2P")
				.add("method","absrequest")
				.add("orderId", orderId)
				.add("orderNumber",orderNumber)
				.add("state",state)
				.add("stateDescription",stateDescription)
				.add("type",type)
				.add("amount",amount)
				.add("dateTime",dateTime)
				.add("cardRefID",cardRefID)
				.add("cardExpYear",cardExpYear)
				.add("cardExpMonth",cardExpMonth)
				.add("cardIssuingBank",cardIssuingBank)
				.add("cardBrand",cardBrand)
				.add("cardType",cardType)
				.add("cardLevel",cardLevel)
				.add("lastStateDate",lastStateDate)
				.add("cardNumber",cardNumber)
				.add("cardNumberHash",cardNumberHash)
				.add("cardHolder",cardHolder)
				.add("toCardNumber",toCardNumber).toString());

		MDC.remove("ORDERID");

		return hashMap;
	}


}