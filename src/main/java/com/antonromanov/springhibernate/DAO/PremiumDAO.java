package com.antonromanov.springhibernate.DAO;

import com.antonromanov.springhibernate.model.LocalEntity;

import java.util.List;

/**
 *  Основное DAO.
 */
public interface PremiumDAO {

    LocalEntity getEntity();
    void createLocalEntity();
    LocalEntity addNewMeasure(LocalEntity localEntity);
    void setTest();

}
