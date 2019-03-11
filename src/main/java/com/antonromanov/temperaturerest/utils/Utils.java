package com.antonromanov.temperaturerest.utils;

import com.antonromanov.temperaturerest.model.DailyReport;
import com.antonromanov.temperaturerest.model.Temperature;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Тут собраны основные утлилиты.
 */
public class Utils {

    /**
     * Определяет лижит ли указанное время между двумя заданными.
     *
     * @param candidate
     * @param start
     * @param end
     * @return
     */
    public static boolean isBetween(LocalTime candidate, LocalTime start, LocalTime end) {
        return !candidate.isBefore(start) && !candidate.isAfter(end);  // Inclusive.
    }

    /**
     * Проверяем заданное время это дневная температура или ночная
     * @param temperatureList
     * @return
     */
    public static List<DailyReport> checkDayNight(ArrayList<Temperature> temperatureList) {

        Double dayTemp = 999.0;
        Double nightTemp = 999.0;
        List<DailyReport> weeklyReport = new ArrayList<>();

        if (!temperatureList.isEmpty()) {
            for (Temperature temp : temperatureList) {

                if (temp.getTimeCreated() != null) {
                    if (isBetween(temp.getTimeCreated().toLocalTime(), LocalTime.of(13, 0), LocalTime.of(15, 0))) { // если дневное
                        dayTemp = temp.getTemperature();
                    }
                    if (isBetween(temp.getTimeCreated().toLocalTime(), LocalTime.of(1, 0), LocalTime.of(3, 0))) { // если дневное
                        nightTemp = temp.getTemperature();
                    }
                    if ((dayTemp != 999.0) && (nightTemp != 999.0)) {
                        weeklyReport.add(new DailyReport(temp.getTest(), dayTemp, nightTemp));
                        dayTemp = 999.0;
                        nightTemp = 999.0;
                    }
                }
            }
        }


        return weeklyReport;
    }

    /**
     * Конвертим SQL-TIME в LOCALTIME
     *
     * @param time
     * @return
     */
    public static LocalTime toLocalTime(java.sql.Time time) {
        return time.toLocalTime();
    }

    // Проверяем таймут до последнего пинга.
    public static Boolean checkTimeout(Time lastPingTime) {

        Date date = new Date();
        Time time = new Time(date.getTime());
        Boolean result = true;

        if (lastPingTime != null) { // время должно быть не ноль, иначе все наебнется
            // TODO надо еще проверить, чтобы дата была именно сегодняшняя
            LocalTime offsetTime = toLocalTime(lastPingTime).plusMinutes(15);
            result = isBetween(toLocalTime(time), toLocalTime(lastPingTime), offsetTime);
        }

        System.out.println("РЕЗУЛЬТАТ ПРОВЕРКИ ТАЙМАУТА = " + result);
        return result;

    }

}
