package com.antonromanov.springhibernate.controller;

import com.antonromanov.springhibernate.DAO.PremiumDAO;
import com.antonromanov.springhibernate.model.LocalEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/rest/api")
public class MainRestController {

    @Autowired
    private PremiumDAO mainDao;

    @GetMapping("/get")
    public LocalEntity getAll() {
        System.out.println("Запрос прошел");

        return mainDao.getEntity();
    }

    @GetMapping("/gettomcat")
    public @ResponseBody String getTomcat() {
        System.out.println("Запрос Tomcat прошел");
        System.out.println(mainDao.getEntity().getTestString());
        return mainDao.getEntity().getTestString();
    }


    @GetMapping("/gettest")
    public LocalEntity getTest() {
        mainDao.setTest();
        return mainDao.getEntity();
    }


    @PostMapping("/add")
    public LocalEntity addMeasure(@RequestBody LocalEntity measure) {
        System.out.println(measure.getTestString());
        return mainDao.addNewMeasure(measure);
    }



    @RequestMapping(value = "/addtomcat", method = RequestMethod.POST)
    public ResponseEntity<Void> addMeasureTomcat(@RequestBody LocalEntity measure) {
        System.out.println("Запрос на добавление прошел");
        System.out.println(measure.getTestString());
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }
}
