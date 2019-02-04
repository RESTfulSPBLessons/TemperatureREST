package ru.reso.calclogcompare.repository;


import org.springframework.stereotype.Repository;
import ru.reso.calclogcompare.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;


@Repository
public interface UsersRepository extends JpaRepository<Users, Integer>{

    /*@Query(value = "SELECT s  FROM Users s where s.dateCreated > :creationDateTime")
    List<Users> getTodayMeasures(@Param("creationDateTime") Date creationDateTime);*/


   /* @Query(value = "SELECT s  FROM Users s where s.dateCreated = :creationDateTime")
    List<Users> getTodayMeasures(@Param("creationDateTime") Date creationDateTime);*/

    @Query(value = "SELECT s  FROM Users s where s.test = :creationDateTime order by s.timeCreated ASC")
    List<Users> getTodayMeasures(@Param("creationDateTime") Date creationDateTime);


   /* @Query(value = "SELECT s  FROM Users s where s.dateCreated = '2019-01-30'")
    List<Users> getTodayMeasures();*/


    /*@Query(value = "SELECT s  FROM Users s where s.dateCreated = :creationDateTime")
    List<Users> getTodayMeasures(@Param("creationDateTime") String creationDateTime);
*/

    @Query(value = "SELECT s  FROM Users s where s.dateCreated > :creationDateTime order by s.dateCreated ASC")
    List<Users> getWeekMeasures(@Param("creationDateTime") Date creationDateTime);



}
