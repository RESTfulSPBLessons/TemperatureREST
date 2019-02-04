package ru.reso.calclogcompare.service;

import org.springframework.stereotype.Service;
import ru.reso.calclogcompare.model.DailyReport;
import ru.reso.calclogcompare.model.Users;

import java.text.ParseException;
import java.util.List;

@Service
public interface MainService {

     List<Users> getAll(); // список всех измерений (пока с одного датчика)
     List<Users> addMeasure(Double temp); // добавить измерение
     List<Users> getTodayMeasures() throws ParseException;
     List<DailyReport> getWeeklyDayReport() throws ParseException;
     List<DailyReport> getWeeklyNightReport() throws ParseException;
     List<DailyReport> getMonthNightReport() throws ParseException;
     List<DailyReport> getMonthDayReport() throws ParseException;





}
