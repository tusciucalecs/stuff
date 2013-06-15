package javabbob.de;

import java.util.Random;

import javabbob.JNIfgeneric;

/**
 * It illustrates the conventional DE algorithm which uses DE/rand/1/bin
 * strategy, CR = 0.9 and F = 1.
 */
public class DE {

    public static void run(JNIfgeneric fgeneric, int dim, double maxfunevals,
            Random random) {

        int np = 8 * dim;
        double CR = 0.5;
        double F = 1;
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

        // double progress = 1e5;
        for (double iter = 0; iter < maxfunevals; iter += np) {
            double[][] nextPopulation = new double[np][dim];
            double[] nextPopulationFitness = new double[np];
            for (int i = 0; i < np; i++) {
                double[] targetVector = population[i];
                double[] trialVector = new double[dim];

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
                    if (random.nextDouble() < CR) {
                        trialVector[j] = xr11[j] + F * (xr12[j] - xr13[j]);
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
                } else {
                    nextPopulation[i] = targetVector;
                    nextPopulationFitness[i] = populationFitness[i];
                }
            }
            population = nextPopulation;
            populationFitness = nextPopulationFitness;
            // if (iter > progress) {
            // System.out.println("----------------");
            // System.out.printf("%.2e ", iter);
            // System.out.println(fgeneric.getBest() + " din "
            // + fgeneric.getFtarget());
            //
            // progress += 1e5;
            // }
        }
    }
}
