package com.bank.ePon.controller;

import com.bank.ePon.model.Transaction;
import com.bank.ePon.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.bank.ePon.service.AccountService;
import com.bank.ePon.service.TransactionService;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

@GetMapping("/dashboard")
public String showDashboard(HttpSession session, Model model) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) {
        return "redirect:/login";
    }

    BigDecimal currentBalance = accountService.getCurrentBalance(userId);

    List<Transaction> transactions = transactionService.getUserTransactions(userId);

    model.addAttribute("userName", session.getAttribute("userName"));
    model.addAttribute("balance", currentBalance);
    model.addAttribute("transactions", transactions);

    return "dashboard";
}
@GetMapping("/cash-in")
    public String showCashInPage(HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        return "cash-in";
    }

    @PostMapping("/cash-in")
    public String processCashIn(@RequestParam("amount") BigDecimal amount,
                                HttpSession session,
                                Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        try {
            transactionService.cashIn(userId, amount);
            model.addAttribute("success", "Cash-in successful!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/transfer")
    public String showTransferPage(HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        return "transfer"; // maps to transfer.html
    }

    @PostMapping("/transfer")
    public String processTransfer(@RequestParam("receiverMobile") String receiverMobile,
                                  @RequestParam("amount") BigDecimal amount,
                                  HttpSession session,
                                  Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        try {
            transactionService.transferMoney(userId, receiverMobile, amount);
            model.addAttribute("success", "Transfer successful!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }
}
