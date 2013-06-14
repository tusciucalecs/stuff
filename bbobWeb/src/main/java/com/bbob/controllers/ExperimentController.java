package com.bbob.controllers;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bbob.algorithms.RunnableExperiment;
import com.bbob.models.Account;
import com.bbob.services.ExperimentService;
import com.bbob.services.RunService;
import com.bbob.services.UserService;

@Controller
public final class ExperimentController {

    private static Logger logger = Logger.getLogger(ExperimentController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ExperimentService experimentService;

    @Autowired
    private RunService runService;

    private static volatile ExecutorService executor = null;
    private static volatile Future<?> experiment = null;

    static {
        executor = Executors.newFixedThreadPool(1);
    }

    @RequestMapping(value = { "/user/experiment" }, method = {
            RequestMethod.GET, RequestMethod.POST })
    public String checkExperiment(Model model) {
        Account account = userService.getAccountIfUserIsAuthenticated();
        model.addAttribute("account", account);

        Boolean running = true;
        if (experiment == null || experiment.isDone()
                || experiment.isCancelled()) {
            running = false;
        }
        model.addAttribute("running", running);
        return "user/experiment";
    }

    @RequestMapping(value = { "/user/startExperiment" }, method = RequestMethod.GET)
    public String startExperiment(Model model) {
        if (experiment == null || experiment.isDone()
                || experiment.isCancelled()) {
            experiment = executor.submit(new RunnableExperiment(
                    experimentService, runService, userService
                            .getAccountIfUserIsAuthenticated().getUser()));
            logger.warn(new Date() + ": An experiment started");
        }
        return "redirect:/user/experiment";
    }
}
