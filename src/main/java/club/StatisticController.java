package club;

import club.model.AccountRefillLog;
import club.model.RifLog;
import club.repository.AccountRefillRepository;
import club.repository.RifLogsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;;

@Controller
public class StatisticController {

    @Autowired
    private RifLogsRepository rifRepository;

    @Autowired
    private AccountRefillRepository accountRefillRepository;

    private static Logger consoleLogger = LoggerFactory.getLogger("console_logger");

    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/statistic", method = {RequestMethod.GET, RequestMethod.POST})
    public String statistic(Model model) {

        ArrayList<RifLog> clientsCount = (ArrayList<RifLog>) rifRepository.findAll();
        ArrayList<AccountRefillLog> moneyTransfersCount = (ArrayList<AccountRefillLog>) accountRefillRepository.findAll();
        Long sum = accountRefillRepository.getSumOfAllTransactions();
        sum = sum == null ? 0 : sum;

        consoleLogger.info("Счетов всего: " + String.valueOf(clientsCount.size()));
        consoleLogger.info("Транзакций всего: " + String.valueOf(moneyTransfersCount.size()));
        consoleLogger.info("Сумма всех переводов: " + sum/100);

        model.addAttribute("allclients", clientsCount.size());
        model.addAttribute("alltransactions", moneyTransfersCount.size());
        model.addAttribute("sum", sum/100);


        return "statistic";
    }
}
