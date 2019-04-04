package club.service;

import club.JSONTemplate;
import club.P2PController;
import club.model.AccountRefillLog;
import club.repository.AccountRefillRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Сервисный класс, инкапсулирующий основные методы логгирования/получения данных
 * операций по пополнению счета. А так же логгирующий их.
 */
@Service
public class AccountRefillServiceImpl implements AccountRefillService {

	private AccountRefillRepository accountRefillRepository;
	private static Logger logger = LoggerFactory.getLogger(P2PController.class);
	private static Logger consoleLogger = LoggerFactory.getLogger("console_logger");
	/**
	 * Конструктор
	 *
	 * @param accountRefillRepository
	 */
	@Autowired
	public void setProductRepository(AccountRefillRepository accountRefillRepository) {
		this.accountRefillRepository = accountRefillRepository;
	}

	/**
	 * Вытаскиваем URL по ранее сохраненному OrderId
	 * @param orderId
	 * @return
	 */
	@Override
	public String getUrlByOrderId(String orderId) {

	if (!isBlank(orderId)) {
		Optional<AccountRefillLog> firstElement = accountRefillRepository.getAllLogsByOrderId(orderId).stream().findFirst();
		if (firstElement.isPresent()) {
			return firstElement.get().getUrl();
		} else {
			return null;
		}
	} else return  null;

	}


	/**
	 * Вытаскиваем RunetId по ранее сохраненному OrderId
	 * @param orderId
	 * @return - Runet-Id как String
	 */
	@Override
	public String getRunetIdByOrderId(String orderId) throws Exception {

	String result; // искомый runet-id
		if (!isBlank(orderId)) {
			try {
				Optional<AccountRefillLog> firstElement = accountRefillRepository.getAllLogsByOrderId(orderId).stream().findFirst();

				/**
				 * Если БД энтри нулевая - бросаем эксепшн
				 */
				if ((firstElement.isPresent()) && (!isBlank(firstElement.get().getRunetId()))) {

					result = firstElement.get().getRunetId();

				} else {
					throw new Exception();
				}


			} catch (Exception ex) {
				throw new Exception("Ошибка при получении ранее сохраненного runet-id [ orderid: " + orderId + " ]", ex);
			}
		} else {
			return null;
		}
		return result;
	}


	/**
	 * Добавляем новую запись (новый лог) о пополнении карты пользователем.
	 * @param log
	 */
	@Override
	public void add(AccountRefillLog log) {
		accountRefillRepository.save(log); // сохраняем в БД

		MDC.put("runetid", log.getRunetId());
		MDC.put("ORDER_ID", log.getOrderId());

		consoleLogger.info("########### PRIMARY LOG IN DB - OK ###########");
		consoleLogger.info("AMOUNT:   " + log.getAmount());
		consoleLogger.info("EAN:   " + log.getEan());
		consoleLogger.info("ORDER_ID:   " + log.getOrderId());
		consoleLogger.info("RUNET_ID:   " + log.getRunetId());
		consoleLogger.info("URL:   " + log.getUrl());

		logger.info(JSONTemplate.create()
				.add("method","add new account refill log")
				.add("AMOUNT",log.getAmount())
				.add("EAN",log.getEan())
				.add("ORDER_ID",log.getOrderId())
				.add("runetId", log.getRunetId()).getJson().toString());

		MDC.remove("runetid");
		MDC.remove("ORDER_ID");
	}

	/**
	 * После удачного пуша от АБС обновляем лог обращения о пополнении результатом от  АБС.
	 * То есть пользователь решил пополнить карту - пишем в лог runet-id, order-id. Пришел удачный ответ от абс -
	 * дописываем в этот же лог (по OrderId) статус пополнения, дату и прочее.
	 *
	 * @param orderId
	 * @param status
	 * @param status_description
	 * @param approvedDate
	 */
	@Override
	public void updateDBEntry(String orderId, String status, String status_description, Timestamp approvedDate) {
		/**
		 * Ищем запись в логе (в бд) по OrderId.
		 */
		Optional<AccountRefillLog> entry = accountRefillRepository.getAllLogsByOrderId(orderId).stream().findFirst();
		if (entry.isPresent()) {
			entry.get().setOrderId(orderId);
			entry.get().setState(status);
			entry.get().setStateDescription(status_description);
			entry.get().setApproveDate(approvedDate);
		}

		MDC.put("orderId", orderId);
		logger.info(JSONTemplate.create()
				.add("method","updateDBEntry")
				.add("orderId",orderId)
				.add("STATE",status)
				.add("TIMESTAMP", approvedDate.toString()).getJson().toString());

		MDC.remove("orderId");

		accountRefillRepository.save(entry.get());
	}


	/**
	 * Вытаскиваем EAN по ранее сохраненному OrderId
	 * @param orderId
	 * @return - EAN как String
	 */
	@Override
	public String getEanByOrderId(String orderId) throws Exception {

		String result; // искомый ean

		if (!isBlank(orderId)) {

			try {
				Optional<AccountRefillLog> firstElement = accountRefillRepository.getAllLogsByOrderId(orderId).stream().findFirst();

				/**
				 * Если БД энтри нулевая или сам ean пустой - бросаем эксепшн
				 */
				if ((firstElement.isPresent()) && (!isBlank(firstElement.get().getEan()))) {

					result = firstElement.get().getEan();

				} else {
					throw new Exception();
				}

			} catch (Exception ex) {
				throw new Exception("Ошибка при получении ранее сохраненного EAN в БД", ex);
			}
		} else {
			result = null;
		}

		return result;
	}


}
