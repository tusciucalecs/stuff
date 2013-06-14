package com.bbob.algorithms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javabbob.JNIfgeneric;

import org.apache.log4j.Logger;

import com.bbob.algorithms.dde.DDE;
import com.bbob.algorithms.de.DE;
import com.bbob.algorithms.deasp.DEASP;
import com.bbob.algorithms.jade.JADE;
import com.bbob.models.Algorithm;
import com.bbob.models.Experiment;
import com.bbob.models.Run;
import com.bbob.models.User;
import com.bbob.services.ExperimentService;
import com.bbob.services.RunService;

/**
 * Wrapper class running an entire BBOB experiment.
 */
public class RunnableExperiment implements Runnable {

    private static Logger logger = Logger.getLogger(RunnableExperiment.class);

    private ExperimentService experimentService;
    private RunService runService;
    private User authorUser;

    public RunnableExperiment(ExperimentService experimentService,
            RunService runService, User authorUser) {
        super();
        this.experimentService = experimentService;
        this.runService = runService;
        this.authorUser = authorUser;
    }

    /**
     * Main method for running the whole BBOB experiment. Executing this method
     * runs the experiment.
     */
    @Override
    public void run() {

        try {
            if (runService == null) {
                logger.error("not autowired");
                return;
            }
            String dataFolder = "C:/Temp/DEDATA/";
            /* run variables for the function/dimension/instances loops */
            // final int dim[] = { 2, 3, 5, 10, 20, 40 };
            final int dim[] = { 30 };
            // final int instances[] = { 1, 2, 3, 4, 5, 31, 32, 33, 34, 35, 36,
            // 37,
            // 38, 39, 40 };
            final int instances[] = { 1 };
            /* Function indices are from 1 to 24 (noiseless) */
            // final int functions[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
            // 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 };
            final int functions[] = { 1, 2, 3, 4, 5, 15, 16, 17, 18, 19 };
            // final int functions[] = { 1 };
            int idx_dim, idx_fun, idx_instances;

            long seed = System.currentTimeMillis();
            /*
             * This lines loads the library cjavabbob at the core of
             * JNIfgeneric. It will throw runtime errors if the library is not
             * found.
             */
            JNIfgeneric fgenericDDE = new JNIfgeneric();
            JNIfgeneric.Params paramsDDE = new JNIfgeneric.Params();
            paramsDDE.algName = "DDE";
            paramsDDE.comments = "DDE Info";
            String outputPathDDE = dataFolder + "dataDDE";
            JNIfgeneric.makeBBOBdirs(outputPathDDE, false);
            Random randDDE = new Random(seed);

            JNIfgeneric fgenericJADE = new JNIfgeneric();
            JNIfgeneric.Params paramsJADE = new JNIfgeneric.Params();
            paramsJADE.algName = "JADE";
            paramsJADE.comments = "JADE Info";
            String outputPathJADE = dataFolder + "dataJADE";
            JNIfgeneric.makeBBOBdirs(outputPathJADE, false);
            Random randJADE = new Random(seed);

            JNIfgeneric fgenericDE = new JNIfgeneric();
            JNIfgeneric.Params paramsDE = new JNIfgeneric.Params();
            paramsDE.algName = "DE";
            paramsDE.comments = "DE Info";
            String outputPathDE = dataFolder + "dataDE";
            JNIfgeneric.makeBBOBdirs(outputPathDE, false);
            Random randDE = new Random(seed);

            JNIfgeneric fgenericDEASP = new JNIfgeneric();
            JNIfgeneric.Params paramsDEASP = new JNIfgeneric.Params();
            paramsDEASP.algName = "DEASP";
            paramsDEASP.comments = "DEASP Info";
            String outputPathDEASP = dataFolder + "dataDEASP";
            JNIfgeneric.makeBBOBdirs(outputPathDEASP, false);
            Random randDEASP = new Random(seed);

            List<Run> runs = new ArrayList<>();
            /* now the main loop */
            for (idx_dim = 0; idx_dim < 1/* dim.length */; idx_dim++) {
                for (idx_fun = 0; idx_fun < functions.length; idx_fun++) {
                    for (idx_instances = 0; idx_instances < instances.length; idx_instances++) {

                        runs.add(runAlgorithm(dim, instances, functions,
                                idx_dim, idx_fun, idx_instances, fgenericDDE,
                                paramsDDE, outputPathDDE, randDDE));
                        runs.add(runAlgorithm(dim, instances, functions,
                                idx_dim, idx_fun, idx_instances, fgenericJADE,
                                paramsJADE, outputPathJADE, randJADE));
                        runs.add(runAlgorithm(dim, instances, functions,
                                idx_dim, idx_fun, idx_instances, fgenericDE,
                                paramsDE, outputPathDE, randDE));
                        runs.add(runAlgorithm(dim, instances, functions,
                                idx_dim, idx_fun, idx_instances, fgenericDEASP,
                                paramsDEASP, outputPathDEASP, randDEASP));
                    }
                }
                logger.info("---- dimension " + dim[idx_dim] + "-D done ----");
            }
            Experiment experiment = new Experiment();
            experiment.setUser(authorUser);
            experiment.setDate(new Date());
            experimentService.save(experiment);
            runService.saveRunsForExperiment(runs, experiment);
            logger.info("Experiment finnished");
        } catch (Exception e) {
            logger.error("error in experiment thread!");
            logger.error(e);
            e.printStackTrace();
        }
    }

    private Run runAlgorithm(final int[] dim, final int[] instances,
            final int[] functions, int idx_dim, int idx_fun, int idx_instances,
            JNIfgeneric fgeneric, JNIfgeneric.Params params, String outputPath,
            Random random) {
        double maxFES = 3e5;
        /* Initialize the objective function in fgeneric. */
        fgeneric.initBBOB(functions[idx_fun], instances[idx_instances],
                dim[idx_dim], outputPath, params);
        long t0 = System.currentTimeMillis();

        /* Call to the optimizer with fgeneric as input */
        Algorithm algorithm = null;
        switch (params.algName) {
        case "DDE":
            algorithm = Algorithm.DDE;
            DDE.run(fgeneric, dim[idx_dim], maxFES, random);
            break;
        case "JADE":
            algorithm = Algorithm.JADE;
            JADE.run(fgeneric, dim[idx_dim], maxFES, random);
            break;
        case "DE":
            algorithm = Algorithm.DE;
            DE.run(fgeneric, dim[idx_dim], maxFES, random);
            break;
        case "DEASP":
            algorithm = Algorithm.DEASP;
            DEASP.run(fgeneric, dim[idx_dim], maxFES, random);
            break;
        default:
            break;
        }
        String result = String.format(params.algName
                + ": f%d in %d-D, instance" + " %d: FEs=%.2e, ",
                functions[idx_fun], dim[idx_dim], instances[idx_instances],
                fgeneric.getEvaluations());
        result += String.format(" fbest-ftarget=%.4e, elapsed time [m]: %.4f",
                fgeneric.getBest() - fgeneric.getFtarget(),
                (double) (System.currentTimeMillis() - t0) / 60000.);
        logger.info(result);
        Run run = new Run();
        run.setAlgorithm(algorithm);
        run.setFunctionIndex(functions[idx_fun]);
        run.setDimension(dim[idx_dim]);
        run.setFunctionInstance(instances[idx_instances]);
        run.setFes(fgeneric.getEvaluations());
        run.setfBestMinusfTarget(fgeneric.getBest() - fgeneric.getFtarget());
        run.setTimeMinutes((double) (System.currentTimeMillis() - t0) / 60000.);

        /* call the BBOB closing function to wrap things up neatly */
        fgeneric.exitBBOB();

        return run;
    }
}
