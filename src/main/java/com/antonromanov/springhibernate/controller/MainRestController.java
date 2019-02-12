package com.antonromanov.springhibernate.controller;

import java.util.List;

import com.antonromanov.springhibernate.DAO.PremiumDAO;
import com.antonromanov.springhibernate.model.LocalEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("/rest/api")
public class MainRestController {

    @Autowired
    private PremiumDAO mainDao;



    @RequestMapping(value = "/get", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LocalEntity> getAllUsersHandler() {
        LocalEntity localEntity = mainDao.getEntity();

        return new ResponseEntity<LocalEntity>(localEntity, HttpStatus.OK);
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<Void> addeUserHandler(@RequestBody LocalEntity user, UriComponentsBuilder ucBuilder) {


        mainDao.addNewMeasure(user);


        HttpHeaders headers = new HttpHeaders();
       // headers.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }




   /*

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
        //return mainDao.addNewMeasure(measure).getTestString();
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }*/
}
