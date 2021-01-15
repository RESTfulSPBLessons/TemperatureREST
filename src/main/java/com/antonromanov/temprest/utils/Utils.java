package com.antonromanov.temprest.utils;

import com.antonromanov.temprest.model.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.LoggerFactory;


/**
 * Тут собраны основные утлилиты.
 */
public class Utils {

	private static org.slf4j.Logger LOGGER = LoggerFactory.getLogger("console_logger");

	/**
	 * Создаем gson builder
	 */
	public static Gson createGsonBuilder() {


		Gson gson = new GsonBuilder()
				.serializeNulls()
				.setDateFormat("dd/MM/yyyy")
				.registerTypeAdapter(java.sql.Time.class, new TimeSerializer())
				.create();

		return gson;
	}





	/**
	 * Создаем статуса для Телеграмм Бота
	 **/
	public static String createBotStatus(Status status) {
		return createGsonBuilder().toJson(status);
	}



}
