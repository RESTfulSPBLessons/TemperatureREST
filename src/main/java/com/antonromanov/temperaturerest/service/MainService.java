package com.antonromanov.temperaturerest.service;

import com.antonromanov.temperaturerest.livecontrolthread.MainParameters;
import org.springframework.stereotype.Service;
import com.antonromanov.temperaturerest.model.DailyReport;
import com.antonromanov.temperaturerest.model.Logs;
import com.antonromanov.temperaturerest.model.Status;
import com.antonromanov.temperaturerest.model.Temperature;

import java.sql.Time;
import java.text.ParseException;
import java.util.List;

@Service
public interface MainService {

	List<Temperature> getAll(); // список всех измерений (пока с одного датчика)

	List<Logs> getAllLogs(); // все логи

	List<Temperature> addMeasure(Double temp, String status); // добавить измерение

	List<Logs> addLog(Status log) throws ParseException; // добавить измерение \ todo: убрать этот метод потом

	void addLog2(Status log); // добавить измерение. Версия №2

	List<Temperature> getTodayMeasures() throws ParseException; //статистика по сегодня

	List<DailyReport> getWeeklyDayReport() throws ParseException; // недельная статистика

	List<DailyReport> getMonthDayReport() throws ParseException; // статистика за месяц

	Time getLastContactTime(); // время последнего контакта

	Boolean getLastContact220();  // AC статус последнего контакта

	Boolean getLastContactLan();  // LAN статус последнего контакта

	Boolean getLastContactLogged();  // Пинг залогирован или нет?

	Status getGlobalStatus(); // глобальное состояние

	Logs getLastLog(); // получить последний пинг (лог)	

	MainParameters getMainParametrs(); // еще одно глобальное состояние инкапсулирующее предыдущее + еще параметры

	void updateLastLog(Logs log); // обновить последний пинг (лог) новыми данными		



}
