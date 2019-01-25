package ru.reso.calclogcompare.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name="temperature", schema = "arduino", catalog = "postgres")
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

   /* @Column(name="datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    @Type(type="date")
    private Date dateCreated;*/

    @Basic
    @Column(name = "datecreated", nullable = true)
    private Date dateCreated;




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

}
