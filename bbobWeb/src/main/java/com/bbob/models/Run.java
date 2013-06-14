package com.bbob.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "run")
public class Run extends BaseEntity {

    @Column(name = "algorithm", nullable = false)
    @Enumerated(EnumType.STRING)
    private Algorithm algorithm;

    @Column(name = "function_index", nullable = false)
    private Integer functionIndex;

    @Column(name = "function_instance", nullable = false)
    private Integer functionInstance;

    @Column(name = "dimension", nullable = false)
    private Integer dimension;

    @Column(name = "fes", nullable = false)
    private Double fes;

    @Column(name = "fbest_minus_ftarget", nullable = false)
    private Double fBestMinusfTarget;

    @Column(name = "time_minutes", nullable = false)
    private Double timeMinutes;

    @JoinColumn(name = "experiment_id", nullable = false)
    @ManyToOne
    private Experiment experiment;

    public Run() {
        super();
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public Integer getFunctionIndex() {
        return functionIndex;
    }

    public void setFunctionIndex(Integer functionIndex) {
        this.functionIndex = functionIndex;
    }

    public Integer getFunctionInstance() {
        return functionInstance;
    }

    public void setFunctionInstance(Integer functionInstance) {
        this.functionInstance = functionInstance;
    }

    public Integer getDimension() {
        return dimension;
    }

    public void setDimension(Integer dimension) {
        this.dimension = dimension;
    }

    public Double getFes() {
        return fes;
    }

    public void setFes(Double fes) {
        this.fes = fes;
    }

    public Double getfBestMinusfTarget() {
        return fBestMinusfTarget;
    }

    public void setfBestMinusfTarget(Double fBestMinusfTarget) {
        this.fBestMinusfTarget = fBestMinusfTarget;
    }

    public Double getTimeMinutes() {
        return timeMinutes;
    }

    public void setTimeMinutes(Double timeMinutes) {
        this.timeMinutes = timeMinutes;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }
}
