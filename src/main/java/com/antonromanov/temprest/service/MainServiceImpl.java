package com.antonromanov.temprest.service;

import com.antonromanov.temprest.model.Status;
import org.springframework.stereotype.Service;

/**
 * Основной сервис. Тут вся бизнес логика (кроме потока мониторинга).
 */
@Service
public class MainServiceImpl implements MainService {


    /**
     * Выдать состояние мониторинга.
     *
     *
     * @return
     */
    @Override
    public Status getGlobalStatus() {

        Status status = new Status("test", true,true, 10, 10,
                null, null, 10,12,12,12, null);

        return status;
    }


}
