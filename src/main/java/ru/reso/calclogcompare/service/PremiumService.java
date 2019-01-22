package ru.reso.calclogcompare.Service;

import ru.reso.calclogcompare.model.Premium;
import java.util.List;


/**
 * Сервис, обслуживающий DAO.
 */
public interface PremiumService {

    /**
     * Выдать премию по id
     * через findOne.
     *
     * @param id - id премии.
     * @return - премия.
     */
    Premium getPremById(Long id);

    /**
     * Выдать все премии.
     *
     * @return список премий.
     */
    List<Premium> getAllInString();

    /**
     * Выдать конкретную премию
     * по id через @Query.
     *
     * @param id - id премии.
     * @return - премия.
     */
    Premium getPremById2(Integer id);

}
