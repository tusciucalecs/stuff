package com.bbob.services;

import java.util.List;

import com.bbob.models.Experiment;

public interface ExperimentService extends RepositoryService<Experiment> {

    void save(Experiment experiment) throws Exception;

    List<Experiment> getAllExperiments() throws Exception;
}
