package ru.reso.calclogcompare.DAO;

import ru.reso.calclogcompare.model.Premium;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Основное DAO.
 */
@Repository
@Transactional
public interface PremiumDAO extends JpaRepository<Premium, Long> {

    /**
     * Выводим все записи с помощью @Query.
     *
     * @return - Список имен
     */
    @Query(value = "SELECT distinct s.name FROM Premium s")
    List<String> getAllSerials();


    /**
     * Выдаем одну конкретную
     * запись по id.
     *
     * @return - конкретная премия.
     */
    @Query(value = "select s from Premium s WHERE s.id = :myid")
    Premium getPremiumById(@Param("myid") Long param);


}
