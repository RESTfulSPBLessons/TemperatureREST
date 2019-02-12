package com.antonromanov.springhibernate.DAO;

import com.antonromanov.springhibernate.model.LocalEntity;
import org.springframework.stereotype.Service;


@Service
public class PremiumDAOImpl implements PremiumDAO {

    private LocalEntity localEntity;


    public PremiumDAOImpl() {
        createLocalEntity();
    }

    @Override
    public LocalEntity getEntity() {
        return this.localEntity;
    }

    @Override
    public void createLocalEntity() {
        this.localEntity = LocalEntity.getInstance();
    }

    @Override
    public LocalEntity addNewMeasure(LocalEntity localEntity) {
        this.localEntity.setId(localEntity.getId());
        this.localEntity.setTestString(localEntity.getTestString());
        return this.localEntity;
    }

    @Override
    public void setTest() {
        this.localEntity.setId(105);
        this.localEntity.setTestString("Test");
    }
}
