package club;

import club.bot.Bot;
import club.model.Recognitions;
import club.model.RifLog;
import club.repository.RecognitionsCountControlRepository;
import club.repository.RifLogsRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Основной контроллер, отвечающий за выпуск карты/открытие счета
 */
@Controller
@ControllerAdvice
@Scope("session")
public class RifController {
	private final ExecutorService pool = Executors.newFixedThreadPool(5);
	private final Map<String, String> modelData = new HashMap<>();
	private MultipartFile fileForDB = null;
	private byte[] byteFromFile;

	/**
	 * CrudRepository, отвечающий за запись/чтение логов при выпуске карты
	 */
	private RifLogsRepository rifRepository;

	/**
	 * CrudRepository, отвечающий за контроль кол-ва распознаваний.
	 */
	private RecognitionsCountControlRepository recognitionsCountControlRepository;

	/**
	 * Сервисный класс, отвечающий за основной "подкапотный" функционал: отправку основных запросов-проверок,
	 * пуши, работу с токенами.
	 */
	private RifRestTool rifRestTool;

	@Autowired
	private Environment env;

	@Value("${testRunetId}")
	private String testRunetId;

	@Value("${DBlogging}")
	private Boolean dbLogMode;

	@Value("${send.push}") //Отключаем все пуши партнеру
	private Boolean sendPush;

	@Value("${recognitions.max}") //Максимальное кол-во распознаваний
	private Integer maxRecognitions;

	@Value("${runet.id.max}") //Максимально возможное Runet-id
	private Integer maxRunetId;


	private Bot bot;

	/**
	 * Логгеры
	 */
	private static Logger logger = LoggerFactory.getLogger(RifController.class);
	private static Logger consoleLogger = LoggerFactory.getLogger("console_logger");

	@Autowired
	public RifController(RifLogsRepository rifRepository, RecognitionsCountControlRepository recognitionsCountControlRepository, @Qualifier("SimpleRifRestTool") RifRestTool rifRestTool, Bot bot) {
		this.rifRepository = rifRepository;
		this.rifRestTool = rifRestTool;
		this.bot = bot;
		this.recognitionsCountControlRepository = recognitionsCountControlRepository;
	}

	@RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
	private String zeropoint() {
		return "redirect:/test";
	}

	@RequestMapping(value = "/test", method = {RequestMethod.GET, RequestMethod.POST})
	private String test() {
		logger.info("TEST");
		return "welcome";
	}


	@RequestMapping(value = "/passportproblem", method = {RequestMethod.POST})
	private void passportProblem() {
		String passport = "passportproblem";
		consoleLogger.info("PASSPORT PROBLEM");


//        JsonObject pass = "{\"result\":true,\"id\":\"1f9ffef7-f292-446d-9cfe-a931243fa16e\",\"data\":{\"documentType\":\"rus.passport.national\",\"stringFields\":[{\"name\":\"authority\",\"utf8value\":\"УФМС РОССИИ ПО Г . СЕВАСТОПОЛЮ\",\"accepted\":true},{\"name\":\"authority_code\",\"utf8value\":\"920-001\",\"accepted\":true},{\"name\":\"authority_code_mrz\",\"utf8value\":\"920-001\",\"accepted\":true},{\"name\":\"birth_date_mrz\",\"utf8value\":\"18.04.1986\",\"accepted\":true},{\"name\":\"birthdate\",\"utf8value\":\"18.04.1986\",\"accepted\":true},{\"name\":\"birthplace\",\"utf8value\":\"Г. СЕВАСТОПОЛЬ УССР.\",\"accepted\":true},{\"name\":\"full_mrz\",\"utf8value\":\"PNRUSSELIVANOV<<NIKOLAQ<ANATOL9EVI3<<<<<<<<<0912573164RUS8604183M<<<<<<<4140827920001<62\",\"accepted\":false},{\"name\":\"gender\",\"utf8value\":\"МУЖ.\",\"accepted\":true},{\"name\":\"gender_mrz\",\"utf8value\":\"МУЖ.\",\"accepted\":true},{\"name\":\"issue_date\",\"utf8value\":\"27.08.2014\",\"accepted\":true},{\"name\":\"issue_date_mrz\",\"utf8value\":\"27.08.2014\",\"accepted\":true},{\"name\":\"name\",\"utf8value\":\"НИКОЛАЙ\",\"accepted\":true},{\"name\":\"name_mrz\",\"utf8value\":\"НИКОЛАЙ\",\"accepted\":true},{\"name\":\"number\",\"utf8value\":\"257316\",\"accepted\":true},{\"name\":\"number_mrz\",\"utf8value\":\"257316\",\"accepted\":false},{\"name\":\"patronymic\",\"utf8value\":\"АНАТОЛЬЕВИЧ\",\"accepted\":true},{\"name\":\"patronymic_mrz\",\"utf8value\":\"АНАТОЛЬЕВИЧ\",\"accepted\":true},{\"name\":\"series\",\"utf8value\":\"0914\",\"accepted\":true},{\"name\":\"series_mrz\",\"utf8value\":\"0914\",\"accepted\":false},{\"name\":\"surname\",\"utf8value\":\"СЕЛИВАНОВ\",\"accepted\":true},{\"name\":\"surname_mrz\",\"utf8value\":\"СЕЛИВАНОВ\",\"accepted\":true}],\"terminal\":false}}";
		String pass = "{\"result\":true,\"id\":\"1f9ffef7-f292-446d-9cfe-a931243fa16e\",\"data\":{\"documentType\":\"rus.passport.national\",\"stringFields\":[{\"name\":\"authority\",\"utf8value\":\"УФМС РОССИИ ПО Г . СЕВАСТОПОЛЮ\",\"accepted\":true},{\"name\":\"authority_code\",\"utf8value\":\"920-001\",\"accepted\":true},{\"name\":\"authority_code_mrz\",\"utf8value\":\"920-001\",\"accepted\":true},{\"name\":\"birth_date_mrz\",\"utf8value\":\"18.04.1986\",\"accepted\":true},{\"name\":\"birthdate\",\"utf8value\":\"18.04.1986\",\"accepted\":true},{\"name\":\"birthplace\",\"utf8value\":\"Г. СЕВАСТОПОЛЬ УССР.\",\"accepted\":true},{\"name\":\"full_mrz\",\"utf8value\":\"PNRUSSELIVANOV<<NIKOLAQ<ANATOL9EVI3<<<<<<<<<0912573164RUS8604183M<<<<<<<4140827920001<62\",\"accepted\":false},{\"name\":\"gender\",\"utf8value\":\"МУЖ.\",\"accepted\":true},{\"name\":\"gender_mrz\",\"utf8value\":\"МУЖ.\",\"accepted\":true},{\"name\":\"issue_date\",\"utf8value\":\"27.08.2014\",\"accepted\":true},{\"name\":\"issue_date_mrz\",\"utf8value\":\"27.08.2014\",\"accepted\":true},{\"name\":\"name\",\"utf8value\":\"НИКОЛАЙ\",\"accepted\":true},{\"name\":\"name_mrz\",\"utf8value\":\"НИКОЛАЙ\",\"accepted\":true},{\"name\":\"number\",\"utf8value\":\"257316\",\"accepted\":true},{\"name\":\"number_mrz\",\"utf8value\":\"257316\",\"accepted\":false},{\"name\":\"patronymic\",\"utf8value\":\"АНАТОЛЬЕВИЧ\",\"accepted\":true},{\"name\":\"patronymic_mrz\",\"utf8value\":\"АНАТОЛЬЕВИЧ\",\"accepted\":true},{\"name\":\"series\",\"utf8value\":\"0914\",\"accepted\":true},{\"name\":\"series_mrz\",\"utf8value\":\"0914\",\"accepted\":false},{\"name\":\"surname\",\"utf8value\":\"СЕЛИВАНОВ\",\"accepted\":true},{\"name\":\"surname_mrz\",\"utf8value\":\"СЕЛИВАНОВ\",\"accepted\":true}],\"terminal\":false}}";


		consoleLogger.info(JSONTemplate.create()
				.add("RESPONSE", pass == null ? "null" : pass.toString()).toString());


		JsonObject responseJson = JSONTemplate.fromString(pass);

		try {
			Map<String, String> map = getPassportData(responseJson);
			modelData.putAll(getPassportData(responseJson));

		} catch (Exception e) {
			e.printStackTrace();
		}
		//modelData.putAll(getPassportData(pass));
		RifLog rifLog = new RifLog(modelData);


		try {

			//   RifLog rifLog = new RifLog(modelData);

			rifRepository.save(rifLog); // сохраняем в БД
		} catch (Exception ex) {
			// Логгируем
			consoleLogger.error("Произошла ошибка сохранения в БД: " + ex.getMessage(), ex);
			consoleLogger.error("Пытаемся записать следующие данные: " + new Gson().toJson(modelData));
			logger.error("Произошла ошибка сохранения в БД: " + ex.getMessage(), ex);
			logger.error("Пытаемся записать следующие данные: " + new Gson().toJson(modelData));
		}
	}


	/**
	 * Главный принимаюющий контроллер.
	 *
	 * @param runetId     - принимает RUNET-ID От партнера
	 * @param phoneNumber - телефон клиента
	 * @param redirectUrl - URL, на который после всех обработок нужно редеректить клиента
	 * @param request     - сервисный реквест.
	 * @param res         - сервисный респонс
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/virtual_card", method = {RequestMethod.GET, RequestMethod.POST})
	private String virtual(
			@RequestParam(required = false, value = "RUNET-ID") String runetId,
			@RequestParam(required = false, value = "PHONE") String phoneNumber,
			@RequestParam(required = false, value = "URL") String redirectUrl,
			HttpServletRequest request,
			HttpServletResponse res,
			Model model) {

		if (!checkMaxRunetId(runetId)){

			modelData.clear();
		model.addAttribute("firstPage", true);

		phoneNumber = phoneNumber.replaceAll("[^\\d]]", "");

		phoneNumber = phoneNumber.length() != 10 ? "+7" : ("+7" + phoneNumber);

		try {
			validStartData(runetId, phoneNumber, redirectUrl);
			modelData.putIfAbsent("runetId", runetId);
			modelData.putIfAbsent("redirectUrl", redirectUrl);
			modelData.putIfAbsent("phoneNumber", phoneNumber);
			modelData.putIfAbsent("startUrl", request.getHeader("Referer"));
			logger.info("Принял парамметры для заполнения анкеты " + modelData);
			consoleLogger.info("Принял парамметры для заполнения анкеты " + modelData);

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			consoleLogger.error(ex.getMessage(), ex);
			modelData.put("redirectUrl", "https://yandex.ru/search/?text=%D0%BA%D0%B0%D0%BA%20%D0%B6%D0%B8%D1%82%D1%8C%20%D0%B4%D0%B0%D0%BB%D1%8C%D1%88%D0%B5&lr=213");
			modelData.putIfAbsent("fatalError", ex.getMessage());
			modelData.putIfAbsent("errorMessage", ex.getMessage());
		}
		model.addAllAttributes(modelData);
		return "passport";
	} else {

			logger.error("Клиент превысил max runet-id ");
			consoleLogger.error("Клиент превысил max runet-id ");
			modelData.putIfAbsent("fatalError", "Извините, мы не можем выпустить Вам карту!");
			modelData.putIfAbsent("errorMessage", "Извините, мы не можем выпустить Вам карту!");
			modelData.put("redirectUrl", modelData.get("startUrl"));
			model.addAllAttributes(modelData);
			return "passport";

		}
	}


	/**
	 * Выпустить пластиковую карту.
	 * <p>
	 * ЭТАП 2.
	 *
	 * @param runetId
	 * @param redirectUrl
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/card", method = {RequestMethod.GET, RequestMethod.POST})
	private String plastic(
			@RequestParam(required = false, value = "runet-id") String runetId,
			@RequestParam(required = false, value = "url") String redirectUrl,
			HttpServletRequest request,
			Model model) {



		return "passport";
	}


	private void validStartData(String runetId, String phoneNumber, String redirectUrl) throws Exception {
		if (!modelData.containsKey("runetId") && (runetId == null || runetId.equals("")) ||
				!modelData.containsKey("phoneNumber") && (phoneNumber == null || phoneNumber.equals("")) ||
				!modelData.containsKey("redirectUrl") && (redirectUrl == null || redirectUrl.equals(""))) {
			modelData.put("fatalError", "Отсутствуют необходимые параметры для заполнения анкеты");
			modelData.put("errorMessage", "Отсутствуют необходимые параметры для заполнения анкеты");
			throw new Exception("Отсутствуют необходимые параметры для заполнения анкеты");
		}
	}

	/**
	 * Проверка пасспорта.
	 *
	 * @param runetId
	 * @param phoneNumber
	 * @param passportFile
	 * @param codeword
	 * @param name
	 * @param surname
	 * @param patronymic
	 * @param birthdate
	 * @param request
	 * @param model
	 * @return
	 */

	@RequestMapping(value = "/check-passport", method = {RequestMethod.GET, RequestMethod.POST})
	private String checkPassport(
			@RequestParam(required = false, value = "runetId") String runetId,
			@RequestParam(required = false, value = "phoneNumber") String phoneNumber,
			@RequestParam(required = false, value = "passportFile") MultipartFile passportFile,
			@RequestParam(required = false, value = "codeword") String codeword,
			@RequestParam(required = false, value = "name") String name,
			@RequestParam(required = false, value = "surname") String surname,
			@RequestParam(required = false, value = "patronymic") String patronymic,
			@RequestParam(required = false, value = "birthdate") String birthdate,
			HttpServletRequest request,

			Model model) {

		try {

			if ((!checkRecognitionsCount(modelData.get("runetId")))) {


			validStartData(runetId, phoneNumber, null);

			if (passportFile == null) {
				model.addAllAttributes(modelData);
				return "passport";
			}

			modelData.remove("errorMessage");
			modelData.put("phoneNumber", phoneNumber);
			modelData.put("name", name);
			modelData.put("surname", surname);
			modelData.put("patronymic", patronymic);
			modelData.put("birthdate", birthdate);
			modelData.put("codeword", codeword);
			modelData.remove("series");
			modelData.remove("number");

			if (dbLogMode)
				byteFromFile = passportFile.getBytes();

			passportTest(passportFile); // основной метод процессинга пасспорта
			addOneRecognition(modelData.get("runetId"));


			if (!modelData.containsKey("series")
					|| !modelData.containsKey("number")
					|| modelData.get("series").replaceAll("[^\\d]", "").length() != 4
					|| modelData.get("number").replaceAll("[^\\d]", "").length() != 6)
				throw new Exception("Фото паспотра не распознано: серия " + modelData.containsKey("series") + " номер " + modelData.get("number"));
			modelData.put("series", modelData.get("series").replaceAll("[^\\d]", ""));
			modelData.put("number", modelData.get("number").replaceAll("[^\\d]", ""));
			model.addAllAttributes(modelData);
			return "passport";

		} else {

				logger.error("Клиент превысил кол-во распознаваний ");
				consoleLogger.error("Клиент превысил кол-во распознаваний ");
				modelData.putIfAbsent("fatalError", "Извините, мы не можем выпустить Вам карту!");
				modelData.putIfAbsent("errorMessage", "Извините, мы не можем выпустить Вам карту!");
				modelData.put("redirectUrl", modelData.get("startUrl"));
				model.addAllAttributes(modelData);
				return "passport";


			}
		} catch (Exception ex) {
			logger.error("Ошибка при сканировании паспорта", ex);
			consoleLogger.error("Ошибка при сканировании паспорта", ex);
			modelData.putIfAbsent("errorMessage", "Ошибка при распознавании паспорта, загрузите другое фото");
			model.addAllAttributes(modelData);
			return "passport";
		}
	}

	/**
	 * Ограничиваем брутфорсеров максимальным Runet-id
	 * @param runetId
	 * @return
	 */
	private boolean checkMaxRunetId(String runetId) {
		try {


			consoleLogger.error("Переданный runet-id " +  runetId);
			if (Integer.parseInt(runetId) > maxRunetId) {
				return true;
			} else {
				consoleLogger.error("Переданный runet-id меньше макисмального");
				return false;
			}

		} catch (Exception ex) {
			logger.error("Ошибка парсинга runet-id", ex);
			consoleLogger.error("Ошибка парсинга runet-id", ex);

			return false;
		}

	}


	private boolean checkRecognitionsCount(String runetId) throws Exception {


		try {

			/**
			 * Вытаскиваем энтити
			 * Берем первую запись, а вообще там должна быть только одна
			 */
			Optional<Recognitions> recognition = recognitionsCountControlRepository.getEntryByRunetId(runetId).stream().findFirst();



			/**
			 * Проверяем есть ли записи по этому runet-id или нет
			 */
			if ((recognition.isPresent())) {
				Recognitions localRecognition = recognition.get();
				consoleLogger.info("По данному runet-d [" + runetId + "] есть " + localRecognition.getCount() + "записей");

				if (localRecognition.getCount() > maxRecognitions-2){
					return true;
				} else return false;


			} else {
				consoleLogger.info("По Runet-id [" + runetId + "] вообще не было распознаваний");
			}

		} catch (Exception ex) {
			throw new Exception("Ошибка сохранении количества распознаваний в БД", ex);
		}

		return false;
	}


	private void addOneRecognition(String runetId) throws Exception {

		try {

			/**
			 * Вытаскиваем энтити
			 * Берем первую запись, а вообще там должна быть только одна
			 */
			Optional<Recognitions> recognition = recognitionsCountControlRepository.getEntryByRunetId(runetId).stream().findFirst();



			/**
			 * Проверяем есть ли записи по этому runet-id или нет
			 */
			if ((recognition.isPresent())) {
				Recognitions localRecognition = recognition.get();
				consoleLogger.info("По данному runet-d [" + runetId + "] есть запись");
				localRecognition.setCount(localRecognition.getCount() + 1);
				recognitionsCountControlRepository.save(localRecognition);

			} else { // иначе просто сохраняем новый рунет-id
				consoleLogger.info("CLIENT ID:   DB ERROR");
				recognitionsCountControlRepository.save(new Recognitions(runetId));

			}

		} catch (Exception ex) {
			throw new Exception("Ошибка сохранении количества распознаваний в БД", ex);
		}
	}

	/**
	 * Контроллер, отвечающий за выпуск ВПК.
	 *
	 * @param phoneNumber
	 * @param codeword
	 * @param name
	 * @param surname
	 * @param patronymic
	 * @param birthdate
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/open-card", method = {RequestMethod.GET, RequestMethod.POST})
	private String openCard(
			@RequestParam(required = false, value = "phoneNumber") String phoneNumber,
			@RequestParam(required = false, value = "codeword") String codeword,
			@RequestParam(required = false, value = "name") String name,
			@RequestParam(required = false, value = "surname") String surname,
			@RequestParam(required = false, value = "patronymic") String patronymic,
			@RequestParam(required = false, value = "birthdate") String birthdate,
			Model model) {
		try {

			if (modelData.containsKey("okMessage") || modelData.containsKey("errorMessage") || modelData.containsKey("fatalError")) {
				model.addAllAttributes(modelData);
				return "passport";
			}

			validStartData(null, phoneNumber, null);

			phoneNumber = phoneNumber.replaceAll("[^\\d]", "");

			modelData.put("phoneNumber", phoneNumber.substring(phoneNumber.length() - 10, phoneNumber.length()));
			modelData.put("codeword", codeword);
			modelData.put("name", name);
			modelData.put("surname", surname);
			modelData.put("patronymic", patronymic);
			modelData.put("birthdate", birthdate);
			modelData.remove("errorMessage");

			if (modelData.containsKey("passportId")) {

				vpkFuture = pool.submit(() -> {
					if (!(Boolean.valueOf(env.getProperty("test")) && Boolean.valueOf(env.getProperty("test.ignore.all")))) {
						try {
							if (env.getProperty("test", Boolean.TYPE) &&
									env.containsProperty("test.inn.http.status") &&
									!"".equals(env.getProperty("test.inn.http.status")))
								throw new RifRestTool.RifHttpClientErrorException("Имитация ошибки", HttpStatus.valueOf(env.getProperty("test.inn.http.status", Integer.TYPE)));
							else innTest(); // запрос ИНН
						} catch (RifRestTool.RifHttpClientErrorException ex) {
							if (ex.getStatusCode() == HttpStatus.NOT_FOUND)
								throw new Exception(ex.getCustomMessage(), ex);

							logger.error(ex.getMessage(), ex);
							modelData.putIfAbsent("fatalError", "Упс, что-то пошло не так. Пожалуйста, попробуйте еще раз чуть позже.");
							modelData.putIfAbsent("errorMessage", "Упс, что-то пошло не так. Пожалуйста, попробуйте еще раз чуть позже.");
							modelData.put("redirectUrl", modelData.get("startUrl"));
							model.addAllAttributes(modelData);
							errorPushToBot("Похоже отвалился сайт ИФНС: " + ex.getCustomMessage());
							return "Упс, что-то пошло не так. Пожалуйста, попробуйте еще раз чуть позже.";
						}

						upridTest(); // запрос УПРИД
						accountApproving(); // подтверждение аккаунта
						try {
							vpkTest(); // запрос ВПК
						} catch (Exception ex) {
							if (sendPush)
								pushError(ex);
							throw ex;
						}

						if (dbLogMode) {
							try {
								rifRepository.save(new RifLog(modelData, byteFromFile)); // сохраняем в БД
							} catch (Exception ex) {
								// Логгируем
								consoleLogger.error("Произошла ошибка сохранения в БД: " + ex.getMessage(), ex);
								consoleLogger.error("Пытаемся записать следующие данные: " + new Gson().toJson(modelData));
								logger.error("Произошла ошибка сохранения в БД: " + ex.getMessage(), ex);
								logger.error("Пытаемся записать следующие данные: " + new Gson().toJson(modelData));
							}
						}

						if (sendPush)
							pushOk();

					}

					modelData.remove("errorMessage");
					modelData.put("okMessage", "true");
					model.addAllAttributes(modelData);

					return null;
				});
				modelData.put("waitVPK","true");
				//return "passport";
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			consoleLogger.error(ex.getMessage(), ex);
			modelData.putIfAbsent("fatalError", "Извините, мы не можем выпустить Вам карту");
			modelData.putIfAbsent("errorMessage", "Извините, мы не можем выпустить Вам карту");
			modelData.put("redirectUrl", modelData.get("startUrl"));
			model.addAllAttributes(modelData);
			errorPushToBot(ex.getMessage());
			return "passport";
		}

		model.addAllAttributes(modelData);
		return "passport";
	}

	private Future<String> vpkFuture;

	@RequestMapping(value = "/status", method = {RequestMethod.POST})
	@ResponseBody
	private String status() {
		if(vpkFuture.isDone()) {
			modelData.remove("waitVPK");
			try {
				String ans = vpkFuture.get(); // Ловлю Exception, а результат игнорю
				if(ans != null)
					return ans;
				return "ok";
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				consoleLogger.error(ex.getMessage(), ex);
				modelData.putIfAbsent("fatalError", "Извините, мы не можем выпустить Вам карту");
				modelData.putIfAbsent("errorMessage", "Извините, мы не можем выпустить Вам карту");
				modelData.put("redirectUrl", modelData.get("startUrl"));
				errorPushToBot(ex.getMessage());
				return "error";
			}
		}
		return "wait";
	}

	/**
	 * Сервисный метод обработки пасспорта.
	 *
	 * @param passportFile
	 * @throws Exception
	 */
	private void passportTest(MultipartFile passportFile) throws Exception {
		JsonObject pass = rifRestTool.getScanData(passportFile, RifRestTool.SCAN_TYPE.PASSPORT_SCAN);
		modelData.putAll(getPassportData(pass));
		modelData.put("passportId", pass.get("id").getAsString());
	}

	/**
	 * Выбросить неудачный пуш на партнера
	 *
	 * @param ex
	 * @throws Exception
	 */
	private void pushError(Exception ex) {
		pool.submit(() -> {
			try {
				if (Boolean.valueOf(env.getProperty("test")) && Boolean.valueOf(env.getProperty("test.push-err.exception")))
					throw new Exception();
				rifRestTool.pushToRunet(JSONTemplate.create()
						.add("TYPE", "VIRTUALCARD")
						.add("RUNET-ID", Boolean.valueOf(env.getProperty("test")) && testRunetId != null && !"".equals(testRunetId) ? testRunetId : modelData.get("runetId"))
						.add("virtual_error", ex.getMessage()).getJson());
			} catch (Exception e) {
				logger.error("Ошибка при отправке неудачного PUSH партнёру", ex);
				consoleLogger.error("Ошибка при отправке неудачного PUSH партнёру", ex);
			}
		});
	}

	private void errorPushToBot(String message) {
		if (bot != null)
			bot.fireMessage("RUNET-ID [" + modelData.get("runetId") + "]: " + message);
	}

	/**
	 * Выбросить удачный пуш на партнера.
	 *
	 * @throws Exception
	 */
	private void pushOk() {
		pool.submit(() -> {
			try {
				if (Boolean.valueOf(env.getProperty("test")) && Boolean.valueOf(env.getProperty("test.push-ok.exception")))
					throw new Exception();
				rifRestTool.pushToRunet(JSONTemplate.create()
						.add("TYPE", "VIRTUALCARD")
						.add("RUNET-ID", Boolean.valueOf(env.getProperty("test")) && testRunetId != null && !"".equals(testRunetId) ? testRunetId : modelData.get("runetId"))
						.add("client_id", modelData.get("client_id"))
						.add("virtual_ean", modelData.get("vpk_number")).getJson());
			} catch (Exception ex) {
				logger.error("Ошибка при отправке удачного PUSH партнёру", ex);
				consoleLogger.error("Ошибка при отправке удачного PUSH партнёру", ex);
			}
		});
	}

	/**
	 * Сервисный метод, отвечающий за выпуск ВПК.
	 *
	 * @throws Exception
	 */
	private void vpkTest() throws Exception {
		try {
			if (Boolean.valueOf(env.getProperty("test")) && Boolean.valueOf(env.getProperty("test.vpk.exception")))
				throw new Exception();
			JsonObject vpk = rifRestTool.getVPK(modelData.get("client_id"));
			modelData.put("vpk_number", vpk.get("card_id").getAsString());
		} catch (Exception ex) {
			throw new Exception("Ошибка при выпуске виртуальной карты: " + ex.getMessage(), ex);
		}
	}

	/**
	 * Сервисный метод, отвечающий за прохождение проверки УПРИД.
	 *
	 * @throws Exception
	 */
	private void upridTest() throws Exception {
		try {
			if (Boolean.valueOf(env.getProperty("test")) && Boolean.valueOf(env.getProperty("test.uprid.exception")))
				throw new RifRestTool.RifHttpClientErrorException("", HttpStatus.BAD_REQUEST);

			JsonObject uprid = rifRestTool.getUPRID(
					modelData.get("phoneNumber"),
					modelData.get("surname"),
					modelData.get("name"),
					modelData.get("patronymic"),
					modelData.get("series") + modelData.get("number"),
					modelData.get("inn"),
					modelData.get("codeword"),
					Boolean.valueOf(env.getProperty("test")) && Boolean.valueOf(env.getProperty("create_anonymous_account")),
					modelData.get("passportId"),
					modelData.get("inn_recognition_id"));

			modelData.put("client_id", uprid.get("id").getAsString());
			modelData.put("account_level", uprid.get("account_level").getAsString());
			modelData.put("expected_account_level", uprid.get("expected_account_level").getAsString());
			modelData.put("created", uprid.get("created").getAsString());
			modelData.put("updated", uprid.get("updated").getAsString());
		} catch (RifRestTool.RifHttpClientErrorException ex) {
			throw new Exception("Ошибка при получении УПРИД: " + ex.getCustomMessage(), ex);
		} catch (Exception ex) {
			throw new Exception("Ошибка при получении УПРИД: " + ex.getMessage(), ex);
		}
	}


	/**
	 * Метод, отвечающий за ожидание изменения статуса клиента по УПРИД.
	 *
	 * @throws Exception
	 */
	private void accountApproving() throws Exception {
		try {
			if (Boolean.valueOf(env.getProperty("test")) && Boolean.valueOf(env.getProperty("test.account-approving.exception")))
				throw new Exception();

			// Ждем пока не сменится тип аккаунта
			while (true) {

				String clientID = modelData.get("client_id"); // id клиента, который нужен для получения информации
				JsonObject accountStatus = rifRestTool.getaccountApprovingResult(clientID);

				if (accountStatus.get("account_level_upgrade_error") != null) { // если отвалились ошибки
					throw new Exception(((JsonObject) accountStatus.get("account_level_upgrade_error")).get("message").getAsString());
				}
				if (accountStatus.get("account_level").getAsString().contains("UPRID")) { // статус поменялся
					modelData.put("account_number", accountStatus.get("account_number").getAsString());
					Thread.sleep(1000);
					break;
				}
				Thread.sleep(5000);
			}

		} catch (Exception ex) {
			throw new Exception("Ошибка при получении статуса клиента: " + ex.getMessage(), ex);
		}
	}

	/**
	 * Метод, отвечающий за проверку ИНН.
	 *
	 * @throws Exception
	 */
	private void innTest() throws Exception {
		try {
			if (Boolean.valueOf(env.getProperty("test")) && Boolean.valueOf(env.getProperty("test.inn.exception")))
				throw new Exception();
			JsonObject inn = rifRestTool.getINN(
					modelData.get("surname"),
					modelData.get("name"),
					modelData.get("patronymic"),
					LocalDate.parse(modelData.get("birthdate"), DateTimeFormatter.ofPattern("dd.MM.yyyy")),
					modelData.get("series"),
					modelData.get("number"));

			modelData.put("inn", inn.get("inn").getAsString());
			modelData.put("inn_recognition_id", inn.get("inn_recognition_id").getAsString());
		} catch (RifRestTool.RifHttpClientErrorException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new Exception("Ошибка при получении ИНН: " + ex.getMessage(), ex);
		}
	}

	/**
	 * Метод, парсящий ранее полученный пасспортные данные в "мапу".
	 *
	 * @param passportJsonObject
	 * @return
	 * @throws Exception
	 */
	private Map<String, String> getPassportData(JsonObject passportJsonObject) throws Exception {
		try {
			Map<String, String> data = new HashMap<>();
			JsonArray arr = passportJsonObject.getAsJsonObject("data").getAsJsonArray("stringFields");
			Iterator<JsonElement> iterator = arr.iterator();
			while (iterator.hasNext()) {
				JsonObject o = (JsonObject) iterator.next();
				data.put(o.get("name").getAsString(), o.get("utf8value").getAsString());
			}
			return data;
		} catch (Exception ex) {
			throw new Exception("Файл не читается", ex);
		}
	}

	/**
	 * Метод, отвечающий за ошибку в случае загрузки слишком большого файла.
	 * Ограничение выставляется в application.properties
	 *
	 * @param e
	 * @param redirectAttributes
	 * @param request
	 * @param response
	 * @return
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ModelAndView handleException(MaxUploadSizeExceededException e,
	                                    RedirectAttributes redirectAttributes,
	                                    HttpServletRequest request,
	                                    HttpServletResponse response) {
		ModelAndView model = new ModelAndView("passport");
		modelData.put("errorMessage", "Слишком большой размер файла, попробуйте загрузить другой");
		model.addAllObjects(modelData);

		return model;
	}

	@PreDestroy
	private void close() {
		pool.shutdown();
	}

	/*class OpenVPKTask implements Callable<JsonObject> {
		private final Map<String, String> data;
		private final int timeout;
		private final boolean dbLogMode;

		public OpenVPKTask(Map<String, String> data, int timeout, boolean dbLogMode) {
			this.data = new HashMap<>(data);
			this.timeout = timeout;
			this.dbLogMode = dbLogMode;
		}

		@Override
		public JsonObject call() throws Exception {
			JsonObject accountStatus = null;

			while ((accountStatus = rifRestTool.getaccountApprovingResult(data.get("client_id"))) == null ||
					accountStatus.get("account_level").equals("NOT_RESERVED") &&
							!accountStatus.keySet().contains("account_level_upgrade_error"))
				Thread.sleep(timeout);

			if (accountStatus.keySet().contains("account_level_upgrade_error"))
				throw new Exception("Ошибка при получении статуса клиента: "+accountStatus.get("account_level_upgrade_error").getAsString());

			data.put("account_number", accountStatus.get("account_number").getAsString());

			// запрос ВПК
			try {
				JsonObject vpk = rifRestTool.getVPK(data.get("client_id"));
				data.put("vpk_number", vpk.get("card_id").getAsString());
			} catch (Exception ex) {
				if (sendPush)
					pushError(ex);
				throw new Exception("Ошибка при выпуске виртуальной карты: " + ex.getMessage(), ex);
			}

			if (dbLogMode) {
				try {
					rifRepository.save(new RifLog(data, byteFromFile)); // сохраняем в БД
				} catch (Exception ex) {
					consoleLogger.error("Произошла ошибка сохранения в БД: " + ex.getMessage(), ex);
					consoleLogger.error("Пытаемся записать следующие данные: " + new Gson().toJson(modelData));
					logger.error("Произошла ошибка сохранения в БД: " + ex.getMessage(), ex);
					logger.error("Пытаемся записать следующие данные: " + new Gson().toJson(modelData));
				}
			}

			return accountStatus;
		}
	}*/
}