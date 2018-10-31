package com.antonromanov.pf.springjpahibernatear.resource;

import com.antonromanov.pf.springjpahibernatear.model.PR;
import com.antonromanov.pf.springjpahibernatear.model.Users;
import com.antonromanov.pf.springjpahibernatear.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/users")
public class UserResource {

    @Autowired
    UsersRepository usersRepository;

    @GetMapping("/all")
    public List<Users> getAll(){
return  usersRepository.findAll();
    }

    @PostMapping("/add")
    public Users newMeasure(@RequestBody PR newTemp) {
        return usersRepository.save(new Users(newTemp.getTemp()));
    }



}
