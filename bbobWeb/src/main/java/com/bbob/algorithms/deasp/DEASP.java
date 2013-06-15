package com.bbob.algorithms.deasp;

import java.util.Arrays;
import java.util.Random;

import javabbob.JNIfgeneric;

/**
 * It illustrates the DEASP-Abs algorithm
 */
public class DEASP {

    public static void run(JNIfgeneric fgeneric, int dim, double maxfunevals,
            Random random) {

        int np = 10 * dim;
        double F = 1;
        double[][] population = new double[np][dim];
        // crosover rate
        double[] delta = new double[np];
        // mutation rate
        double[] eta = new double[np];
        // population size parameter
        long[] pi = new long[np];

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
            delta[i] = random.nextDouble();
            eta[i] = random.nextDouble();
            pi[i] = Math.round(np + random.nextDouble());
        }

        // double progress = 1e5;
        double iter = 0;
        while (iter < maxfunevals) {
            double[][] nextPopulation = new double[np][dim];
            double[] nextDelta = new double[np];
            double[] nextEta = new double[np];
            long[] nextPi = new long[np];
            for (int i = 0; i < np; i++) {
                double[] childVector = new double[dim];
                double childDelta, childEta;
                long childPi;

                // choose parents
                int r11 = random.nextInt(np);
                double[] xr11 = population[r11];
                int r12 = random.nextInt(np);
                double[] xr12 = population[r12];
                while (xr12 == xr11) {
                    xr12 = population[random.nextInt(np)];
                }
                int r13 = random.nextInt(np);
                double[] xr13 = population[r13];
                while (xr13 == xr11 || xr13 == xr12) {
                    xr13 = population[random.nextInt(np)];
                }
                int atLeastThis = random.nextInt(dim);

                // crosover
                for (int j = 0; j < dim; j++) {
                    if (random.nextDouble() < delta[r11] || i == atLeastThis) {
                        childVector[j] = xr11[j] + F * (xr12[j] - xr13[j]);
                    } else {
                        childVector[j] = xr11[j];
                    }
                }
                if (random.nextDouble() < delta[r11] || i == atLeastThis) {
                    childDelta = delta[r11] + F * (delta[r12] - delta[r13]);
                    while (childDelta < 0 || childDelta > 1) {
                        if (childDelta < 0) {
                            childDelta += random.nextDouble();
                        } else {
                            childDelta -= random.nextDouble();
                        }
                    }
                    childEta = eta[r11] + F * (eta[r12] - eta[r13]);
                    while (childEta < 0 || childEta > 1) {
                        if (childEta < 0) {
                            childEta += random.nextDouble();
                        } else {
                            childEta -= random.nextDouble();
                        }
                    }
                    childPi = pi[r11] + Math.round(F * (pi[r12] - pi[r13]));
                    if (childPi < dim || childPi > 20 * dim) {
                        childPi = 10 * dim;
                    }
                } else {
                    childDelta = delta[r11];
                    childEta = eta[r11];
                    childPi = pi[r11];
                }
                // mutation
                for (int j = 0; j < dim; j++) {
                    if (random.nextDouble() < eta[r11]) {
                        // mutationValue is N(0, eta)
                        double mutationValue = random.nextDouble();
                        while (mutationValue > eta[r11]) {
                            mutationValue -= eta[r11];
                        }
                        childVector[j] += mutationValue;
                    }
                }
                if (random.nextDouble() < eta[r11]) {
                    childDelta = random.nextDouble();
                    childEta = random.nextDouble();
                    childPi++;
                }
                nextPopulation[i] = childVector;
                nextDelta[i] = childDelta;
                nextEta[i] = childEta;
                nextPi[i] = childPi;
            }
            double bestFitness = Double.MAX_VALUE;
            int indexBest = 0;
            for (int i = 0; i < np; i++) {
                fitness = fgeneric.evaluate(population[i]);
                if (fitness < bestFitness) {
                    bestFitness = fitness;
                    indexBest = i;
                    if (fitness < fTarget) {
                        return;
                    }
                }
            }
            iter += np;
            // calculate new population size
            int newNp = 0;
            for (Long val : nextPi) {
                newNp += val;
            }
            newNp = Math.round(newNp / nextPi.length);
            if (newNp <= np) {
                np = newNp;
                population = new double[np][dim];
                delta = new double[np];
                eta = new double[np];
                pi = new long[np];
                for (int j = 0; j < np; j++) {
                    population[j] = nextPopulation[j];
                    delta[j] = nextDelta[j];
                    eta[j] = nextEta[j];
                    pi[j] = nextPi[j];
                }
            } else {
                population = new double[newNp][dim];
                delta = new double[newNp];
                eta = new double[newNp];
                pi = new long[newNp];
                for (int j = 0; j < np; j++) {
                    population[j] = nextPopulation[j];
                    delta[j] = nextDelta[j];
                    eta[j] = nextEta[j];
                    pi[j] = nextPi[j];
                }
                for (int j = np; j < newNp; j++) {
                    population[j] = Arrays.copyOf(nextPopulation[indexBest],
                            nextPopulation[indexBest].length);
                    delta[j] = nextDelta[indexBest];
                    eta[j] = nextEta[indexBest];
                    pi[j] = nextPi[indexBest];
                }
                np = newNp;
            }

            // if (iter > progress) {
            // System.out.println("----------------");
            // System.out.printf("%.2e ", iter);
            // System.out.println(fgeneric.getBest() + " din "
            // + fgeneric.getFtarget());
            // System.out.println("NP: " + np);
            //
            // progress += 1e5;
            // }
        }
    }
}
