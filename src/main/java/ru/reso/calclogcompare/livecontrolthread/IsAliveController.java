package ru.reso.calclogcompare.livecontrolthread;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.reso.calclogcompare.model.Logs;
import ru.reso.calclogcompare.model.Status;
import ru.reso.calclogcompare.service.MainService;
import java.sql.Time;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class IsAliveController implements Runnable {


    MainService mainService;


    public IsAliveController(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    public void run() {

        System.out.println("Thread will started....");

        boolean ret = true;

        if (mainService != null) {



            while (ret) {

                try {

                    System.out.println("Получили");
                    System.out.println("Тестим Чек-таймаут - " + checkTimeout());
                    Date date = new Date();
                    Time time = new Time(date.getTime());

                    System.out.println("Параметры MainParametrs. AC - " + mainService.getMainParametrs().isAcStatus());
                    System.out.println("Параметры MainParametrs. LAN - " + mainService.getMainParametrs().isLanStatus());
                    System.out.println("is logged?  " + mainService.getMainParametrs().isLogged());


                    if (!checkTimeout() && (!mainService.getMainParametrs().isLogged()) &&
                            (mainService.getMainParametrs().isAcStatus() || mainService.getMainParametrs().isLanStatus())) { // Хьюстон, у нас проблемы.....

                        System.out.println("Хьюстон, у нас проблемы.....");
                        Status log = new Status("REST: NO PING", false, false, 0, 0, time, time,
                                0, 0, 0, 0, new Date());


                        try {
                            List<Logs> tempLogs = mainService.addLog(log);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        mainService.getMainParametrs().setLogged(true);
                        mainService.getMainParametrs().setAcStatus(false);
                        mainService.getMainParametrs().setLanStatus(false);
                        mainService.getMainParametrs().setJustStartedSituation(false);

                    } else if ((mainService.getMainParametrs().isAcStatus() || mainService.getMainParametrs().isLanStatus())) {

                        System.out.println("Проблема решена или ее и не было");
                        mainService.getMainParametrs().setLogged(false);
                        //  ret = false;
                    } else if (!checkTimeout() && !mainService.getMainParametrs().isLogged() &&
                            (!mainService.getMainParametrs().isAcStatus() || !mainService.getMainParametrs().isLanStatus())
                            ) { // стартовая ситуация
                        System.out.println("Стартовая ситуация");
                    }

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } catch (BeanCreationException exception) {
                    System.out.println("Косяк");
                }
            }
        }
    }


    public Boolean checkTimeout() {

        Date date = new Date();
        Time time = new Time(date.getTime());
        Boolean result = true;


        if (mainService.getMainParametrs().getLastPingTime() != null) { // время должно быть не ноль, иначе все наебнется

            // надо еще проверить, чтобы дата была именно сегодняшняя

            System.out.println("local time (-15 min)- " + mainService.getMainParametrs().getLastPingTime());
            LocalTime offsetTime = toLocalTime(mainService.getMainParametrs().getLastPingTime()).plusMinutes(15);
            System.out.println("OFFSET time (+15 min)- " + offsetTime);
            System.out.println("CURRENT time- " + time);
            result = isBetween(toLocalTime(time), toLocalTime(mainService.getMainParametrs().getLastPingTime()), offsetTime);
        }

        System.out.println("РЕЗУЛЬТАТ ПРОВЕРКИ ТАЙМАУТА = " + result);
        return result;
    }

    public static boolean isBetween(LocalTime candidate, LocalTime start, LocalTime end) {
        return !candidate.isBefore(start) && !candidate.isAfter(end);  // Inclusive.
    }

    private static LocalTime toLocalTime(java.sql.Time time) {
        return time.toLocalTime();
    }


}
