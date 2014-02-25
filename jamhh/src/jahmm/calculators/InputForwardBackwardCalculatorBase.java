package jahmm.calculators;

import jahmm.RegularHmm;
import jahmm.observables.Observation;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import jutlis.tuples.Tuple3;

/**
 * An alpha-beta calculator that calculates 
 * @author kommusoft
 */
public class InputForwardBackwardCalculatorBase extends ForwardBackwardCalculatorRaw<double[][][], double[][][]> implements InputForwardBackwardCalculator {

    private static final InputForwardBackwardCalculatorBase Instance = new InputForwardBackwardCalculatorBase();

    private static final Logger LOG = Logger.getLogger(InputForwardBackwardCalculatorBase.class.getName());

    private InputForwardBackwardCalculatorBase() {
    }

    @Override
    public <O extends Observation> double[][][] computeAlpha(RegularHmm<? super O> hmm, Collection<O> oseq) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <O extends Observation> double[][][] computeBeta(RegularHmm<? super O> hmm, List<O> oseq) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <O extends Observation> Tuple3<double[][][], double[][][], Double> computeAll(RegularHmm<? super O> hmm, List<O> oseq) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <O extends Observation> double computeProbability(RegularHmm<O> hmm, Collection<ComputationType> flags, List<? extends O> oseq) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}