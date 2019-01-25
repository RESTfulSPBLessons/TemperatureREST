package ru.reso.calclogcompare.service;

import org.springframework.stereotype.Service;
import ru.reso.calclogcompare.model.Users;
import java.util.List;

@Service
public interface MainService {

     List<Users> getAll();


     List<Users> addMeasure(Double temp);

}
