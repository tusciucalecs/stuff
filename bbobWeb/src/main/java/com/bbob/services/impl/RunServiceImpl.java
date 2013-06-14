package com.bbob.services.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.bbob.models.Experiment;
import com.bbob.models.Run;
import com.bbob.services.RunService;

@Repository
public final class RunServiceImpl extends RepositoryServiceImpl<Run> implements
        RunService {

    private static final Logger LOGGER = Logger.getLogger(RunServiceImpl.class);

    @Override
    public void saveRunsForExperiment(List<Run> runs, Experiment experiment)
            throws Exception {
        for (Run run : runs) {
            run.setExperiment(experiment);
            create(run);
        }
        LOGGER.debug("Created runs:" + runs.size());
    }

    @Override
    public List<Run> getAllRunsForExperiment(Experiment experiment)
            throws Exception {
        return findBy(Run.class, "experiment", experiment);
    }

}
