package com.antonromanov.temperaturerest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.antonromanov.temperaturerest.model.*;
import com.antonromanov.temperaturerest.service.MainService;
import java.sql.Time;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.antonromanov.temperaturerest.utils.Utils.isBetween;

/**
 * Основной REST-контроллер приложения.
 */
@RestController
@RequestMapping("/rest/users")
public class MainRestController {

    //todo: надо прикрутить нормальные серверные логи
    //todo: надо поменять название проекта
    //todo: надо поменять названия классов и переменных
    //todo: надо поменять адрес REST API
    //todo: надо поменять еще вот эту ссылку - http://localhost:8080/FirstSPRINGJDBC-2.0-SNAPSHOT/rest/api/add. Чо за ФёрстСпрингДжейДиБиСи ????!!!!!!!


    /** Значит надо договориться, что постить мы будем в :
     *      - 02:00
     *      - 08:00
     *      - 14:00
     *      - 19:00
     */


    /**
     * Инжектим сервис.
     */
    @Autowired
    MainService mainService;

    /**
     * Глобальные флаги, чтобы отсекать пинги не в нужное время и чтобы в нужное время был только один пинг,
     * ибо кидаться мы с ардуины будем каждые 15 минут.
     */
    private boolean at2am;
    private boolean at8am;
    private boolean at14;
    private boolean at19;

    /**
     * Вдать все измерения температуры.
     *
     * @return
     */
    @GetMapping("/all")
    public List<Temperature> getAll() {
        System.out.println("Запрос прошел"); // вот это бы все логгировать или в консоль нормально и потом аппендером или в базу

        for (Temperature temp : mainService.getAll()) {
            System.out.println(temp.getId() + " - " + temp.getTemperature() + " - " + temp.getDateCreated());
        }


        return mainService.getAll();
    }


    /**
     * Добавить измерение. Уберем в следующем коммите.
     *
     * @param newTemp
     * @return
     */
    @PostMapping("/add")
    public List<Temperature> newMeasure(@RequestBody PR newTemp) {

        System.out.println("we are in POST HTTP");

        Date currentDate = new Date();
        Time time = new Time(currentDate.getTime());
        List<Temperature> result = new ArrayList<>();


      ///  if ((isBetween(time.toLocalTime(), LocalTime.of(1, 0), LocalTime.of(3, 0))) && !at2am) { // если 2 часа ночи
      //      at2am = true;
            result = mainService.addMeasure(newTemp.getTemp());
      //  }
       /* if ((isBetween(time.toLocalTime(), LocalTime.of(7, 0), LocalTime.of(9, 0))) && !at8am) { // если 8 утра
            at8am = true;
            result = mainService.addMeasure(newTemp.getTemp());
        }
        if ((isBetween(time.toLocalTime(), LocalTime.of(13, 0), LocalTime.of(15, 0))) && !at14) { // если 14 часов дня
            at14 = true;
            result = mainService.addMeasure(newTemp.getTemp());
        }
        if ((isBetween(time.toLocalTime(), LocalTime.of(18, 0), LocalTime.of(20, 0)))&& !at19) { // если 19 часов вечера
            at19 = true;
            result = mainService.addMeasure(newTemp.getTemp());
        }

        if (at2am && at8am && at14 && at19){

            at2am = false;
            at8am = false;
            at14 = false;
            at19 = false;
        }*/



        return result;
    }

    /**
     * добавить измерение температуры.
     *
     * @param measure
     * @return
     */
    @PostMapping("/addmeasure")
    public List<Temperature> addMeasure(@RequestBody Temperature measure) {

        return mainService.addNewMeasure(measure);
    }


    /**
     * Выдать статистику за сегодня.
     *
     * @return
     * @throws ParseException
     */
    @GetMapping("/today")
    public List<Temperature> getTodayMeasures() throws ParseException {
        return mainService.getTodayMeasures();
    }

    /**
     * Выдать статистику за неделю.
     *
     * @return
     * @throws ParseException
     */
    @GetMapping("/weekday")
    public List<DailyReport> getWeeklyReport() throws ParseException {
        return mainService.getWeeklyDayReport();
    }

    /**
     * Выдать статистику за месяц.
     *
     * @return
     * @throws ParseException
     */
    @GetMapping("/monthday")
    public List<DailyReport> getMonthReport() throws ParseException {
        return mainService.getMonthDayReport();
    }

    /**
     * Выдать текущий статус (состояние мониторинга).
     *
     * @return
     * @throws ParseException
     */
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

    /**
     * Добавить состояние мониторинга (для Ардуины).
     *
     * @param log
     * @return
     * @throws ParseException
     */
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

    /**
     * Добавить состояние мониторинга (для Ардуины).
     *
     * @param pushFromAbs
     * @return
     * @throws ParseException
     */
    @PostMapping("/addlog2")
    public ResponseEntity<String> addLog3(@RequestBody String pushFromAbs) throws ParseException {

        System.out.println(pushFromAbs);


        ResponseEntity<String> responseEntity = new ResponseEntity<>("my response body",
                HttpStatus.OK);
        return responseEntity;
    }


}
