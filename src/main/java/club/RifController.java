package club;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
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


	//private Bot bot;

	/**
	 * Логгеры
	 */
	private static Logger logger = LoggerFactory.getLogger(RifController.class);
	private static Logger consoleLogger = LoggerFactory.getLogger("console_logger");

	//@Autowired
	public RifController() {
		//this.bot = bot;
	}

	@RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
	private String zeropoint() {
		return "redirect:/test";
	}

	@RequestMapping(value = "/test", method = {RequestMethod.GET, RequestMethod.POST})
	private String test() {
		logger.info("TEST");
		System.out.println("МЫ ТУТ");
	//	errorPushToBot("Похоже отвалился сайт ИФНС: ");
		return "welcome";
	}




	private void errorPushToBot(String message) {
//		if (bot != null)
//			bot.fireMessage("RUNET-ID [" + modelData.get("runetId") + "]: " + message);
	}

}