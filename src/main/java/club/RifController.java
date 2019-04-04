package club;

import club.bot.Bot;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
import java.util.concurrent.*;


/**
 * Основной контроллер, отвечающий за выпуск карты/открытие счета
 */
@Controller
@ControllerAdvice
@Scope("session")
public class RifController {
	private final ExecutorService pool = Executors.newFixedThreadPool(5);
	private final Map<String, String> modelData = new HashMap<>();


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
	public RifController(Bot bot) {
		this.bot = bot;
	}

	@RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
	private String zeropoint() {
		return "redirect:/test";
	}

	@RequestMapping(value = "/test", method = {RequestMethod.GET, RequestMethod.POST})
	private String test() {
		logger.info("TEST");
		errorPushToBot("Похоже отвалился сайт ИФНС: ");
		return "welcome";
	}




	private void errorPushToBot(String message) {
//		if (bot != null)
//			bot.fireMessage("RUNET-ID [" + modelData.get("runetId") + "]: " + message);
	}

}