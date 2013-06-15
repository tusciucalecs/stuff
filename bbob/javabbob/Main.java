package javabbob;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import javabbob.dde.DDE;
import javabbob.de.DE;
import javabbob.deasp.DEASP;
import javabbob.jade.JADE;

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
        // final int instances[] = { 1, 2, 3, 4, 5, 31, 32, 33, 34, 35, 36, 37,
        // 38, 39, 40 };
        final int instances[] = { 1 };
        /* Function indices are from 1 to 24 (noiseless) */
        // final int functions[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
        // 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };
        final int functions[] = { 1, 2, 3, 4, 5, 15, 16, 17, 18, 19 };
        // final int functions[] = { 16 };
        int idx_dim, idx_fun, idx_instances;

        long seed = System.currentTimeMillis();
        System.out.println("seed: " + seed);
        /*
         * This lines loads the library cjavabbob at the core of JNIfgeneric. It
         * will throw runtime errors if the library is not found.
         */
        JNIfgeneric fgenericDDE = new JNIfgeneric();
        JNIfgeneric.Params paramsDDE = new JNIfgeneric.Params();
        paramsDDE.algName = "DDE";
        paramsDDE.comments = "DDE Info";
        String outputPathDDE = "dataDDE";
        JNIfgeneric.makeBBOBdirs(outputPathDDE, false);
        Random randDDE = new Random(seed);

        JNIfgeneric fgenericJADE = new JNIfgeneric();
        JNIfgeneric.Params paramsJADE = new JNIfgeneric.Params();
        paramsJADE.algName = "JADE";
        paramsJADE.comments = "JADE Info";
        String outputPathJADE = "dataJADE";
        JNIfgeneric.makeBBOBdirs(outputPathJADE, false);
        Random randJADE = new Random(seed);

        JNIfgeneric fgenericDE = new JNIfgeneric();
        JNIfgeneric.Params paramsDE = new JNIfgeneric.Params();
        paramsDE.algName = "DE";
        paramsDE.comments = "DE Info";
        String outputPathDE = "dataDE";
        JNIfgeneric.makeBBOBdirs(outputPathDE, false);
        Random randDE = new Random(seed);

        JNIfgeneric fgenericDEASP = new JNIfgeneric();
        JNIfgeneric.Params paramsDEASP = new JNIfgeneric.Params();
        paramsDEASP.algName = "DEASP";
        paramsDEASP.comments = "DEASP Info";
        String outputPathDEASP = "dataDEASP";
        JNIfgeneric.makeBBOBdirs(outputPathDEASP, false);
        Random randDEASP = new Random(seed);

        /* now the main loop */
        for (idx_dim = 0; idx_dim < 1/* dim.length */; idx_dim++) {
            for (idx_fun = 0; idx_fun < functions.length; idx_fun++) {
                for (idx_instances = 0; idx_instances < instances.length; idx_instances++) {

                    runAlgorithm(dim, instances, functions, idx_dim, idx_fun,
                            idx_instances, fgenericDDE, paramsDDE,
                            outputPathDDE, randDDE);
                    runAlgorithm(dim, instances, functions, idx_dim, idx_fun,
                            idx_instances, fgenericJADE, paramsJADE,
                            outputPathJADE, randJADE);
                    runAlgorithm(dim, instances, functions, idx_dim, idx_fun,
                            idx_instances, fgenericDE, paramsDE, outputPathDE,
                            randDE);
                    runAlgorithm(dim, instances, functions, idx_dim, idx_fun,
                            idx_instances, fgenericDEASP, paramsDEASP,
                            outputPathDEASP, randDEASP);
                    System.out.println();
                }

                System.out.println("\ndate and time: "
                        + (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"))
                                .format((Calendar.getInstance()).getTime()));

            }
            System.out.println("---- dimension " + dim[idx_dim]
                    + "-D done ----");
        }
    }

    private static void runAlgorithm(final int[] dim, final int[] instances,
            final int[] functions, int idx_dim, int idx_fun, int idx_instances,
            JNIfgeneric fgeneric, JNIfgeneric.Params params, String outputPath,
            Random random) {
        double maxFES = 3e5;
        /* Initialize the objective function in fgeneric. */
        fgeneric.initBBOB(functions[idx_fun], instances[idx_instances],
                dim[idx_dim], outputPath, params);
        long t0 = System.currentTimeMillis();

        /* Call to the optimizer with fgeneric as input */
        switch (params.algName) {
        case "DDE":
            DDE.run(fgeneric, dim[idx_dim], maxFES, random);
            break;
        case "JADE":
            JADE.run(fgeneric, dim[idx_dim], maxFES, random);
            break;
        case "DE":
            DE.run(fgeneric, dim[idx_dim], maxFES, random);
            break;
        case "DEASP":
            DEASP.run(fgeneric, dim[idx_dim], maxFES, random);
            break;
        default:
            break;
        }
        System.out.printf(params.algName + ": f%d in %d-D, instance"
                + " %d: FEs=%.2e, ", functions[idx_fun], dim[idx_dim],
                instances[idx_instances], fgeneric.getEvaluations());
        System.out.printf(" fbest-ftarget=%.4e, elapsed time [m]: %.4f\n",
                fgeneric.getBest() - fgeneric.getFtarget(),
                (double) (System.currentTimeMillis() - t0) / 60000.);

        /* call the BBOB closing function to wrap things up neatly */
        fgeneric.exitBBOB();
    }
}
