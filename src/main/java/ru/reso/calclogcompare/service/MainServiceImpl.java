package ru.reso.calclogcompare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.reso.calclogcompare.livecontrolthread.MainParametrs;
import ru.reso.calclogcompare.model.DailyReport;
import ru.reso.calclogcompare.model.Logs;
import ru.reso.calclogcompare.model.Status;
import ru.reso.calclogcompare.model.Users;
import ru.reso.calclogcompare.repository.LogsRepository;
import ru.reso.calclogcompare.repository.UsersRepository;

import java.sql.Time;
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

    @Autowired
    private LogsRepository logsRepository;

    MainParametrs mainParametrs = new MainParametrs();


    @Override
    public List<Users> getAll() {
        return usersRepository.findAll();
    }

    /**
     * Выплюнуть все логи.
     *
     * @return
     */
    @Override
    public List<Logs> getAllLogs() {
        return logsRepository.findAll();
    }

    @Override
    public List<Users> addMeasure(Double temp) {
        usersRepository.save(new Users(temp));
        return usersRepository.findAll();
    }

    @Override
    public List<Users> addNewMeasure(Users measure) {
        usersRepository.save(measure);
        return usersRepository.findAll();
    }

    @Override
    public List<Users> getTodayMeasures() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        return usersRepository.getTodayMeasures(new SimpleDateFormat("yyyy-MM-dd").parse(dateFormat.format(date)));
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

        // Печатаем
        for (DailyReport temp : weeklyReport) {
            System.out.println(temp.toString());
        }

        return weeklyReport;
    }

    @Override
    public List<DailyReport> getWeeklyNightReport() throws ParseException {
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
                if (temp.getTimeCreated() != null) {
                    if (isBetween(temp.getTimeCreated().toLocalTime(), LocalTime.of(1, 0), LocalTime.of(3, 0))) { // если дневное
                        weeklyReport.add(new DailyReport(temp.getTest(), Double.valueOf(0), temp.getTemperature()));
                    }
                }
            }
        }

        return weeklyReport;
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
                if (temp.getTimeCreated() != null) {
                    if (isBetween(temp.getTimeCreated().toLocalTime(), LocalTime.of(1, 0), LocalTime.of(3, 0))) { // если дневное
                        weeklyReport.add(new DailyReport(temp.getTest(), Double.valueOf(0), temp.getTemperature()));
                    }
                }
            }
        }


        return weeklyReport;
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
                if (temp.getTimeCreated() != null) {
                    if (isBetween(temp.getTimeCreated().toLocalTime(), LocalTime.of(13, 0), LocalTime.of(15, 0))) { // если дневное
                        dayTemp = temp.getTemperature();
                        // weeklyReport.add(new DailyReport(temp.getTest(), temp.getTemperature(), Double.valueOf(0)));
                    }
                    if (isBetween(temp.getTimeCreated().toLocalTime(), LocalTime.of(1, 0), LocalTime.of(3, 0))) { // если дневное
                        //weeklyReport.add(new DailyReport(temp.getTest(), Double.valueOf(0), temp.getTemperature()));
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

    @Override
    public synchronized Boolean checkTimeout() {

        Date date = new Date();
        Time time = new Time(date.getTime());
        Boolean result = true;

        if (getLastContactTime() != null) { // время должно быть не ноль, иначе все наебнется
            System.out.println("local time (+15 min)- " + getLastContactTime());
            LocalTime offsetTime = toLocalTime(getLastContactTime()).plusMinutes(15);
            result = isBetween(toLocalTime(time), toLocalTime(getLastContactTime()), offsetTime);

        } else {
            System.out.println("local time (+15 min)- NULL");

            /**
             * TODO Триггер Постгреса
             *
             * Вообще, по хорошому, пустых полей (я имею ввиду lastContactTime быть не должно.
             * Но было бы не плохо, сделать что-то типа джоба, который такие поля все-таки бы искал и либо удалял,
             * либо например сделать триггер, типа если эти поля пустые, то ставим типа их текущей датой постгреса.
             */
        }


        return result;
    }

    @Override
    public Status getGlobalStatus() {

        Date date = new Date();
        Date date4PostStatus = new Date();
        Time time = new Time(date.getTime());
        Time time4PostStatus;
        Time PingTime4PostStatus;
        Status status;

        // Проверки на ноль времени

        if (mainParametrs.getStatus() == null) {

                time4PostStatus = new Time(date.getTime());
                PingTime4PostStatus = time4PostStatus;
                date4PostStatus = date;

        } else {


            if (mainParametrs.getStatus().getServerTime() == null) {
                time4PostStatus = new Time(date.getTime());
            } else {
                time4PostStatus = mainParametrs.getStatus().getServerTime();
            }

            if (mainParametrs.getLastPingTime() == null) {
                PingTime4PostStatus = time4PostStatus;
            } else {
                PingTime4PostStatus = mainParametrs.getLastPingTime();
            }

            if (mainParametrs.getStatus().getLastContactDate() == null) {
                date4PostStatus = date;
            } else {
                date4PostStatus = mainParametrs.getStatus().getLastContactDate();
            }
        }


        if ((!mainParametrs.isAcStatus() || !mainParametrs.isLanStatus())) {
            status = new Status(mainParametrs.isAcStatus(), mainParametrs.isLanStatus(), 0,
                    0, time4PostStatus, PingTime4PostStatus, 0, 0, 0, 0,
                    date4PostStatus);

        } else {
            status = new Status(mainParametrs.isAcStatus(), mainParametrs.isLanStatus(),
                    mainParametrs.getStatus().getLastTemperature(),
                    mainParametrs.getStatus().getLastHumidity(),
                    mainParametrs.getStatus().getServerTime(),
                    mainParametrs.getLastPingTime(),
                    mainParametrs.getStatus().getCurrent(),
                    mainParametrs.getStatus().getAmperage(),
                    mainParametrs.getStatus().getPower(),
                    mainParametrs.getStatus().getConsuming(),
                    mainParametrs.getStatus().getLastContactDate());

        }



        return status;
    }

    @Override
    public MainParametrs getMainParametrs() {
        return this.mainParametrs;
    }

    private static LocalTime toLocalTime(java.sql.Time time) {
        return time.toLocalTime();
    }

    @Override
    public Time getLastContactTime() {

        // Распечатаем всю выдачу


        System.out.println("Last Contact Time: " + ((logsRepository.getLastPingedEntry3(new PageRequest(0, 1, Sort.Direction.DESC, "servertime"))).get(0).getLastсontacttime()));

        return (logsRepository.getLastPingedEntry3(new PageRequest(0, 1, Sort.Direction.DESC, "servertime"))).get(0).getLastсontacttime();
    }

    /**
     * Добавить статус (это будет делать Ардуина).
     *
     * @param log
     * @return
     */
    @Override
    public List<Logs> addLog(Status log) throws ParseException {

        //Логгируем (добавляем) время сервера
        Date date = new Date();
        Time time = new Time(date.getTime());
        log.setServerTime(time);
        mainParametrs.setJustStartedSituation(false);
        mainParametrs.setLastPingTime(time);
        mainParametrs.setStatus(log);
        mainParametrs.setAcStatus(true);
        mainParametrs.setLanStatus(true);
        if (log.getWho().isEmpty()) log.setWho("REST");

        // TODO:  нам вот здесь хорошо бы проверить на ноль что-нибудь (надо подумать что: скорее всего даты)

        logsRepository.save(new Logs(log));
        return logsRepository.findAll();
    }
}
