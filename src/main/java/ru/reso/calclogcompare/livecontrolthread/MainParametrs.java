package ru.reso.calclogcompare.livecontrolthread;

import ru.reso.calclogcompare.model.Status;

import java.sql.Time;

public class  MainParametrs {

    private boolean acStatus;
    private boolean lanStatus;
    private volatile Status status;
    private boolean logged;
    private boolean timeIsOver;
    private Time lastPingTime;
    private boolean justStartedSituation;



    public boolean isAcStatus() {
        return acStatus;
    }

    public void setAcStatus(boolean acStatus) {
        this.acStatus = acStatus;
    }

    public boolean isLanStatus() {
        return lanStatus;
    }

    public void setLanStatus(boolean lanStatus) {
        this.lanStatus = lanStatus;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public boolean isTimeIsOver() {
        return timeIsOver;
    }

    public void setTimeIsOver(boolean timeIsOver) {
        this.timeIsOver = timeIsOver;
    }

    public Time getLastPingTime() {
        return lastPingTime;
    }

    public void setLastPingTime(Time lastPingTime) {
        this.lastPingTime = lastPingTime;
    }

    public boolean isJustStartedSituation() {
        return justStartedSituation;
    }

    public void setJustStartedSituation(boolean justStartedSituation) {
        this.justStartedSituation = justStartedSituation;
    }
}
