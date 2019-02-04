package ru.reso.calclogcompare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.reso.calclogcompare.model.DailyReport;
import ru.reso.calclogcompare.model.Users;
import ru.reso.calclogcompare.repository.UsersRepository;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MainServiceImpl implements MainService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public List<Users> getAll() {
        return usersRepository.findAll();
    }

    @Override
    public List<Users> addMeasure(Double temp) {
        usersRepository.save(new Users(temp));
        return usersRepository.findAll();
    }

    @Override
    public List<Users> getTodayMeasures() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
       // Date date2 = new Date();
       // date2 = new SimpleDateFormat("yyyy-MM-dd").parse(dateFormat.format(date));

      //  System.out.println("Date - " + date2);

      //  LocalDateTime ldt = LocalDateTime.now();
      //  System.out.println(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(ldt));




      //  Date myDate = new Date();
      //  SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
      ///  String strDate = sm.format(myDate);
      //  Date dt = sm.parse(strDate);
     //  System.out.println(strDate);
      //  System.out.println(dt);



     //   System.out.println("Печатаем дневные данные");
        // Печатаем
       // for (Users temp : usersRepository.getTodayMeasures()) {
       // for (Users temp : usersRepository.getTodayMeasures(strDate)) {
        /* for (Users temp : usersRepository.getTodayMeasures(new SimpleDateFormat("yyyy-MM-dd ").parse("2019-01-30"))) {
            System.out.println(temp.toString());
        }*/

       /* System.out.println("ВСЕ .... ");

        for (Users temp : usersRepository.findAll()) {
            System.out.println(temp.toString());
        }
*/


        // return  usersRepository.getTodayMeasures(strDate);
        return  usersRepository.getTodayMeasures(new SimpleDateFormat("yyyy-MM-dd").parse(dateFormat.format(date)));
    }

    public static boolean isBetween(LocalTime candidate, LocalTime start, LocalTime end) {
        return !candidate.isBefore(start) && !candidate.isAfter(end);  // Inclusive.
    }

    @Override
    public List<DailyReport> getWeeklyDayReport() throws ParseException {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        Double dayTemp = 999.0;
        Double nightTemp = 999.0;
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, -7);
        Date currentDateWeekAgo = c.getTime();

        ArrayList<Users> temperaturesForWeek = new ArrayList<>();
        List<DailyReport> weeklyReport = new ArrayList<>();
        temperaturesForWeek.addAll(usersRepository.getWeekMeasures(new SimpleDateFormat("yyyy-MM-dd").parse(dateFormat.format(currentDateWeekAgo))));

          System.out.println("Напечатаем ка сначала весь запрос.... ");

        // Печатаем
        for (Users temp : temperaturesForWeek) {
            System.out.println(temp.toString());
        }


        if (!temperaturesForWeek.isEmpty()) {
            for (Users temp : temperaturesForWeek) {

                if (temp.getTimeCreated()!=null) {
                  //  System.out.println(temp.getTimeCreated());
                  //  System.out.println(isBetween(temp.getTimeCreated().toLocalTime(), LocalTime.of(10, 0), LocalTime.of(18, 0)));
                    if (isBetween(temp.getTimeCreated().toLocalTime(), LocalTime.of(13, 0), LocalTime.of(15, 0))) { // если дневное
                        dayTemp = temp.getTemperature();
                    //    weeklyReport.add(new DailyReport(temp.getTest(), temp.getTemperature(), Double.valueOf(0)));
                    }
                    if (isBetween(temp.getTimeCreated().toLocalTime(), LocalTime.of(1, 0), LocalTime.of(3, 0))) { // если дневное
                        nightTemp = temp.getTemperature();
                    //    weeklyReport.add(new DailyReport(temp.getTest(), Double.valueOf(0), temp.getTemperature()));
                    }
                    if ((dayTemp!=999.0) && (nightTemp!=999.0)) {
                        weeklyReport.add(new DailyReport(temp.getTest(), dayTemp, nightTemp));
                        dayTemp = 999.0;
                        nightTemp = 999.0;

                    }

                }
            }
        }

        // Печатаем
        for (DailyReport temp : weeklyReport) {
            System.out.println(temp.toString());
        }

        return  weeklyReport;
    }

    @Override
    public List<DailyReport> getWeeklyNightReport()  throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, -7);
        Date currentDateWeekAgo = c.getTime();
        ArrayList<Users> temperaturesForWeek = new ArrayList<>();
        List<DailyReport> weeklyReport = new ArrayList<>();
        temperaturesForWeek.addAll(usersRepository.getWeekMeasures(new SimpleDateFormat("yyyy-MM-dd").parse(dateFormat.format(currentDateWeekAgo))));

        if (!temperaturesForWeek.isEmpty()) {
            for (Users temp : temperaturesForWeek) {
                if (temp.getTimeCreated()!=null) {
                    if (isBetween(temp.getTimeCreated().toLocalTime(), LocalTime.of(1, 0), LocalTime.of(3, 0))) { // если дневное
                        weeklyReport.add(new DailyReport(temp.getTest(), Double.valueOf(0), temp.getTemperature()));
                    }
                }
            }
        }

        return  weeklyReport;
    }

    @Override
    public List<DailyReport> getMonthNightReport() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, -30);
        Date currentDateWeekAgo = c.getTime();

        System.out.println("The DATE is" + currentDateWeekAgo);

        ArrayList<Users> temperaturesForWeek = new ArrayList<>();
        List<DailyReport> weeklyReport = new ArrayList<>();
        temperaturesForWeek.addAll(usersRepository.getWeekMeasures(new SimpleDateFormat("yyyy-MM-dd").parse(dateFormat.format(currentDateWeekAgo))));

        if (!temperaturesForWeek.isEmpty()) {
            for (Users temp : temperaturesForWeek) {
                if (temp.getTimeCreated()!=null) {
                    if (isBetween(temp.getTimeCreated().toLocalTime(), LocalTime.of(1, 0), LocalTime.of(3, 0))) { // если дневное
                        weeklyReport.add(new DailyReport(temp.getTest(), Double.valueOf(0), temp.getTemperature()));
                    }
                }
            }
        }


        return  weeklyReport;
    }

    @Override
    public List<DailyReport> getMonthDayReport() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        Double dayTemp = 999.0;
        Double nightTemp = 999.0;
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, -30);
        Date currentDateWeekAgo = c.getTime();

        ArrayList<Users> temperaturesForWeek = new ArrayList<>();
        List<DailyReport> weeklyReport = new ArrayList<>();
        temperaturesForWeek.addAll(usersRepository.getWeekMeasures(new SimpleDateFormat("yyyy-MM-dd").parse(dateFormat.format(currentDateWeekAgo))));

        if (!temperaturesForWeek.isEmpty()) {
            for (Users temp : temperaturesForWeek) {
                if (temp.getTimeCreated()!=null) {
                    if (isBetween(temp.getTimeCreated().toLocalTime(), LocalTime.of(13, 0), LocalTime.of(15, 0))) { // если дневное
                        dayTemp = temp.getTemperature();
                        // weeklyReport.add(new DailyReport(temp.getTest(), temp.getTemperature(), Double.valueOf(0)));
                    }
                    if (isBetween(temp.getTimeCreated().toLocalTime(), LocalTime.of(1, 0), LocalTime.of(3, 0))) { // если дневное
                        //weeklyReport.add(new DailyReport(temp.getTest(), Double.valueOf(0), temp.getTemperature()));
                        nightTemp = temp.getTemperature();
                    }
                    if ((dayTemp!=999.0) && (nightTemp!=999.0)) {
                        weeklyReport.add(new DailyReport(temp.getTest(), dayTemp, nightTemp));
                        dayTemp = 999.0;
                        nightTemp = 999.0;
                    }
                }
            }
        }

        return  weeklyReport;
    }
}
