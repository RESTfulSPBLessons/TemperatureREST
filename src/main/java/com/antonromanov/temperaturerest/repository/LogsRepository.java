package com.antonromanov.temperaturerest.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.antonromanov.temperaturerest.model.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Репозиторий для работы с логами (есть АС, Lan или нет?
 * А так же вольтаж, ампраж и прочее....
 */
@Repository
public interface LogsRepository extends JpaRepository<Logs, Integer>{

    /*@Query(value = "SELECT s  FROM Users s where s.test = :creationDateTime order by s.timeCreated ASC")
    List<Users> getTodayMeasuresOld(@Param("creationDateTime") Date creationDateTime);

*/
    @Query(value="select l from Logs l order by l.lastсontactdate DESC")
    List<Logs> getLastPingedEntry(Pageable pageable);

    @Query(value="select l from Logs l order by l.lastсontactdate DESC, l.lastсontacttime DESC")
    List<Logs> getLastPingedEntry2();

    @Query(value="select l from Logs l order by l.lastсontactdate DESC, l.lastсontacttime DESC")
    List<Logs> getLastPingedEntry3(Pageable pageable);


}
