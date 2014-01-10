/*
 * Copyright (c) 2004-2009, Jean-Marc François. All Rights Reserved.
 * Licensed under the New BSD license.  See the LICENSE file.
 */
package be.ac.ulg.montefiore.run.jahmm;

import be.ac.ulg.montefiore.run.distributions.MultiGaussianDistribution;
import java.text.NumberFormat;
import static java.text.NumberFormat.getInstance;
import java.util.Arrays;
import static java.util.Arrays.asList;
import static java.util.Arrays.fill;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * This class represents a multivariate gaussian distribution function.
 */
public class OpdfMultiGaussian
        implements Opdf<ObservationVector> {

    private MultiGaussianDistribution distribution;

    /**
     * Builds a new gaussian probability distribution with zero mean and
     * identity covariance matrix.
     *
     * @param dimension The dimension of the vectors.
     */
    public OpdfMultiGaussian(int dimension) {
        distribution = new MultiGaussianDistribution(dimension);
    }

    /**
     * Builds a new gaussian probability distribution with a given mean and
     * covariance matrix.
     *
     * @param mean The distribution's mean.
     * @param covariance The distribution's covariance matrix.
     */
    public OpdfMultiGaussian(double[] mean, double[][] covariance) {
        if (covariance.length == 0 || mean.length != covariance.length
                || covariance.length != covariance[0].length) {
            throw new IllegalArgumentException();
        }

        distribution = new MultiGaussianDistribution(mean, covariance);
    }

    /**
     * Returns (a copy of) this distribution's mean vector.
     *
     * @return The mean vector.
     */
    public double[] mean() {
        return distribution.mean();
    }

    /**
     * Returns (a copy of) this distribution's covariance matrix.
     *
     * @return The covariance matrix.
     */
    public double[][] covariance() {
        return distribution.covariance();
    }

    /**
     * Returns the dimension of the vectors handled by this distribution.
     *
     * @return The dimension of the vectors handled by this distribution.
     */
    public int dimension() {
        return distribution.dimension();
    }

    @Override
    public double probability(ObservationVector o) {
        if (o.dimension() != distribution.dimension()) {
            throw new IllegalArgumentException("Vector has a wrong "
                    + "dimension");
        }

        return distribution.probability(o.value);
    }

    @Override
    public ObservationVector generate() {
        return new ObservationVector(distribution.generate());
    }

    @Override
    public void fit(ObservationVector... oa) {
        fit(asList(oa));
    }

    @Override
    public void fit(Collection<? extends ObservationVector> co) {
        if (co.isEmpty()) {
            throw new IllegalArgumentException("Empty observation set");
        }

        double[] weights = new double[co.size()];
        fill(weights, 1. / co.size());

        fit(co, weights);
    }

    @Override
    public void fit(ObservationVector[] o, double[] weights) {
        fit(asList(o), weights);
    }

    @Override
    public void fit(Collection<? extends ObservationVector> co,
            double[] weights) {
        if (co.isEmpty() || co.size() != weights.length) {
            throw new IllegalArgumentException();
        }

        // Compute mean
        double[] mean = new double[dimension()];
        for (int r = 0; r < dimension(); r++) {
            int i = 0;

            for (ObservationVector o : co) {
                mean[r] += o.value[r] * weights[i++];
            }
        }

        // Compute covariance
        double[][] covariance = new double[dimension()][dimension()];
        int i = 0;
        for (ObservationVector o : co) {
            double[] obs = o.value;
            double[] omm = new double[obs.length];

            for (int j = 0; j < obs.length; j++) {
                omm[j] = obs[j] - mean[j];
            }

            for (int r = 0; r < dimension(); r++) {
                for (int c = 0; c < dimension(); c++) {
                    covariance[r][c] += omm[r] * omm[c] * weights[i];
                }
            }

            i++;
        }

        distribution = new MultiGaussianDistribution(mean, covariance);
    }

    /**
     *
     * @return
     */
    @Override
    public OpdfMultiGaussian clone() {
        try {
            return (OpdfMultiGaussian) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return toString(getInstance());
    }

    @Override
    public String toString(NumberFormat numberFormat) {
        String s = "Multi-variate Gaussian distribution --- Mean: [ ";
        double[] mean = distribution.mean();

        for (int i = 0; i < mean.length; i++) {
            s += numberFormat.format(mean[i]) + " ";
        }

        return s + "]";
    }

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(OpdfMultiGaussian.class.getName());
}
