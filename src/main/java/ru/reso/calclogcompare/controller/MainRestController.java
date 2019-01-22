package ru.reso.calclogcompare.controller;

// This class created by Anton Romanov 03.12.2018 at 11:44
// Git Hub repo - ...
// https://192.*.*.*:33246/RCCT-2.0-SNAPSHOT/rest/getone?id=1
// http://localhost:8083/RCCT-2.0-SNAPSHOT/rest/check?id=1


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ru.reso.calclogcompare.Service.PremiumService;
import ru.reso.calclogcompare.model.Premium;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class MainRestController {

    /**
     * Сервис, работающий с ДАО.
     */
    @Autowired
    private PremiumService premiumService;

    @RequestMapping(value = "check", method = RequestMethod.GET)
    public ResponseEntity<?> CheckSerial(@RequestParam("id") String id) {

   //     Response response = new Response();

        System.out.println("Пришел запрос");
        return ResponseEntity.ok((premiumService.getPremById2(Integer.parseInt(id))));

    }


}
