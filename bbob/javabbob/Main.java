package javabbob;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import javabbob.dde.DDE;

/**
 * Wrapper class running an entire BBOB experiment.
 */
public class Main {

    /**
     * Main method for running the whole BBOB experiment. Executing this method
     * runs the experiment. The first command-line input argument is
     * interpreted: if given, it denotes the data directory to write in (in
     * which case it overrides the one assigned in the preamble of the method).
     */
    public static void main(String[] args) {

        /* run variables for the function/dimension/instances loops */
        // final int dim[] = { 2, 3, 5, 10, 20, 40 };
        final int dim[] = { 30 };
        final int instances[] = { 1, 2, 3, 4, 5, 31, 32, 33, 34, 35, 36, 37,
                38, 39, 40 };
        /* Function indices are from 1 to 24 (noiseless) */
        // final int functions[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
        // 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };
        final int functions[] = { 1, 2, 3, 4, 5, 15, 16, 17, 18, 19 };
        // final int functions[] = { 16 };
        int idx_dim, idx_fun, idx_instances, independent_restarts;
        double maxfunevals;
        String outputPath;

        JNIfgeneric fgeneric = new JNIfgeneric();
        /*
         * The line above loads the library cjavabbob at the core of
         * JNIfgeneric. It will throw runtime errors if the library is not
         * found.
         */

        /**************************************************
         * BBOB Mandatory initialization *
         *************************************************/
        JNIfgeneric.Params params = new JNIfgeneric.Params();
        /*
         * Modify the following parameters, choosing a different setting for
         * each new experiment
         */
        params.algName = "DDE";
        params.comments = "DDE Info";
        outputPath = "data";

        /* Creates the folders for storing the experimental data. */
        if (JNIfgeneric.makeBBOBdirs(outputPath, false)) {
            System.out.println("BBOB data directories at " + outputPath
                    + " created.");
        } else {
            System.out.println("Error! BBOB data directories at " + outputPath
                    + " was NOT created, stopping.");
            return;
        }
        ;

        /* External initialization of MY_OPTIMIZER */
        long seed = System.currentTimeMillis();
        Random rand = new Random(seed);
        System.out.println("seed: " + seed);

        /* record starting time (also useful as random number generation seed) */
        long t0 = System.currentTimeMillis();

        /* now the main loop */
        for (idx_dim = 0; idx_dim < 1/* dim.length */; idx_dim++) {
            for (idx_fun = 0; idx_fun < functions.length; idx_fun++) {
                for (idx_instances = 0; idx_instances < instances.length; idx_instances++) {

                    /* Initialize the objective function in fgeneric. */
                    fgeneric.initBBOB(functions[idx_fun],
                            instances[idx_instances], dim[idx_dim], outputPath,
                            params);
                    /* Call to the optimizer with fgeneric as input */
                    maxfunevals = 1e5 * dim[idx_dim];
                    independent_restarts = -1;
                    t0 = System.currentTimeMillis();
                    while (fgeneric.getEvaluations() < maxfunevals) {
                        independent_restarts++;
                        DDE.run(fgeneric, dim[idx_dim], 1e5 * dim[idx_dim],
                                rand);
                        if (fgeneric.getBest() < fgeneric.getFtarget())
                            break;
                        // System.err.println("Restart " + independent_restarts
                        // + ": " + fgeneric.getBest() + " from "
                        // + fgeneric.getFtarget());
                    }
                    System.out.printf(
                            "f%d in %d-D, instance %d: FEs=%.2e, rr=%d",
                            functions[idx_fun], dim[idx_dim],
                            instances[idx_instances],
                            fgeneric.getEvaluations(), independent_restarts);
                    System.out
                            .printf(" fbest-ftarget=%.4e, elapsed time [m]: %.4f\n",
                                    fgeneric.getBest() - fgeneric.getFtarget(),
                                    (double) (System.currentTimeMillis() - t0) / 60000.);

                    /* call the BBOB closing function to wrap things up neatly */
                    fgeneric.exitBBOB();
                }

                System.out.println("\ndate and time: "
                        + (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"))
                                .format((Calendar.getInstance()).getTime()));

            }
            System.out.println("---- dimension " + dim[idx_dim]
                    + "-D done ----");
        }
    }
}
