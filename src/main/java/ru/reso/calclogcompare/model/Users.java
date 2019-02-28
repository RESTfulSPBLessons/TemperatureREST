package ru.reso.calclogcompare.model;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import org.hibernate.annotations.Type;

@Entity
@Table(name="temperature_copy", schema = "arduino", catalog = "postgres")
public class Users {

    @Id
    @Column(name="id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "temp_seq_gen")
    @SequenceGenerator(name = "temp_seq_gen", sequenceName ="arduino.temp_id_seq")
    private Integer id;


    @Column(name="name")
    private String name;

    @Column(name="temperature")
    private Double temperature;


    @Column(name = "datecreated", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    // @Type(type="date")
    private Date dateCreated;

    @Column(name="timecreated")
    @Temporal(TemporalType.TIME)
    @Type(type="time")
    private Time timeCreated;


    @Column(name = "test")
    @Temporal(TemporalType.DATE)
    private Date test;


    public Users() {
    }

    public Users(Double newTemp) {
        this.temperature = newTemp;
        this.name = "from REST";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Time getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Time timeCreated) {
        this.timeCreated = timeCreated;
    }


    public Date getTest() {
        return test;
    }

    public void setTest(Date test) {
        this.test = test;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", temperature=" + temperature +
                ", dateCreated=" + dateCreated +
                ", timeCreated=" + timeCreated +
                '}';
    }
}