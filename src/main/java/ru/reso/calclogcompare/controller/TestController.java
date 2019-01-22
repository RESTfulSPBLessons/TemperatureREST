package ru.reso.calclogcompare.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.reso.calclogcompare.Service.PremiumService;
import ru.reso.calclogcompare.model.Premium;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;


/**
 * Основной контроллер, который и выплевывает
 * данные на jsp.
 */
@Controller
//@RequestMapping("/home")
public class TestController {

    /**
     * Сервис, работающий с ДАО.
     */
    @Autowired
    private PremiumService premiumService;

    /**
     * Не действующий в данном релизе метод.
     *
     * @return - табличку через обычное ДАО.
     * @throws Exception - эксепшн.
     */
    @RequestMapping("/ho")
    public ModelAndView handleRequest2() throws Exception {
        List<Premium> listUsers = new ArrayList<>();
        listUsers.add(premiumService.getPremById(Long.valueOf(1)));
        listUsers.add(premiumService.getPremById(Long.valueOf(2)));
        listUsers.add(premiumService.getPremById(Long.valueOf(3)));
        ModelAndView model = new ModelAndView("UserList");
        model.addObject("userList", listUsers);
        return model;
    }


    /**
     * Выплевываем все записи через @Query.
     *
     * @return - табличку через обычное ДАО.
     * @throws Exception - эксепшн.
     */
    @RequestMapping("/ha")
    public ModelAndView handleRequest3() throws Exception {
        List<Premium> listUsers = premiumService.getAllInString();
        ModelAndView model = new ModelAndView("UserList");
        model.addObject("userList", listUsers);
        return model;
    }


    /**
     * Выплевываем конкретную запись
     * по id @Query.
     *
     * @return - табличку через обычное ДАО.
     * @throws Exception - эксепшн.
     */
    @RequestMapping("/hu")
    public ModelAndView handleRequest4() throws Exception {
        List<Premium> listUsers = new ArrayList<>();
        listUsers.add(premiumService.getPremById2(1));

        ModelAndView model = new ModelAndView("UserList");
        model.addObject("userList", listUsers);
        return model;
    }



  /* *//**
 * Просто пустая тестовая страничка.
 *
 * @param map the map
 * @return the string
 *//*
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(final ModelMap map) {
        map.put("msg", "Hello Spring 4 Web MVC!");
        return "index";
    }*/


   /* @GetMapping
    public String home(Model model) throws Exception {
        System.out.println("HOME");
        return "forward:/index.html";
    }*/



}
