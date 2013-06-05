package javabbob;

import java.util.Map;
import java.util.Random;
import java.util.Calendar;
import java.text.SimpleDateFormat;

/**
 * Wrapper class running an entire BBOB experiment. It illustrates the
 * benchmarking of DDE on the noise-free testbed.
 */
public class Experiment {

    public static void DDE(JNIfgeneric fgeneric, int dim, double maxfunevals,
            Random rand) {

        int np = 8 * dim;
        double[][] population = new double[np][dim];
        double[] populationFitness = new double[np];
        double best = 1000.;
        Map<Combination, Double> combinationsChances = Combination
                .initCombinations();

        /* Obtain the target function value, which only use is termination */
        double ftarget = fgeneric.getFtarget();
        double fitness;

        /* Generate and evaluate the P0 population */
        for (int i = 0; i < np; i++) {
            for (int j = 0; j < dim; j++) {
                population[i][j] = 10. * rand.nextDouble() - 5.;
            }
            fitness = fgeneric.evaluate(population[i]);
            if (fitness < ftarget) {
                System.out.println("Found from start!");
                return;
            }
            populationFitness[i] = fitness;
        }

        for (int iter = 0; iter < maxfunevals; iter += np) {
            double[][] nextPopulation = new double[np][dim];
            double[] nextPopulationFitness = new double[np];
            for (int i = 0; i < np; i++) {
                double[] targetVector = population[i];
                double[] trialVector = null;

                fitness = fgeneric.evaluate(trialVector);

                if (fitness < best) {
                    best = fitness;
                    if (fitness < ftarget) {
                        System.out.println("Fount from " + iter);
                        break;
                    }
                }
                /* select */
                if (fitness < populationFitness[i]) {
                    nextPopulation[i] = trialVector;
                    nextPopulationFitness[i] = fitness;
                } else {
                    nextPopulation[i] = targetVector;
                    nextPopulationFitness[i] = populationFitness[i];
                }
            }
            population = nextPopulation;
            populationFitness = nextPopulationFitness;
            if (iter % 1e3 == 0) {
                System.out.println("Best = " + best);
            }
        }
    }

    private static Combination getCombination(
            Map<Combination, Double> combinationsChances) {
        
        
        return null;
    }

    /**
     * Main method for running the whole BBOB experiment. Executing this method
     * runs the experiment. The first command-line input argument is
     * interpreted: if given, it denotes the data directory to write in (in
     * which case it overrides the one assigned in the preamble of the method).
     */
    public static void main(String[] args) {

        /* run variables for the function/dimension/instances loops */
        final int dim[] = { 2, 3, 5, 10, 20, 40 };
        final int instances[] = { 1, 2, 3, 4, 5, 31, 32, 33, 34, 35, 36, 37,
                38, 39, 40 };
        int idx_dim, ifun, idx_instances, independent_restarts;
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
        params.comments = "PUT MORE DETAILED INFORMATION, PARAMETER SETTINGS ETC";
        outputPath = "tmp1";

        if (args.length > 0) {
            outputPath = args[0]; // Warning: might override the assignment
                                  // above.
        }

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
        System.out.println("MY_OPTIMIZER seed: " + seed);

        /* record starting time (also useful as random number generation seed) */
        long t0 = System.currentTimeMillis();
        for (ifun = 1; ifun <= 24; ifun++) { // Noiseless testbed
            for (idx_instances = 0; idx_instances < instances.length; idx_instances++) {

                /* Initialize the objective function in fgeneric. */
                fgeneric.initBBOB(ifun, instances[idx_instances],
                        dim[0], outputPath, params);
                System.out.println(fgeneric.getFtarget());
                fgeneric.exitBBOB();
            }
        }
        if (t0 > 1)
            return;

        /* now the main loop */
        for (idx_dim = 0; idx_dim < dim.length; idx_dim++) {
            /*
             * Function indices are from 1 to 24 (noiseless)
             */
            for (ifun = 1; ifun <= 24; ifun++) { // Noiseless testbed
                for (idx_instances = 0; idx_instances < instances.length; idx_instances++) {

                    /* Initialize the objective function in fgeneric. */
                    fgeneric.initBBOB(ifun, instances[idx_instances],
                            dim[idx_dim], outputPath, params);
                    /* Call to the optimizer with fgeneric as input */
                    maxfunevals = 1e3 * dim[idx_dim];
                    independent_restarts = -1;
                    while (fgeneric.getEvaluations() < maxfunevals) {
                        independent_restarts++;
                        DDE(fgeneric, dim[idx_dim],
                                maxfunevals - fgeneric.getEvaluations(), rand);
                        if (fgeneric.getBest() < fgeneric.getFtarget())
                            break;
                    }

                    System.out
                            .printf("  f%d in %d-D, instance %d: FEs=%.0f with %d restarts,",
                                    ifun, dim[idx_dim],
                                    instances[idx_instances],
                                    fgeneric.getEvaluations(),
                                    independent_restarts);
                    System.out
                            .printf(" fbest-ftarget=%.4e, elapsed time [h]: %.2f\n",
                                    fgeneric.getBest() - fgeneric.getFtarget(),
                                    (double) (System.currentTimeMillis() - t0) / 3600000.);

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
