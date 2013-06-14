package com.bbob.services;

import java.util.List;

import com.bbob.models.Experiment;
import com.bbob.models.Run;

public interface RunService extends RepositoryService<Run> {

    void saveRunsForExperiment(List<Run> runs, Experiment experiment)
            throws Exception;

    List<Run> getAllRunsForExperiment(Experiment experiment) throws Exception;
}
