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
     List<Temperature> addNewMeasure(Temperature measure); // добавить комплекс измерений
     List<Logs> addLog(Status log) throws ParseException; // добавить измерение
     List<Temperature> getTodayMeasures() throws ParseException; //статистика по сегодня
     List<DailyReport> getWeeklyDayReport() throws ParseException; // недельная статистика
     List<DailyReport> getMonthDayReport() throws ParseException; // статистика за месяц
     Time getLastContactTime(); // время последнего контакта
     Status getGlobalStatus(); // глобальное состояние
     MainParameters getMainParametrs(); // еще одно глобальное состояние инкапсулирующее предыдущее + еще параметры

}
