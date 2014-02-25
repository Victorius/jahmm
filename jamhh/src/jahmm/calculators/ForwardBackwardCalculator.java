package jahmm.calculators;

import jahmm.Hmm;
import jahmm.RegularHmm;
import jahmm.observables.Observation;
import java.util.Collection;
import java.util.List;
import jutlis.tuples.Tuple3;

/**
 *
 * @author kommusoft
 * @param <TAlpha> The type of the alpha values.
 * @param <TBeta> The type of the beta values.
 */
public interface ForwardBackwardCalculator<TAlpha, TBeta, TObs extends Observation, TInt extends Observation, THmm extends Hmm<? super TObs, TInt>> {

    public abstract TAlpha computeAlpha(THmm hmm, Collection<TObs> oseq);

    public abstract TAlpha computeAlpha(THmm hmm, TObs... oseq);

    public abstract TBeta computeBeta(THmm hmm, List<TObs> oseq);

    public abstract TBeta computeBeta(THmm hmm, TObs... oseq);

    public abstract Tuple3<TAlpha, TBeta, Double> computeAll(THmm hmm, List<TObs> oseq);

    public abstract Tuple3<TAlpha, TBeta, Double> computeAll(THmm hmm, TObs... oseq);

    public abstract double computeProbability(RegularHmm<TObs> hmm, Collection<ComputationType> flags, List<? extends TObs> oseq);

    public abstract double computeProbability(RegularHmm<TObs> hmm, List<? extends TObs> oseq);

    public abstract double computeProbability(RegularHmm<TObs> hmm, Collection<ComputationType> flags, TObs... oseq);

    public abstract double computeProbability(RegularHmm<TObs> hmm, TObs... oseq);

}
