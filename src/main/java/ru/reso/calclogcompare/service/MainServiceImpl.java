package ru.reso.calclogcompare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.reso.calclogcompare.model.Users;
import ru.reso.calclogcompare.repository.UsersRepository;

import java.util.List;

@Service
public class MainServiceImpl implements MainService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public List<Users> getAll() {
        return usersRepository.findAll();
    }

    @Override
    public List<Users> addMeasure(Double temp) {
        usersRepository.save(new Users(temp));
        return usersRepository.findAll();
    }
}
