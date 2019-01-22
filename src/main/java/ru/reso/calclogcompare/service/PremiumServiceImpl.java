package ru.reso.calclogcompare.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.reso.calclogcompare.DAO.PremiumDAO;
import ru.reso.calclogcompare.model.Premium;
import java.util.ArrayList;
import java.util.List;


/**
 * Сервис, реализующий основной
 * персистент функционал.
 */
@Service
public class PremiumServiceImpl implements PremiumService {

    /**
     * Основной ДАО репозиторий.
     */
    @Autowired
    private PremiumDAO daoRepository;

    /**
     * Выдать премию по id
     * базовым методом findOne.
     *
     * @param id - id премии.
     * @return - Премия.
     */
    public Premium getPremById(final Long id) {
        return daoRepository.findOne(id);
    }

    /**
     * Выдать все премии
     * через @Query.
     *
     * @return - список Премий.
     */
    @Override
    public List<Premium> getAllInString() {
        List<Premium> result = new ArrayList<>();


        for (String name : daoRepository.getAllSerials()) {
            result.add(new Premium(name));
        }

        return result;
    }

    /**
     * Выдать премию по id
     * через @Query.
     *
     * @return - Премия.
     */
    @Override
    public Premium getPremById2(final Integer id) {
        return daoRepository.getPremiumById(Long.valueOf(1));
    }
}
