package com.antonromanov.temperaturerest.controller;

import com.antonromanov.temperaturerest.utils.JSONTemplate;
import com.antonromanov.temperaturerest.utils.TimeSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.antonromanov.temperaturerest.model.*;
import com.antonromanov.temperaturerest.service.MainService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static com.antonromanov.temperaturerest.utils.Utils.*;

/**
 * Основной REST-контроллер приложения.
 */
@RestController
@RequestMapping("/rest/users")
public class MainRestController {


    private static final Logger LOGGER = Logger.getLogger(MainRestController.class.getName());
    List<Temperature> allTemperatures = new ArrayList<>();


    //todo: надо поменять название проекта
    //todo: надо поменять названия классов и переменных
    //todo: надо поменять адрес REST API
    //todo: прикрутить телеграмм-бота
    //todo: надо поменять еще вот эту ссылку - http://localhost:8080/FirstSPRINGJDBC-2.0-SNAPSHOT/rest/api/add. Чо за ФёрстСпрингДжейДиБиСи ????!!!!!!!
    //todo:  логгирование в бд с настройкой удаления старых записей.
    //todo:  прикрутить экран к ардуине
    // todo:  прикрутить лампочки и вывод инфы (например, флаги) на OLED.
    // todo: удалить поле test из temperature_copy
    // todo: temperature_copy переименовать в нормальную
    // todo: переименовать и перенести все модели в Ангуляре, например что за User.ts - ????
    // todo: переименовать и перенести (Ангуляр) (this.year и this.count)
    // todo: переименовать методы типа addlog3. Ну что это за пипец.....


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
    private boolean at2am = false;
    private boolean at8am = false;
    private boolean at14 = false;
    private boolean at19 = false;

    // todo: этот метод вообще надо убрать
    /**
     * Выдать все измерения температуры.
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

    // todo: разобраться - чо за метод ваще
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
    public ResponseEntity<String> getTodayMeasures(HttpServletRequest request) throws ParseException {

        List<Temperature> todayList = mainService.getTodayMeasures();

        String remoteAddr = "";

        LOGGER.warning("========= TODAY MEASURES LIST ============== ");

        // todo: вынести в отдельный метод
        // Пытаемся взять ip
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
                LOGGER.warning("GETTING REQUEST FROM:  " + remoteAddr);
            }
        }

        // todo: вынести в отдельный метод
        // Формируем JSON
        JsonObject responseStatusInJson = JSONTemplate.create()
                .add("AllTemperatures", todayList.size())
                .add("NightPost", at2am)
                .add("MorningPost", at8am)
                .add("DayPost", at14)
                .add("EveningPost", at19).getJson();

        LOGGER.warning("RESULT:  " + responseStatusInJson.toString());

        // todo: вынести в отдельный метод класса Utils
        Gson gson = new GsonBuilder()
                .setDateFormat("dd/MM/yyyy")
                .registerTypeAdapter(java.sql.Time.class, new TimeSerializer())
                .create();

        String result = gson.toJson(todayList);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl("no-cache");

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(result, headers, HttpStatus.OK);

        return responseEntity;
    }


    /**
     * Выдать статистику за неделю.
     *
     * @return
     * @throws ParseException
     */
    @GetMapping("/week")
    public ResponseEntity<String> getWeeklyReport(HttpServletRequest request) throws ParseException {

        List<DailyReport> weekList = mainService.getWeeklyDayReport();

        String remoteAddr = "";

        LOGGER.warning("========= WEEK MEASURES LIST ============== ");

        // todo: вынести в отдельный метод
        // Пытаемся взять ip
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
                LOGGER.warning("GETTING REQUEST FROM:  " + remoteAddr);
            }
        }

        // todo: вынести в отдельный метод
        // Формируем JSON
        JsonObject responseStatusInJson = JSONTemplate.create()
                .add("AllTemperatures", weekList.size())
                .add("NightPost", at2am)
                .add("MorningPost", at8am)
                .add("DayPost", at14)
                .add("EveningPost", at19).getJson();

        LOGGER.warning("RESULT:  " + responseStatusInJson.toString());

        // todo: вынести в отдельный метод класса Utils
        Gson gson = new GsonBuilder()
                .setDateFormat("dd/MM/yyyy")
                .registerTypeAdapter(java.sql.Time.class, new TimeSerializer())
                .create();

        String result = gson.toJson(weekList);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl("no-cache");

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(result, headers, HttpStatus.OK);


        return responseEntity;
    }

    /**
     * Выдать статистику за месяц (старый месяц).
     *
     * @return
     * @throws ParseException
     */
    @GetMapping("/monthday_old")
    public List<DailyReport> getMonthReport_Old() throws ParseException {
        return mainService.getMonthDayReport();
    }

    /**
     * Выдать статистику за месяц.
     *
     * @return
     * @throws ParseException
     */
    @GetMapping("/month")
    public ResponseEntity<String> getMonthReport(HttpServletRequest request) throws ParseException {

        List<DailyReport> monthList = mainService.getMonthDayReport();

        String remoteAddr = "";

        LOGGER.warning("========= MONTH MEASURES LIST ============== ");

        // todo: вынести в отдельный метод
        // Пытаемся взять ip
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
                LOGGER.warning("GETTING REQUEST FROM:  " + remoteAddr);
            }
        }

        // todo: вынести в отдельный метод
        // Формируем JSON
        JsonObject responseStatusInJson = JSONTemplate.create()
                .add("AllTemperatures", monthList.size())
                .add("NightPost", at2am)
                .add("MorningPost", at8am)
                .add("DayPost", at14)
                .add("EveningPost", at19).getJson();

        LOGGER.warning("RESULT:  " + responseStatusInJson.toString());

        // todo: вынести в отдельный метод класса Utils
        Gson gson = new GsonBuilder()
                .setDateFormat("dd/MM/yyyy")
                .registerTypeAdapter(java.sql.Time.class, new TimeSerializer())
                .create();

        String result = gson.toJson(monthList);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl("no-cache");

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(result, headers, HttpStatus.OK);



        return responseEntity;
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

      /*

        BODY:


        {
            "who": null,
                "lastTemperature": -15,
                "lastHumidity": -10,
                "serverTime": null,
                "lastContactTime": null,
                "current": 15,
                "amperage": 150,
                "power": 18,
                "consuming": 1024,
                "lastContactDate": null,
                "acOn": true,
                "lanOn": true
        }*/


        LOGGER.warning("Добавляем запись");

        Date date = new Date();
        Time time = new Time(date.getTime());

        log.setLastContactTime(time);
        log.setServerTime(time);
        log.setLastContactDate(date);

        LOGGER.warning(log.toString());

        return mainService.addLog(log);
    }

    /**
     * Добавить состояние мониторинга (для Ардуины).
     *
     * @param
     * @return
     * @throws ParseException
     */
    @GetMapping("/testlog")
    public Status testLOg() throws ParseException {

        LOGGER.warning("Тестовый лог");

        /*Date date = new Date();
        Time time = new Time(date.getTime());

        log.setLastContactTime(time);
        log.setServerTime(time);
        log.setLastContactDate(date);*/

       // LOGGER.warning(log.toString());

        return new Status();
    }

    /**
     * Добавить состояние мониторинга (для Ардуины).
     * Отсылаем с Ардуины - {"temp":120}
     *
     * @param requestParam - json от Ардуины
     * @return
     * @throws ParseException
     */
    @PostMapping("/add")
    public ResponseEntity<String> addLog3(@RequestBody String requestParam, HttpServletRequest request, HttpServletResponse resp) throws ParseException {

        Date currentDate = new Date();
        Time time = new Time(currentDate.getTime());
        String remoteAddr = "";

        LOGGER.warning("We are in POST HTTP: " + requestParam);


        // Пытаемся взять ip
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
                LOGGER.warning("GETTING REQUEST FROM:  " + remoteAddr);
            }
        }

        allTemperatures = mainService.getAll();

        // Формируем JSON
        JsonObject responseStatusInJson = JSONTemplate.create()
                .add("AllTemperatures", allTemperatures.size())
                .add("ip", remoteAddr)
                .add("NightPost", at2am)
                .add("MorningPost", at8am)
                .add("DayPost", at14)
                .add("EveningPost", at19).getJson();

        // Парсим пришедший JSON  с температурой
        try {

            Double temp = JSONTemplate.fromString(requestParam).get("temp").getAsDouble();

            if ((isBetween(time.toLocalTime(), LocalTime.of(1, 0), LocalTime.of(3, 0))) && !at2am) { // если 2 часа ночи
                at2am = true;

                // Проверяем, что такой температуры нет еще за сегодня
                if (!isAlreadyWriten(mainService.getTodayMeasures(), 1, 3)) {
                    allTemperatures = mainService.addMeasure(temp, responseStatusInJson.toString());
                    LOGGER.warning("POST NIGHT TEMPERATURE --------- SUCCESS:  " + time.toLocalTime());
                } else {
                    LOGGER.warning("POST NIGHT TEMPERATURE --------- FAIL - DUPLICATE MEASURE:  " + time.toLocalTime());
                }
            }
            if ((isBetween(time.toLocalTime(), LocalTime.of(7, 0), LocalTime.of(9, 0))) && !at8am) { // если 8 утра
                at8am = true;

                // Проверяем, что такой температуры нет еще за сегодня
                if (!isAlreadyWriten(mainService.getTodayMeasures(), 7, 9)) {
                    allTemperatures = mainService.addMeasure(temp, responseStatusInJson.toString());
                    LOGGER.warning("POST MORNING TEMPERATURE --------- SUCCESS:  " + time.toLocalTime());
                } else {
                    LOGGER.warning("POST MORNING TEMPERATURE --------- FAIL - DUPLICATE MEASURE:  " + time.toLocalTime());
                }
            }
            if ((isBetween(time.toLocalTime(), LocalTime.of(13, 0), LocalTime.of(15, 0))) && !at14) { // если 14 часов дня
                at14 = true;

                // Проверяем, что такой температуры нет еще за сегодня
                if (!isAlreadyWriten(mainService.getTodayMeasures(), 13, 15)) {
                    allTemperatures = mainService.addMeasure(temp, responseStatusInJson.toString());
                    LOGGER.warning("POST DAY TEMPERATURE --------- SUCCESS:  " + time.toLocalTime());
                } else {
                    LOGGER.warning("POST DAY TEMPERATURE --------- FAIL - DUPLICATE MEASURE:  " + time.toLocalTime());
                }

            }
            if ((isBetween(time.toLocalTime(), LocalTime.of(18, 0), LocalTime.of(20, 0))) && !at19) { // если 19 часов вечера
                at19 = true;

                // Проверяем, что такой температуры нет еще за сегодня
                if (!isAlreadyWriten(mainService.getTodayMeasures(), 18, 20)) {
                    allTemperatures = mainService.addMeasure(temp, responseStatusInJson.toString());
                    LOGGER.warning("POST EVENING TEMPERATURE --------- SUCCESS:  " + time.toLocalTime());
                } else {
                    LOGGER.warning("POST EVENING TEMPERATURE --------- FAIL - DUPLICATE MEASURE:  " + time.toLocalTime());
                }
            }

            if (at2am && at8am && at14 && at19) {

                at2am = false;
                at8am = false;
                at14 = false;
                at19 = false;
            }
        } catch (JsonParseException ex) {

            try {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ошибка парсинга JSON");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LOGGER.warning("RESULT:  " + responseStatusInJson.toString());

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseStatusInJson.toString(), HttpStatus.OK);
        return responseEntity;
    }

    /**
     * Добавить состояние мониторинга (для Ардуины).
     * Отсылаем с Ардуины - {"temp":120}
     *
     * @param requestParam - json от Ардуины
     * @return
     * @throws ParseException
     */
    @PostMapping("/addTest")
    public ResponseEntity<String> addLogTest(@RequestBody String requestParam, HttpServletRequest request, HttpServletResponse resp) throws ParseException {

        Date currentDate = new Date();
        Time time = new Time(currentDate.getTime());
        String remoteAddr = "";

        LOGGER.warning("We are in POST HTTP: " + requestParam);


        // Пытаемся взять ip
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
                LOGGER.warning("GETTING REQUEST FROM:  " + remoteAddr);
            }
        }

        allTemperatures = mainService.getAll();

        // Формируем JSON
        JsonObject responseStatusInJson = JSONTemplate.create()
                .add("AllTemperatures", allTemperatures.size())
                .add("ip", remoteAddr)
                .add("NightPost", at2am)
                .add("MorningPost", at8am)
                .add("DayPost", at14)
                .add("EveningPost", at19).getJson();

        // Парсим пришедший JSON  с температурой
        try {

            Double temp = JSONTemplate.fromString(requestParam).get("temp").getAsDouble();


                    allTemperatures = mainService.addMeasure(temp, responseStatusInJson.toString());
                    LOGGER.warning("POST TEST TEMP TEMPERATURE --------- SUCCESS:  " + time.toLocalTime());

        } catch (JsonParseException ex) {

            try {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ошибка парсинга JSON");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LOGGER.warning("RESULT:  " + responseStatusInJson.toString());

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseStatusInJson.toString(), HttpStatus.OK);
        return responseEntity;
    }


}
