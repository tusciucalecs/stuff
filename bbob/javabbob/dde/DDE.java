package javabbob.dde;

//import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javabbob.JNIfgeneric;

/**
 * It illustrates the DDE algorithm.
 */
public class DDE {

    // static Map<Combination, Integer> hits = new HashMap<Combination,
    // Integer>();
    // static long nrHits = 0;

    public static void run(JNIfgeneric fgeneric, int dim, double maxfunevals,
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

        double progress = 1e5;
        for (double iter = 0; iter < maxfunevals; iter += np) {
            double[][] nextPopulation = new double[np][dim];
            double[] nextPopulationFitness = new double[np];
            for (int i = 0; i < np; i++) {
                double[] targetVector = population[i];
                double[] trialVector = new double[dim];

                Combination combination = getCombination(combinationsChances,
                        random, total);
                // nrHits++;

                switch (combination.getGenerationStrategy()) {
                case "rand1":
                    double[] xr11 = population[random.nextInt(np)];
                    while (xr11 == targetVector) {
                        xr11 = population[random.nextInt(np)];
                    }
                    double[] xr12 = population[random.nextInt(np)];
                    while (xr12 == targetVector || xr12 == xr11) {
                        xr12 = population[random.nextInt(np)];
                    }
                    double[] xr13 = population[random.nextInt(np)];
                    while (xr13 == targetVector || xr13 == xr11 || xr13 == xr12) {
                        xr13 = population[random.nextInt(np)];
                    }
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
                    while (xr21 == targetVector) {
                        xr21 = population[random.nextInt(np)];
                    }
                    double[] xr22 = population[random.nextInt(np)];
                    while (xr22 == targetVector || xr22 == xr21) {
                        xr22 = population[random.nextInt(np)];
                    }
                    double[] xr23 = population[random.nextInt(np)];
                    while (xr23 == targetVector || xr23 == xr21 || xr23 == xr22) {
                        xr23 = population[random.nextInt(np)];
                    }
                    double[] xr24 = population[random.nextInt(np)];
                    while (xr24 == targetVector || xr24 == xr21 || xr24 == xr22
                            || xr24 == xr23) {
                        xr24 = population[random.nextInt(np)];
                    }
                    double[] xr25 = population[random.nextInt(np)];
                    while (xr25 == targetVector || xr25 == xr21 || xr25 == xr22
                            || xr25 == xr23 || xr25 == xr24) {
                        xr25 = population[random.nextInt(np)];
                    }
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
                    while (xr31 == targetVector) {
                        xr31 = population[random.nextInt(np)];
                    }
                    double[] xr32 = population[random.nextInt(np)];
                    while (xr32 == targetVector || xr32 == xr31) {
                        xr32 = population[random.nextInt(np)];
                    }
                    double[] xr33 = population[random.nextInt(np)];
                    while (xr33 == targetVector || xr33 == xr31 || xr33 == xr32) {
                        xr33 = population[random.nextInt(np)];
                    }
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
            if (iter > progress) {
                // System.out.println("----------------");
                // System.out.printf("%.2e ", iter);
                // System.out.println(fgeneric.getBest() + " din "
                // + fgeneric.getFtarget());
                // printDistribution(combinationsChances, total);

                combinationsChances = Combination.initCombinations();
                total = combinationsChances.size() * 10;
                // hits = new HashMap<Combination, Integer>();
                // nrHits = 0;

                progress += 1e5;
            }
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
                // hits.put(
                // combination,
                // hits.get(combination) != null ? hits.get(combination) + 1
                // : 1);
                return combination;
            }
        }
        return null;
    }

    // private static void printDistribution(Map<Combination, Integer> map,
    // long total) {
    // System.out.println("=================");
    // System.out.println(total);
    // for (Combination combination1 : map.keySet()) {
    // System.out.println(combination1 + " : " + map.get(combination1)
    // * 100 / total + "% as " + map.get(combination1));
    // }
    //
    // System.out.println();
    // System.out.println(nrHits);
    // for (Combination combination1 : hits.keySet()) {
    // System.out.println(combination1 + " : " + hits.get(combination1)
    // * 100 / nrHits + "% as " + hits.get(combination1));
    // }
    // }
}
