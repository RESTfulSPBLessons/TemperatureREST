package ru.reso.calclogcompare.livecontrolthread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.reso.calclogcompare.service.MainService;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class ThreadManager {

    @Autowired
    MainService mainService;



    /*public ThreadManager(MainService mainService) {
        this.mainService = mainService;
    }*/


    @PostConstruct
    public void init() {

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

        if (mainService!=null){
            IsAliveController task = new IsAliveController(mainService);
            System.out.println("Thread will start");
            mainService.getMainParametrs().setLogged(false);
            mainService.getMainParametrs().setJustStartedSituation(true);
            mainService.getMainParametrs().setTimeIsOver(true);
            mainService.getMainParametrs().setLanStatus(false);
            mainService.getMainParametrs().setAcStatus(false);

            System.out.println("T I M E ======" + mainService.getLastContactTime());

            if (mainService.getLastContactTime()!=null) {
                mainService.getMainParametrs().setLastPingTime(mainService.getLastContactTime());

                //mainService.getMainParametrs().getStatus().setLastContactTime(mainService.getLastContactTime());
            }

            executor.execute(task);

        } else {
            System.out.println("mainService==null (Manager)");
        }



    }



}
