package com.antonromanov.temperaturerest.livecontrolthread;

import com.antonromanov.temperaturerest.controller.MainRestController;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.stereotype.Service;
import com.antonromanov.temperaturerest.model.Logs;
import com.antonromanov.temperaturerest.model.Status;
import com.antonromanov.temperaturerest.service.MainService;
import java.sql.Time;
import java.text.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.List;
import static com.antonromanov.temperaturerest.utils.Utils.*;

/**
 * Поток, мониторящий состояние датчиков по таймаутам.
 */
@Service
public class IsAliveController implements Runnable {

	/**
	 * Инжектим наш сервис.
	 */
	MainService mainService;

	/**
	 * Логгер файловый
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(IsAliveController.class);

	/**
	 * Конструктор.
	 *
	 * @param mainService
	 */
	public IsAliveController(MainService mainService) {
		this.mainService = mainService;
	}

	/**
	 * Собсна, основной метод, где вся логика.
	 */
	@Override
	public void run() {

		boolean ret = true;

		if (mainService != null) { // проверяем, чтобы сервис вообще доступен был

			while (ret) { // бесконечный цикл.
				try {

					//     System.out.println("Тестим Чек-таймаут - " + checkTimeout(mainService.getMainParametrs().getLastPingTime()));
					LOGGER.warn("TIMEOUT TEST:  " + checkTimeout(mainService.getMainParametrs().getLastPingTime()));
					Date date = new Date();
					Time time = new Time(date.getTime()); // берем текущее время.

					LOGGER.info("MainParametrs: AC {" + mainService.getMainParametrs().isAcStatus() + "},LAN {" +
							mainService.getMainParametrs().isLanStatus() + "}, IS LOGGED {" +
							mainService.getMainParametrs().isLogged() + "}");


					/**
					 *  Первое условие:
					 *
					 *  + Таймаут просрочен (false)
					 *  + Не логгировали это раньше
					 *  + АС или LAN по последним логам включен.
					 */
					if (!checkTimeout(mainService.getMainParametrs().getLastPingTime()) && (!mainService.getMainParametrs().isLogged()) &&
							(mainService.getMainParametrs().isAcStatus() || mainService.getMainParametrs().isLanStatus())) { // Хьюстон, у нас проблемы.....

						// Хьюстон, у нас проблемы....
						LOGGER.warn("WE HAVE PROBLEMS");

						/**
						 * Пишет в БД запись, что связи нет, чтобы ее фронтенд прочитать мог.
						 */
						Status log = new Status("REST: NO PING", false, false, 0, 0, time, time,
								0, 0, 0, 0, new Date());

						// Хьюстон, у нас проблемы....
						LOGGER.info("ENTRY IN DB CREATED:" + log.toString());

						try {
							List<Logs> tempLogs = mainService.addLog(log); // пишем через сервис.
						} catch (ParseException e) {
							e.printStackTrace();
						}

						mainService.getMainParametrs().setLogged(true); // теперь мы залоггировали трабл
						mainService.getMainParametrs().setAcStatus(false); // ставим флаг АС - нет
						mainService.getMainParametrs().setLanStatus(false);// ставим флаг LAN - нет
						mainService.getMainParametrs().setJustStartedSituation(false); // ну и это теперь не "только стартанули2 ситуация


						/**
						 * Следующая ситуация - проблема или решилась или ее не было. Условия:
						 *
						 * + АС - включено
						 * + LAN - включен
						 */
					} else if ((mainService.getMainParametrs().isAcStatus() || mainService.getMainParametrs().isLanStatus())) {

						System.out.println("Проблема решена или ее и не было");
						LOGGER.info("PROBLEM WAS SOLVED OR HAS NOT BEEN OCCURED");
						mainService.getMainParametrs().setLogged(false); // убираем, что мы залогировались

						/**
						 * Стартовая ситуация:
						 *
						 * + Таймаут по пингу просрочен
						 * + незалогированы
						 * + статусы сети и 220 отрицательные
						 */
					} else if (!checkTimeout(mainService.getMainParametrs().getLastPingTime()) && !mainService.getMainParametrs().isLogged() &&
							(!mainService.getMainParametrs().isAcStatus() || !mainService.getMainParametrs().isLanStatus())
					) { // стартовая ситуация
						//       System.out.println("Стартовая ситуация");
					}

					/**
					 * ждем 60 секунд.
					 */
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				} catch (BeanCreationException exception) {
					//        System.out.println("Косяк");
				}
			}
		}
	}

}
