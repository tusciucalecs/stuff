package javabbob.jade;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javabbob.JNIfgeneric;

/**
 * It illustrates the JADE algorithm.
 */
public class JADE {

    public static void run(JNIfgeneric fgeneric, int dim, double maxfunevals,
            Random random) {

        int np = 8 * dim;
        double[][] population = new double[np][dim];
        double[] populationFitness = new double[np];

        /* Obtain the target function value, which only use is termination */
        double fTarget = fgeneric.getFtarget();
        double fitness;

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
        double cr, f;
        double uCr = 0.5, uF = 0.5;
        final double c = 0.5;
        Set<Double> sCr, sF;
        for (double iter = 0; iter < maxfunevals; iter += np) {
            double[][] nextPopulation = new double[np][dim];
            double[] nextPopulationFitness = new double[np];
            sCr = new HashSet<>();
            sF = new HashSet<>();
            for (int i = 0; i < np; i++) {
                cr = random.nextGaussian() * 0.1 + uCr;
                f = random.nextGaussian() * 0.1 + uF;
                double[] targetVector = population[i];
                double[] trialVector = new double[dim];

                double[] xBestP = getXBestP(random, population,
                        populationFitness);
                double[] xr1 = population[random.nextInt(np)];
                while (xr1 == targetVector) {
                    xr1 = population[random.nextInt(np)];
                }
                double[] xr2 = population[random.nextInt(np)];
                while (xr2 == targetVector) {
                    xr2 = population[random.nextInt(np)];
                }
                for (int j = 0; j < dim; j++) {
                    if (random.nextDouble() < cr) {
                        trialVector[j] = targetVector[j] + f
                                * (xBestP[j] - targetVector[j]) + f
                                * (xr1[j] - xr2[j]);
                    } else {
                        trialVector[j] = targetVector[j];
                    }
                }

                fitness = fgeneric.evaluate(trialVector);

                if (fitness < fTarget) {
                    return;
                }
                /* select */
                if (fitness < populationFitness[i]) {
                    nextPopulation[i] = trialVector;
                    nextPopulationFitness[i] = fitness;
                    sCr.add(cr);
                    sF.add(f);
                } else {
                    nextPopulation[i] = targetVector;
                    nextPopulationFitness[i] = populationFitness[i];
                }
            }
            population = nextPopulation;
            populationFitness = nextPopulationFitness;
            uCr = (1 - c) * uCr + c * meanSetDouble(sCr);
            uF = (1 - c) * uF + c * meanSetDouble(sF);
            if (iter > progress) {
                // System.out.printf("%.2e ", iter);
                // System.out.println(fgeneric.getBest() + " din "
                // + fgeneric.getFtarget());

//                progress += 1e5;
            }
        }
    }

    private static double[] getXBestP(Random random, double[][] population,
            double[] populationFitness) {
        double p = random.nextDouble() * population.length;
        int index = random.nextInt(population.length);
        double bestFitness = Double.MAX_VALUE;
        double[] best = null;
        for (int i = 0; i < p; i++) {
            if (populationFitness[index] < bestFitness) {
                best = population[index];
                bestFitness = populationFitness[index];
            }
            index++;
            if (index == population.length) {
                index = 0;
            }
        }
        return best;
    }

    private static Double meanSetDouble(Set<Double> set) {
        Double sum = 0.;
        for (Double i : set)
            sum = sum + i;
        return sum / set.size();
    }
}
