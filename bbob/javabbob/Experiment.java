package javabbob;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Calendar;
import java.text.SimpleDateFormat;

/**
 * Wrapper class running an entire BBOB experiment. It illustrates the
 * benchmarking of DDE on the noise-free testbed.
 */
public class Experiment {

    static Map<Combination, Integer> hits = new HashMap<Combination, Integer>();
    static long nrHits = 0;

    public static void DDE(JNIfgeneric fgeneric, int dim, double maxfunevals,
            Random random) {

        int np = 8 * dim;
        double[][] population = new double[np][dim];
        double[] populationFitness = new double[np];

        /* Obtain the target function value, which only use is termination */
        double fTarget = fgeneric.getFtarget();
        double fitness;

        Map<Combination, Integer> combinationsChances = Combination
                .initCombinations();
        int total = combinationsChances.size() * 10;

        /* Generate and evaluate the P0 population */
        for (int i = 0; i < np; i++) {
            for (int j = 0; j < dim; j++) {
                population[i][j] = 10. * random.nextDouble() - 5.;
            }
            fitness = fgeneric.evaluate(population[i]);
            if (fitness < fTarget) {
                return;
            }
            populationFitness[i] = fitness;
        }

        for (double iter = 0; iter < maxfunevals; iter += np) {
            double[][] nextPopulation = new double[np][dim];
            double[] nextPopulationFitness = new double[np];
            for (int i = 0; i < np; i++) {
                double[] targetVector = population[i];
                double[] trialVector = new double[dim];

                Combination combination = getCombination(combinationsChances,
                        random, total);
                nrHits++;

                switch (combination.getGenerationStrategy()) {
                case "rand1":
                    double[] xr11 = population[random.nextInt(np)];
                    double[] xr12 = population[random.nextInt(np)];
                    double[] xr13 = population[random.nextInt(np)];
                    for (int j = 0; j < dim; j++) {
                        if (random.nextDouble() < combination.getCR()) {
                            trialVector[j] = xr11[j] + combination.getF()
                                    * (xr12[j] - xr13[j]);
                        } else {
                            trialVector[j] = targetVector[j];
                        }
                    }
                    break;
                case "rand2":
                    double[] xr21 = population[random.nextInt(np)];
                    double[] xr22 = population[random.nextInt(np)];
                    double[] xr23 = population[random.nextInt(np)];
                    double[] xr24 = population[random.nextInt(np)];
                    double[] xr25 = population[random.nextInt(np)];
                    for (int j = 0; j < dim; j++) {
                        if (random.nextDouble() < combination.getCR()) {
                            trialVector[j] = xr21[j] + combination.getF()
                                    * (xr22[j] - xr23[j]) + combination.getF()
                                    * (xr24[j] - xr25[j]);
                        } else {
                            trialVector[j] = targetVector[j];
                        }
                    }
                    break;
                case "currentToRand1":
                    double[] xr31 = population[random.nextInt(np)];
                    double[] xr32 = population[random.nextInt(np)];
                    double[] xr33 = population[random.nextInt(np)];
                    for (int j = 0; j < dim; j++) {
                        trialVector[j] = targetVector[j] + combination.getF()
                                * (xr31[j] - targetVector[j])
                                + combination.getF() * (xr32[j] - xr33[j]);

                    }
                    break;
                default:
                    break;
                }

                fitness = fgeneric.evaluate(trialVector);

                if (fitness < fTarget) {
                    return;
                }
                /* select */
                if (fitness < populationFitness[i]) {
                    nextPopulation[i] = trialVector;
                    nextPopulationFitness[i] = fitness;
                    combinationsChances.put(combination,
                            combinationsChances.get(combination) + 1);
                    total++;
                } else {
                    nextPopulation[i] = targetVector;
                    nextPopulationFitness[i] = populationFitness[i];
                }
            }
            population = nextPopulation;
            populationFitness = nextPopulationFitness;
        }
    }

    private static Combination getCombination(
            Map<Combination, Integer> combinationsChances, Random random,
            int total) {
        int tempTotal = 0;
        int randVal = random.nextInt(total);
        for (Combination combination : combinationsChances.keySet()) {
            tempTotal += combinationsChances.get(combination);
            if (tempTotal >= randVal) {
                hits.put(
                        combination,
                        hits.get(combination) != null ? hits.get(combination) + 1
                                : 1);
                return combination;
            }
        }
        return null;
    }

    private static void printDistribution(Map<Combination, Integer> map,
            long total) {
        System.out.println("=================");
        for (Combination combination1 : map.keySet()) {
            System.out.println(combination1 + " : " + map.get(combination1)
                    * 100 / total + "%" + map.get(combination1));
        }

        System.out.println();
        for (Combination combination1 : hits.keySet()) {
            System.out.println(combination1 + " : " + hits.get(combination1)
                    * 100 / nrHits + "% " + hits.get(combination1));
        }
    }

    /**
     * Main method for running the whole BBOB experiment. Executing this method
     * runs the experiment. The first command-line input argument is
     * interpreted: if given, it denotes the data directory to write in (in
     * which case it overrides the one assigned in the preamble of the method).
     */
    public static void main(String[] args) {

        /* run variables for the function/dimension/instances loops */
        final int dim[] = { 30, 2, 3, 5, 10, 20, 40 };
        final int instances[] = { 1, 2, 3, 4, 5, 31, 32, 33, 34, 35, 36, 37,
                38, 39, 40 };
        /* Function indices are from 1 to 24 (noiseless) */
        // final int functions[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
        // 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };
        final int functions[] = { 1, 2, 3, 4, 5, 15, 16, 17, 18, 19 };
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

        /* now the main loop */
        for (idx_dim = 0; idx_dim < 1/* dim.length */; idx_dim++) {
            for (idx_fun = 0; idx_fun < functions.length; idx_fun++) {
                for (idx_instances = 0; idx_instances < instances.length; idx_instances++) {

                    /* Initialize the objective function in fgeneric. */
                    fgeneric.initBBOB(functions[idx_fun],
                            instances[idx_instances], dim[idx_dim], outputPath,
                            params);
                    /* Call to the optimizer with fgeneric as input */
                    maxfunevals = 1e8 * dim[idx_dim];
                    independent_restarts = -1;
                    t0 = System.currentTimeMillis();
                    while (fgeneric.getEvaluations() < maxfunevals) {
                        independent_restarts++;
                        DDE(fgeneric, dim[idx_dim], 5e6 * dim[idx_dim], rand);
                        if (fgeneric.getBest() < fgeneric.getFtarget())
                            break;
                        System.out.println("Restart");
                    }
                    System.out.printf(
                            "f%d in %d-D, instance %d: FEs=%.2e, rr=%d",
                            functions[idx_fun], dim[idx_dim],
                            instances[idx_instances],
                            fgeneric.getEvaluations(), independent_restarts);
                    System.out
                            .printf(" elapsed time [m]: %.4f\n",
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