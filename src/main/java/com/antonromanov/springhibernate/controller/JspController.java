package com.antonromanov.springhibernate.controller;

import com.antonromanov.springhibernate.DAO.PremiumDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



@Controller
public class JspController {

   @Autowired
   private PremiumDAO mainDao;


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(ModelMap map) {

        map.put("msg", mainDao.getEntity().getTestString());
        return "index";
    }

}
