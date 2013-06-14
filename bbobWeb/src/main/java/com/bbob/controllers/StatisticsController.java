package com.bbob.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bbob.models.Account;
import com.bbob.models.Algorithm;
import com.bbob.models.Experiment;
import com.bbob.models.Run;
import com.bbob.services.ExperimentService;
import com.bbob.services.RunService;
import com.bbob.services.UserService;

@Controller
public final class StatisticsController {

    private static Logger logger = Logger.getLogger(StatisticsController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ExperimentService experimentService;

    @Autowired
    private RunService runService;

    @RequestMapping(value = "/user/statistics", method = RequestMethod.GET)
    public String viewStatisticsPage(
            Model model,
            @RequestParam(value = "experiment", required = false) Long experimentId) {
        try {
            Account account = userService.getAccountIfUserIsAuthenticated();
            model.addAttribute("account", account);

            List<Experiment> experiments = experimentService
                    .getAllExperiments();
            model.addAttribute("experiments", experiments);
            if (experimentId != null) {
                List<Experiment> one = new ArrayList<Experiment>();
                one.add(experimentService.get(Experiment.class, experimentId));
                model.addAttribute("statistics", computeStatistic(one));
            } else {
                model.addAttribute("statistics", computeStatistic(experiments));
            }
        } catch (Exception e) {
            logger.error("viewStatisticsPage: " + e);
            e.printStackTrace();
        }
        return "user/statistics";
    }

    private List<Statistic> computeStatistic(List<Experiment> experiments)
            throws Exception {
        Map<String, Map<Algorithm, List<Run>>> functions = new TreeMap<String, Map<Algorithm, List<Run>>>();
        for (Experiment experiment : experiments) {
            List<Run> runs = runService.getAllRunsForExperiment(experiment);
            for (Run run : runs) {
                String key = "F"
                        + String.format("%02d", run.getFunctionIndex()) + "-"
                        + String.format("%02d", run.getFunctionInstance());
                Map<Algorithm, List<Run>> function = functions.get(key);
                if (function == null) {
                    function = new TreeMap<Algorithm, List<Run>>();
                    functions.put(key, function);
                }
                List<Run> runsForFunction = function.get(run.getAlgorithm());
                if (runsForFunction == null) {
                    runsForFunction = new ArrayList<>();
                    function.put(run.getAlgorithm(), runsForFunction);
                }
                runsForFunction.add(run);
            }
        }
        List<Statistic> statistics = new ArrayList<>();
        for (Map.Entry<String, Map<Algorithm, List<Run>>> function : functions
                .entrySet()) {
            Statistic statistic = new Statistic();
            statistic.setName(function.getKey());
            Integer index = 0;
            for (Algorithm algorithm : Algorithm.values()) {
                List<Run> runs = function.getValue().get(algorithm);
                Double max = Double.MIN_VALUE;
                Double min = Double.MAX_VALUE;
                Double sum = 0.;
                for (Run run : runs) {
                    Double performance = run.getfBestMinusfTarget();
                    sum += performance;
                    if (max < performance) {
                        max = performance;
                    }
                    if (min > performance) {
                        min = performance;
                    }
                }
                Double mean = sum / runs.size();
                Object[] array = statistic.getMatrix()[index];
                Double standardDeviation = max - mean;
                if (mean - min > standardDeviation) {
                    standardDeviation = mean - min;
                }
                array[0] = algorithm.toString() + " "
                        + String.format("%.1e", mean) + "+-"
                        + String.format("%.1e", standardDeviation);
                array[1] = min;
                array[2] = (min + mean) / 2;
                array[3] = (max + mean) / 2;
                array[4] = max;
                index++;
            }
            statistics.add(statistic);
        }
        return statistics;
    }
}
