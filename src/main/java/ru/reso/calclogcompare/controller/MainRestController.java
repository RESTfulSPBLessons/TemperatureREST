package ru.reso.calclogcompare.controller;

// This class created by Anton Romanov 03.12.2018 at 11:44
// Git Hub repo - ...
// https://192.*.*.*:33246/RCCT-2.0-SNAPSHOT/rest/getone?id=1
// http://localhost:8083/RCCT-2.0-SNAPSHOT/rest/check?id=1


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.reso.calclogcompare.model.*;
import ru.reso.calclogcompare.service.MainService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/users")
public class MainRestController {


    @Autowired
    MainService mainService;

    @GetMapping("/all")
    public List<Users> getAll(){
        System.out.println("Запрос прошел");

        for(Users temp : mainService.getAll()){
            System.out.println(temp.getId() + " - " + temp.getTemperature() + " - " + temp.getDateCreated());
        }


        return  mainService.getAll();
    }


    @RequestMapping(method = RequestMethod.GET, path = "/v2")
    public ArrayList<String> getAll2() {

        System.out.println("I have got you!");

        ArrayList<String> a = new ArrayList<>();

        a.add("1");
        a.add("2");


        return  a;

    }


    @PostMapping("/add")
    public List<Users> newMeasure(@RequestBody PR newTemp) {
        System.out.println("we are in POST HTTP");

        return mainService.addMeasure(newTemp.getTemp());
    }


    @RequestMapping(method = RequestMethod.GET, path = "/test")
    public PR testPost() {

        System.out.println("get POST object");

        PR testObj = new PR(Double.valueOf(123));



        return  testObj;

    }




}
