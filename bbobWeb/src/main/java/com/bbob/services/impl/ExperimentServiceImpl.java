package com.bbob.services.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.bbob.models.Experiment;
import com.bbob.services.ExperimentService;

@Repository
public final class ExperimentServiceImpl extends
        RepositoryServiceImpl<Experiment> implements ExperimentService {

    private static final Logger LOGGER = Logger
            .getLogger(ExperimentServiceImpl.class);

    @Override
    public void save(Experiment experiment) throws Exception {
        create(experiment);
        LOGGER.debug("Experiment created");
    }

    @Override
    public List<Experiment> getAllExperiments() throws Exception {
        return findAll(Experiment.class);
    }

}
