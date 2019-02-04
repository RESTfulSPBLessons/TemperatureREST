package ru.reso.calclogcompare.controller;

// This class created by Anton Romanov 03.12.2018 at 11:44
// Git Hub repo - ...
// https://192.*.*.*:33246/RCCT-2.0-SNAPSHOT/rest/getone?id=1
// http://localhost:8083/RCCT-2.0-SNAPSHOT/rest/check?id=1


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.reso.calclogcompare.model.*;
import ru.reso.calclogcompare.service.MainService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/rest/users")
public class MainRestController {


    @Autowired
    MainService mainService;

    @GetMapping("/all")
    public List<Users> getAll(){
        System.out.println("Запрос прошел");

        for(Users temp : mainService.getAll()){
            System.out.println(temp.getId() + " - " + temp.getTemperature() + " - " + temp.getDateCreated());
        }


        return  mainService.getAll();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/v2")
    public ArrayList<String> getAll2() {

        System.out.println("I have got you!");

        ArrayList<String> a = new ArrayList<>();

        a.add("1");
        a.add("2");


        return  a;

    }

    @PostMapping("/add")
    public List<Users> newMeasure(@RequestBody PR newTemp) {
        System.out.println("we are in POST HTTP");

        return mainService.addMeasure(newTemp.getTemp());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/test")
    public PR testPost() {

        System.out.println("get POST object");

        PR testObj = new PR(Double.valueOf(123));



        return  testObj;

    }

    @GetMapping("/today")
    public List<Users> getTodayMeasures() throws ParseException {
        return  mainService.getTodayMeasures();
    }

    @GetMapping("/weekday")
    public List<DailyReport> getWeeklyReport() throws ParseException {
        return  mainService.getWeeklyDayReport();
    }

    @GetMapping("/week")
    public List<DailyReport> getWeeklyDayNightReport() throws ParseException {

        List<DailyReport> newWeeklyReport = new ArrayList<>();
        for (DailyReport dayReport : mainService.getWeeklyDayReport()) {
            for (DailyReport nightReport : mainService.getWeeklyNightReport()) {

                if (dayReport.getMeasureDate() == dayReport.getMeasureDate()){
                    dayReport.setNightTemp(nightReport.getNightTemp());
                    newWeeklyReport.add(new DailyReport(dayReport.getMeasureDate(), dayReport.getDayTemp(), nightReport.getNightTemp()));
                }
            }
        }

        return  newWeeklyReport;
    }



    @GetMapping("/weeknight")
    public List<DailyReport> getWeeklyNightReport() throws ParseException {
        return  mainService.getWeeklyNightReport();
    }

    @GetMapping("/monthnight")
    public List<DailyReport> getMonthNightReport() throws ParseException {
        return  mainService.getMonthNightReport();
    }


    @GetMapping("/monthday")
    public List<DailyReport> getMonthReport() throws ParseException {
        return  mainService.getMonthDayReport();
    }


    @GetMapping("/" +
            "")
    public List<DailyReport> getMonthDayReport() throws ParseException {
        return  mainService.getMonthDayReport();
    }


}
