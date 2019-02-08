package ru.reso.calclogcompare.controller;

// This class created by Anton Romanov 03.12.2018 at 11:44
// Git Hub repo - ...


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.reso.calclogcompare.livecontrolthread.IsAliveController;
import ru.reso.calclogcompare.model.*;
import ru.reso.calclogcompare.service.MainService;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static ru.reso.calclogcompare.utils.Utils.isBetween;

@RestController
@RequestMapping("/rest/users")
public class MainRestController {

    /** Значит надо договориться, что постить мы будем в :
     *      - 02:00
     *      - 08:00
     *      - 14:00
     *      - 19:00
     */



    @Autowired
    MainService mainService;

    private boolean at2am;
    private boolean at8am;
    private boolean at14;
    private boolean at19;


    @GetMapping("/all")
    public List<Users> getAll() {
        System.out.println("Запрос прошел");

        for (Users temp : mainService.getAll()) {
            System.out.println(temp.getId() + " - " + temp.getTemperature() + " - " + temp.getDateCreated());
        }


        return mainService.getAll();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/v2")
    public ArrayList<String> getAll2() {

        System.out.println("I have got you!");

        ArrayList<String> a = new ArrayList<>();

        a.add("1");
        a.add("2");


        return a;

    }

    @PostMapping("/add")
    public List<Users> newMeasure(@RequestBody PR newTemp) {

        System.out.println("we are in POST HTTP");

        Date currentDate = new Date();
        Time time = new Time(currentDate.getTime());
        List<Users> result = new ArrayList<>();


        if ((isBetween(time.toLocalTime(), LocalTime.of(1, 0), LocalTime.of(3, 0))) && !at2am) { // если 2 часа ночи
            at2am = true;
            result = mainService.addMeasure(newTemp.getTemp());
        }
        if ((isBetween(time.toLocalTime(), LocalTime.of(7, 0), LocalTime.of(9, 0))) && !at8am) { // если 8 утра
            at8am = true;
            result = mainService.addMeasure(newTemp.getTemp());
        }
        if ((isBetween(time.toLocalTime(), LocalTime.of(13, 0), LocalTime.of(15, 0))) && !at14) { // если 14 часов дня
            at14 = true;
            result = mainService.addMeasure(newTemp.getTemp());
        }
        if ((isBetween(time.toLocalTime(), LocalTime.of(13, 0), LocalTime.of(15, 0)))&& !at19) { // если 19 часов вечера
            at19 = true;
            result = mainService.addMeasure(newTemp.getTemp());
        }

        if (at2am && at8am && at14 && at19){

            at2am = false;
            at8am = false;
            at14 = false;
            at19 = false;

        }



        return result;
    }

    @PostMapping("/addmeasure")
    public List<Users> addMeasure(@RequestBody Users measure) {

        return mainService.addNewMeasure(measure);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/test")
    public PR testPost() {

        System.out.println("get POST object");

        PR testObj = new PR(Double.valueOf(123));


        return testObj;

    }

    @GetMapping("/today")
    public List<Users> getTodayMeasures() throws ParseException {
        return mainService.getTodayMeasures();
    }

    @GetMapping("/weekday")
    public List<DailyReport> getWeeklyReport() throws ParseException {
        return mainService.getWeeklyDayReport();
    }

    @GetMapping("/week")
    public List<DailyReport> getWeeklyDayNightReport() throws ParseException {

        List<DailyReport> newWeeklyReport = new ArrayList<>();
        for (DailyReport dayReport : mainService.getWeeklyDayReport()) {
            for (DailyReport nightReport : mainService.getWeeklyNightReport()) {

                if (dayReport.getMeasureDate() == dayReport.getMeasureDate()) {
                    dayReport.setNightTemp(nightReport.getNightTemp());
                    newWeeklyReport.add(new DailyReport(dayReport.getMeasureDate(), dayReport.getDayTemp(), nightReport.getNightTemp()));
                }
            }
        }

        return newWeeklyReport;
    }


    @GetMapping("/weeknight")
    public List<DailyReport> getWeeklyNightReport() throws ParseException {
        return mainService.getWeeklyNightReport();
    }

    @GetMapping("/monthnight")
    public List<DailyReport> getMonthNightReport() throws ParseException {
        return mainService.getMonthNightReport();
    }


    @GetMapping("/monthday")
    public List<DailyReport> getMonthReport() throws ParseException {
        return mainService.getMonthDayReport();
    }

    @GetMapping("/status")
    public Status getStatus() throws ParseException {
        return mainService.getGlobalStatus();
    }

    /**
     * Выплюнуть в http все логи.
     *
     * @return
     */
    @GetMapping("/alllogs")
    public List<Logs> getAllLogs() {
        return mainService.getAllLogs();
    }

    @PostMapping("/addlog")
    public List<Logs> addLog(@RequestBody Status log) throws ParseException {

        System.out.println("Добавляем запись");

        Date date = new Date();
        Time time = new Time(date.getTime());

        log.setLastContactTime(time);
        log.setServerTime(time);
        log.setLastContactDate(date);

        System.out.println(log.toString());


        return mainService.addLog(log);
    }


    @GetMapping("/" +
            "")
    public List<DailyReport> getMonthDayReport() throws ParseException {
        return mainService.getMonthDayReport();
    }


}
