package javabbob;

import java.util.HashMap;
import java.util.Map;

public class Combination {
    private String generationStrategy;
    private double F;
    private double CR;

    public Combination(String generationStrategy, double f, double cR) {
        super();
        this.generationStrategy = generationStrategy;
        F = f;
        CR = cR;
    }

    public String getGenerationStrategy() {
        return generationStrategy;
    }

    public void setGenerationStrategy(String generationStrategy) {
        this.generationStrategy = generationStrategy;
    }

    public double getF() {
        return F;
    }

    public void setF(double f) {
        F = f;
    }

    public double getCR() {
        return CR;
    }

    public void setCR(double cR) {
        CR = cR;
    }

    public static Map<Combination, Integer> initCombinations() {
        Map<Combination, Integer> combinations = new HashMap<Combination, Integer>();
        Combination combination;
        combination = new Combination("rand1", 1., 0.1);
        combinations.put(combination, 10);
        combination = new Combination("rand1", 1., 0.9);
        combinations.put(combination, 10);
        combination = new Combination("rand1", 0.8, 0.2);
        combinations.put(combination, 10);

        combination = new Combination("rand2", 1., 0.1);
        combinations.put(combination, 10);
        combination = new Combination("rand2", 1., 0.9);
        combinations.put(combination, 10);
        combination = new Combination("rand2", 0.8, 0.2);
        combinations.put(combination, 10);

        combination = new Combination("currentToRand1", 1., 0.1);
        combinations.put(combination, 10);
        combination = new Combination("currentToRand1", 1., 0.9);
        combinations.put(combination, 10);
        combination = new Combination("currentToRand1", 0.8, 0.2);
        combinations.put(combination, 10);
        return combinations;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(CR);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(F);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime
                * result
                + ((generationStrategy == null) ? 0 : generationStrategy
                        .hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Combination other = (Combination) obj;
        if (Double.doubleToLongBits(CR) != Double.doubleToLongBits(other.CR))
            return false;
        if (Double.doubleToLongBits(F) != Double.doubleToLongBits(other.F))
            return false;
        if (generationStrategy == null) {
            if (other.generationStrategy != null)
                return false;
        } else if (!generationStrategy.equals(other.generationStrategy))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return generationStrategy + " " + F + " " + CR;
    }
}
