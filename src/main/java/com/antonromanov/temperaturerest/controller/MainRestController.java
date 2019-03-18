package com.antonromanov.temperaturerest.controller;

import com.antonromanov.temperaturerest.utils.JSONTemplate;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    //todo: влить core-ui project уже
    //todo: надо поменять названия классов и переменных
    //todo: надо поменять адрес REST API
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

        // Парсим пришедший JSON  с температурой
        try {

            Double temp = JSONTemplate.fromString(requestParam).get("temp").getAsDouble();

            if ((isBetween(time.toLocalTime(), LocalTime.of(1, 0), LocalTime.of(3, 0))) && !at2am) { // если 2 часа ночи
                at2am = true;

                // Проверяем, что такой температуры нет еще за сегодня
                if (!isAlreadyWriten(mainService.getTodayMeasures(), 1, 3)) {
                    allTemperatures = mainService.addMeasure(temp);
                    LOGGER.warning("POST NIGHT TEMPERATURE --------- SUCCESS:  " + time.toLocalTime());
                } else {
                    LOGGER.warning("POST NIGHT TEMPERATURE --------- FAIL - DUPLICATE MEASURE:  " + time.toLocalTime());
                }
            }
        if ((isBetween(time.toLocalTime(), LocalTime.of(7, 0), LocalTime.of(9, 0))) && !at8am) { // если 8 утра
            at8am = true;

            // Проверяем, что такой температуры нет еще за сегодня
            if (!isAlreadyWriten(mainService.getTodayMeasures(), 7, 9)) {
                allTemperatures = mainService.addMeasure(temp);
                LOGGER.warning("POST MORNING TEMPERATURE --------- SUCCESS:  " + time.toLocalTime());
            } else {
                LOGGER.warning("POST MORNING TEMPERATURE --------- FAIL - DUPLICATE MEASURE:  " + time.toLocalTime());
            }
        }
        if ((isBetween(time.toLocalTime(), LocalTime.of(13, 0), LocalTime.of(15, 0))) && !at14) { // если 14 часов дня
            at14 = true;

            // Проверяем, что такой температуры нет еще за сегодня
            if (!isAlreadyWriten(mainService.getTodayMeasures(), 13, 15)) {
                allTemperatures = mainService.addMeasure(temp);
                LOGGER.warning("POST DAY TEMPERATURE --------- SUCCESS:  " + time.toLocalTime());
            } else {
                LOGGER.warning("POST DAY TEMPERATURE --------- FAIL - DUPLICATE MEASURE:  " + time.toLocalTime());
            }

        }
        if ((isBetween(time.toLocalTime(), LocalTime.of(18, 0), LocalTime.of(20, 0)))&& !at19) { // если 19 часов вечера
            at19 = true;

            // Проверяем, что такой температуры нет еще за сегодня
            if (!isAlreadyWriten(mainService.getTodayMeasures(), 18, 20)) {
                allTemperatures = mainService.addMeasure(temp);
                LOGGER.warning("POST EVENING TEMPERATURE --------- SUCCESS:  " + time.toLocalTime());
            } else {
                LOGGER.warning("POST EVENING TEMPERATURE --------- FAIL - DUPLICATE MEASURE:  " + time.toLocalTime());
            }
        }

        if (at2am && at8am && at14 && at19){

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

        // Формируем JSON
        JsonObject responseStatusInJson = JSONTemplate.create()
                .add("AllTemperatures", allTemperatures.size())
                .add("NightPost", at2am)
                .add("MorningPost", at8am)
                .add("DayPost", at14)
                .add("EveningPost", at19).getJson();

        LOGGER.warning("RESULT:  " + responseStatusInJson.toString());

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseStatusInJson.toString(), HttpStatus.OK);
        return responseEntity;
    }
}
