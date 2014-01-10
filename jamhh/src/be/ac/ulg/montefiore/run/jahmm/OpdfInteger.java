/*
 * Copyright (c) 2004-2009, Jean-Marc François. All Rights Reserved.
 * Licensed under the New BSD license.  See the LICENSE file.
 */
package be.ac.ulg.montefiore.run.jahmm;

import static java.lang.Math.random;
import java.text.NumberFormat;
import static java.text.NumberFormat.getInstance;
import java.util.Arrays;
import static java.util.Arrays.asList;
import static java.util.Arrays.fill;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * This class represents a distribution of a finite number of positive integer
 * observations.
 */
public class OpdfInteger
        implements Opdf<ObservationInteger> {

    private double[] probabilities;

    /**
     * Builds a new probability distribution which operates on integer values.
     * The probabilities are initialized so that the distribution is uniformaly
     * distributed.
     *
     * @param nbEntries The number of values to which to associate
     * probabilities. Observations handled by this distribution have to be
     * higher or equal than 0 and strictly smaller than <code>nbEntries</code>.
     */
    public OpdfInteger(int nbEntries) {
        if (nbEntries <= 0) {
            throw new IllegalArgumentException("Argument must be strictly "
                    + "positive");
        }

        probabilities = new double[nbEntries];

        for (int i = 0; i < nbEntries; i++) {
            probabilities[i] = 1. / nbEntries;
        }
    }

    /**
     * Builds a new probability distribution which operates on integer values.
     *
     * @param probabilities Array holding one probability for each possible
     * argument value (<i>i.e.</i> such that <code>probabilities[i]</code> is
     * the probability of the observation <code>i</code>.
     */
    public OpdfInteger(double[] probabilities) {
        if (probabilities.length == 0) {
            throw new IllegalArgumentException("Invalid empty array");
        }

        this.probabilities = new double[probabilities.length];

        for (int i = 0; i < probabilities.length; i++) {
            if ((this.probabilities[i] = probabilities[i]) < 0.) {
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * Returns how many integers are associated to probabilities by this
     * distribution.
     *
     * @return The number of integers are associated to probabilities.
     */
    public int nbEntries() {
        return probabilities.length;
    }

    @Override
    public double probability(ObservationInteger o) {
        if (o.value > probabilities.length - 1) {
            throw new IllegalArgumentException("Wrong observation value");
        }

        return probabilities[o.value];
    }

    @Override
    public ObservationInteger generate() {
        double rand = random();

        for (int i = 0; i < probabilities.length - 1; i++) {
            if ((rand -= probabilities[i]) < 0.) {
                return new ObservationInteger(i);
            }
        }

        return new ObservationInteger(probabilities.length - 1);
    }

    @Override
    public void fit(ObservationInteger... oa) {
        fit(asList(oa));
    }

    @Override
    public void fit(Collection<? extends ObservationInteger> co) {
        if (co.isEmpty()) {
            throw new IllegalArgumentException("Empty observation set");
        }

        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] = 0.;
        }

        for (ObservationInteger o : co) {
            probabilities[o.value]++;
        }

        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] /= co.size();
        }
    }

    @Override
    public void fit(ObservationInteger[] o, double[] weights) {
        fit(asList(o), weights);
    }

    @Override
    public void fit(Collection<? extends ObservationInteger> co,
            double[] weights) {
        if (co.isEmpty() || co.size() != weights.length) {
            throw new IllegalArgumentException();
        }

        fill(probabilities, 0.);

        int i = 0;
        for (ObservationInteger o : co) {
            probabilities[o.value] += weights[i++];
        }
    }

    /**
     *
     * @return
     */
    @Override
    public OpdfInteger clone() {
        try {
            OpdfInteger opdf = (OpdfInteger) super.clone();
            opdf.probabilities = probabilities.clone();
            return opdf;
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
        String s = "Integer distribution --- ";

        for (int i = 0; i < nbEntries();) {
            ObservationInteger oi = new ObservationInteger(i);

            s += numberFormat.format(probability(oi))
                    + ((++i < nbEntries()) ? " " : "");
        }

        return s;
    }

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(OpdfInteger.class.getName());
}
