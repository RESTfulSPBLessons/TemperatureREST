package com.antonromanov.temperaturerest.repository;


import org.springframework.stereotype.Repository;
import com.antonromanov.temperaturerest.model.Temperature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;

/**
 * Репозиторий, отвечающий за выдачу температуры
 */
@Repository
public interface TemperatureRepository extends JpaRepository<Temperature, Integer>{

    @Query(value = "SELECT s  FROM Temperature s where s.test = :creationDateTime order by s.timeCreated ASC")
    List<Temperature> getTodayMeasures(@Param("creationDateTime") Date creationDateTime);


    @Query(value = "SELECT s  FROM Temperature s where s.dateCreated > :creationDateTime order by s.dateCreated ASC")
    List<Temperature> getWeekMeasures(@Param("creationDateTime") Date creationDateTime);


}
