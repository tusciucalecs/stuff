package com.bbob.controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bbob.models.Account;
import com.bbob.models.Role;
import com.bbob.services.UserService;

@Controller
public final class LoginController {

    private static Logger logger = Logger
            .getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = { "/", "/login" }, method = { RequestMethod.GET,
            RequestMethod.POST })
    public String viewLoginPage() {
        Account account = userService.getAccountIfUserIsAuthenticated();
        if (account != null) {
            logger.debug("User is authenticated. Redirect to home page");
            return "redirect:/home";
        }
        return "login";
    }

    @RequestMapping(value = "/home", method = { RequestMethod.GET,
            RequestMethod.POST })
    public String goToHomePage(Model model) {
        Account account = userService.getAccountIfUserIsAuthenticated();
        model.addAttribute("account", account);
        if (Role.ROLE_ADMIN.equals(account.getUser().getRole())) {
            return "redirect:/admin";
        }
        return "user/home";
    }

    @RequestMapping(value = "/admin", method = { RequestMethod.GET,
            RequestMethod.POST })
    public String goToAdminHomePage(Model model) {
        Account account = userService.getAccountIfUserIsAuthenticated();
        model.addAttribute("account", account);
        if (Role.ROLE_ADMIN.equals(account.getUser().getRole())) {
            return "admin/home";
        }
        return "redirect:/home";
    }
    
    @RequestMapping(value = { "/favicon.ico" }, method = { RequestMethod.GET,
            RequestMethod.POST })
    public String getFavicon() {
        return "redirect:/resources/img/favicon.ico";
    }
}
