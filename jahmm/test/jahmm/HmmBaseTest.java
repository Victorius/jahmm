package jahmm;

import jahmm.calculators.ComputationType;
import jahmm.calculators.RegularForwardBackwardCalculatorBase;
import jahmm.observables.ObservationInteger;
import jahmm.observables.OpdfInteger;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Logger;
import junit.framework.Assert;
import jutils.probability.ProbabilityUtils;
import jutils.testing.AssertExtensions;
import jutlis.lists.ListArray;
import static org.junit.Assert.fail;
import org.junit.Test;
import utils.TestParameters;

/**
 *
 * @author kommusoft
 */
public class HmmBaseTest {

    private static final double[] hmm_pi = {0.25d, 0.40d, 0.35d};
    private static final double a00 = 0.50d, a01 = 0.15d, a02 = 0.35d;
    private static final double a10 = 0.30d, a11 = 0.40d, a12 = 0.30d;
    private static final double a20 = 0.10d, a21 = 0.20d, a22 = 0.70d;
    private static final double[][] hmm_a = {
        {a00, a01, a02},
        {a10, a11, a12},
        {a20, a21, a22}
    };
    private static final double b00 = 0.45d, b01 = 0.55d;
    private static final double b10 = 0.25d, b11 = 0.75d;
    private static final double b20 = 0.67d, b21 = 0.33d;
    @SuppressWarnings("unchecked")
    private static final ListArray<OpdfInteger> hmm_opdf = new ListArray(
            new OpdfInteger(b00, b01),
            new OpdfInteger(b10, b11),
            new OpdfInteger(b20, b21)
    );
    @SuppressWarnings("unchecked")
    private static final ListArray<ObservationInteger> hmm_sequence = new ListArray<>(
            new ObservationInteger(0x00),
            new ObservationInteger(0x01),
            new ObservationInteger(0x00),
            new ObservationInteger(0x01),
            new ObservationInteger(0x00),
            new ObservationInteger(0x01)
    );
    private static final Logger LOG = Logger.getLogger(HmmBaseTest.class.getName());
    private final RegularHmmBase<ObservationInteger> hmm;

    public HmmBaseTest() {
        hmm = new RegularHmmBase<>(hmm_pi, hmm_a, hmm_opdf);
    }

    @Test
    public void testConstructor() {
        Assert.assertEquals(hmm_pi[0x00], hmm.getPi(0x00));
        Assert.assertEquals(hmm_pi[0x01], hmm.getPi(0x01));
        Assert.assertEquals(hmm_pi[0x02], hmm.getPi(0x02));
        Assert.assertEquals(a00, hmm.getAij(0x00, 0x00));
        Assert.assertEquals(a01, hmm.getAij(0x00, 0x01));
        Assert.assertEquals(a02, hmm.getAij(0x00, 0x02));
        Assert.assertEquals(a00, hmm.getAij(0x00, 0x00));
        Assert.assertEquals(a10, hmm.getAij(0x01, 0x00));
        Assert.assertEquals(a11, hmm.getAij(0x01, 0x01));
        Assert.assertEquals(a12, hmm.getAij(0x01, 0x02));
        Assert.assertEquals(a20, hmm.getAij(0x02, 0x00));
        Assert.assertEquals(a21, hmm.getAij(0x02, 0x01));
        Assert.assertEquals(a22, hmm.getAij(0x02, 0x02));
        for (int i = 0x00; i < 0x03; i++) {
            Assert.assertNotNull(hmm.getOpdf(i));
            AssertExtensions.assertTypeof(OpdfInteger.class, hmm.getOpdf(i));
        }
        for (int i = 0x00; i < 0x03; i++) {
            for (int k = 0x00; k < 0x03; k++) {
                if (i == k) {
                    Assert.assertSame(hmm.getOpdf(k), hmm.getOpdf(i));
                } else {
                    Assert.assertNotSame(hmm.getOpdf(k), hmm.getOpdf(i));
                }
            }
        }
        ObservationInteger oi0 = new ObservationInteger(0x00);
        ObservationInteger oi1 = new ObservationInteger(0x01);
        Assert.assertEquals(b00, hmm.getOpdf(0x00).probability(oi0));
        Assert.assertEquals(b01, hmm.getOpdf(0x00).probability(oi1));
        Assert.assertEquals(b10, hmm.getOpdf(0x01).probability(oi0));
        Assert.assertEquals(b11, hmm.getOpdf(0x01).probability(oi1));
        Assert.assertEquals(b20, hmm.getOpdf(0x02).probability(oi0));
        Assert.assertEquals(b21, hmm.getOpdf(0x02).probability(oi1));
    }

    /**
     * Test of generatePi method, of class HmmBase.
     */
    @Test
    public void testGeneratePi00() {
        try {
            HmmBase.generatePi(0x00);
            fail("Should throw an exception.");
        } catch (IllegalArgumentException t) {
        }
    }

    /**
     * Test of generatePi method, of class HmmBase.
     */
    @Test
    public void testGeneratePi01() {
        try {
            HmmBase.generatePi(-0x01);
            fail("Should throw an exception.");
        } catch (IllegalArgumentException t) {
        }
    }

    /**
     * Test of generatePi method, of class HmmBase.
     */
    @Test
    public void testGeneratePi02() {
        try {
            HmmBase.generatePi(-Integer.MIN_VALUE);
            fail("Should throw an exception.");
        } catch (IllegalArgumentException t) {
        }
    }

    /**
     * Test of generatePi method, of class HmmBase.
     */
    @Test
    public void testGeneratePi03() {
        double[] vals = HmmBase.generatePi(0x01);
        Assert.assertEquals(0x01, vals.length);
        AssertExtensions.assertEquals(1.0d, vals[0x00]);
    }

    /**
     * Test of generatePi method, of class HmmBase.
     */
    @Test
    public void testGeneratePi04() {
        for (int t = 0x00; t < TestParameters.NUMBER_OF_TESTS; t++) {
            int l = ProbabilityUtils.nextInt(TestParameters.TEST_SIZE) + 0x01;
            double[] vals = HmmBase.generatePi(l);
            Assert.assertEquals(l, vals.length);
            double total = 0.0d;
            for (int i = 0x00; i < l; i++) {
                total += vals[i];
                for (int j = i + 0x01; j < l; j++) {
                    AssertExtensions.assertEquals(vals[i], vals[j]);
                }
            }
            AssertExtensions.assertEquals(1.0d, total);
        }
    }

    /**
     * Test of probability method, of class InputHmmBase.
     */
    @Test
    public void testProbability00() {
        int l = 0x01;
        List<ObservationInteger> lst = hmm_sequence.subList(0x00, l);
        double pi00 = hmm_pi[0x00];
        double pi01 = hmm_pi[0x01];
        double pi02 = hmm_pi[0x02];

        double pa = pi00 * b00 + pi01 * b10 + pi02 * b20;

        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.BETA), lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.ALPHA), lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.ALPHA, ComputationType.BETA), lst));
        AssertExtensions.assertEquals(pa, hmm.probability(lst));
    }

    /**
     * Test of probability method, of class InputHmmBase.
     */
    @Test
    public void testProbability01() {
        int l = 0x02;
        List<ObservationInteger> lst = hmm_sequence.subList(0x00, l);
        double pi00 = hmm_pi[0x00] * b00;
        double pi01 = hmm_pi[0x01] * b10;
        double pi02 = hmm_pi[0x02] * b20;

        double pi10 = pi00 * a00 + pi01 * a10 + pi02 * a20;
        double pi11 = pi00 * a01 + pi01 * a11 + pi02 * a21;
        double pi12 = pi00 * a02 + pi01 * a12 + pi02 * a22;

        double pa = pi10 * b01 + pi11 * b11 + pi12 * b21;

        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.BETA), lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.ALPHA), lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.ALPHA, ComputationType.BETA), lst));
        AssertExtensions.assertEquals(pa, hmm.probability(lst));
    }

    /**
     * Test of probability method, of class InputHmmBase.
     */
    @Test
    public void testProbability02() {
        int l = 0x03;
        List<ObservationInteger> lst = hmm_sequence.subList(0x00, l);
        double pi00 = hmm_pi[0x00] * b00;
        double pi01 = hmm_pi[0x01] * b10;
        double pi02 = hmm_pi[0x02] * b20;

        double pi10 = pi00 * a00 + pi01 * a10 + pi02 * a20;
        double pi11 = pi00 * a01 + pi01 * a11 + pi02 * a21;
        double pi12 = pi00 * a02 + pi01 * a12 + pi02 * a22;

        pi10 *= b01;
        pi11 *= b11;
        pi12 *= b21;

        double pi20 = pi10 * a00 + pi11 * a10 + pi12 * a20;
        double pi21 = pi10 * a01 + pi11 * a11 + pi12 * a21;
        double pi22 = pi10 * a02 + pi11 * a12 + pi12 * a22;

        double pa = pi20 * b00 + pi21 * b10 + pi22 * b20;

        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.BETA), lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.ALPHA), lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.ALPHA, ComputationType.BETA), lst));
        AssertExtensions.assertEquals(pa, hmm.probability(lst));
    }

    /**
     * Test of probability method, of class InputHmmBase.
     */
    @Test
    public void testProbability03() {
        int l = 0x04;
        List<ObservationInteger> lst = hmm_sequence.subList(0x00, l);
        double pi00 = hmm_pi[0x00] * b00;
        double pi01 = hmm_pi[0x01] * b10;
        double pi02 = hmm_pi[0x02] * b20;

        double pi10 = pi00 * a00 + pi01 * a10 + pi02 * a20;
        double pi11 = pi00 * a01 + pi01 * a11 + pi02 * a21;
        double pi12 = pi00 * a02 + pi01 * a12 + pi02 * a22;

        pi10 *= b01;
        pi11 *= b11;
        pi12 *= b21;

        double pi20 = pi10 * a00 + pi11 * a10 + pi12 * a20;
        double pi21 = pi10 * a01 + pi11 * a11 + pi12 * a21;
        double pi22 = pi10 * a02 + pi11 * a12 + pi12 * a22;

        pi20 *= b00;
        pi21 *= b10;
        pi22 *= b20;

        double pi30 = pi20 * a00 + pi21 * a10 + pi22 * a20;
        double pi31 = pi20 * a01 + pi21 * a11 + pi22 * a21;
        double pi32 = pi20 * a02 + pi21 * a12 + pi22 * a22;

        double pa = pi30 * b01 + pi31 * b11 + pi32 * b21;

        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.BETA), lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.ALPHA), lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.ALPHA, ComputationType.BETA), lst));
        AssertExtensions.assertEquals(pa, hmm.probability(lst));
    }

    /**
     * Test of probability method, of class InputHmmBase.
     */
    @Test
    public void testProbability04() {
        int l = 0x05;
        List<ObservationInteger> lst = hmm_sequence.subList(0x00, l);
        double pi00 = hmm_pi[0x00] * b00;
        double pi01 = hmm_pi[0x01] * b10;
        double pi02 = hmm_pi[0x02] * b20;

        double pi10 = pi00 * a00 + pi01 * a10 + pi02 * a20;
        double pi11 = pi00 * a01 + pi01 * a11 + pi02 * a21;
        double pi12 = pi00 * a02 + pi01 * a12 + pi02 * a22;

        pi10 *= b01;
        pi11 *= b11;
        pi12 *= b21;

        double pi20 = pi10 * a00 + pi11 * a10 + pi12 * a20;
        double pi21 = pi10 * a01 + pi11 * a11 + pi12 * a21;
        double pi22 = pi10 * a02 + pi11 * a12 + pi12 * a22;

        pi20 *= b00;
        pi21 *= b10;
        pi22 *= b20;

        double pi30 = pi20 * a00 + pi21 * a10 + pi22 * a20;
        double pi31 = pi20 * a01 + pi21 * a11 + pi22 * a21;
        double pi32 = pi20 * a02 + pi21 * a12 + pi22 * a22;

        pi30 *= b01;
        pi31 *= b11;
        pi32 *= b21;

        double pi40 = pi30 * a00 + pi31 * a10 + pi32 * a20;
        double pi41 = pi30 * a01 + pi31 * a11 + pi32 * a21;
        double pi42 = pi30 * a02 + pi31 * a12 + pi32 * a22;

        double pa = pi40 * b00 + pi41 * b10 + pi42 * b20;

        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.BETA), lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.ALPHA), lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.ALPHA, ComputationType.BETA), lst));
        AssertExtensions.assertEquals(pa, hmm.probability(lst));
    }

    /**
     * Test of probability method, of class InputHmmBase.
     */
    @Test
    public void testProbability05() {
        int l = 0x06;
        List<ObservationInteger> lst = hmm_sequence.subList(0x00, l);
        double pi00 = hmm_pi[0x00] * b00;
        double pi01 = hmm_pi[0x01] * b10;
        double pi02 = hmm_pi[0x02] * b20;

        double pi10 = pi00 * a00 + pi01 * a10 + pi02 * a20;
        double pi11 = pi00 * a01 + pi01 * a11 + pi02 * a21;
        double pi12 = pi00 * a02 + pi01 * a12 + pi02 * a22;

        pi10 *= b01;
        pi11 *= b11;
        pi12 *= b21;

        double pi20 = pi10 * a00 + pi11 * a10 + pi12 * a20;
        double pi21 = pi10 * a01 + pi11 * a11 + pi12 * a21;
        double pi22 = pi10 * a02 + pi11 * a12 + pi12 * a22;

        pi20 *= b00;
        pi21 *= b10;
        pi22 *= b20;

        double pi30 = pi20 * a00 + pi21 * a10 + pi22 * a20;
        double pi31 = pi20 * a01 + pi21 * a11 + pi22 * a21;
        double pi32 = pi20 * a02 + pi21 * a12 + pi22 * a22;

        pi30 *= b01;
        pi31 *= b11;
        pi32 *= b21;

        double pi40 = pi30 * a00 + pi31 * a10 + pi32 * a20;
        double pi41 = pi30 * a01 + pi31 * a11 + pi32 * a21;
        double pi42 = pi30 * a02 + pi31 * a12 + pi32 * a22;

        pi40 *= b00;
        pi41 *= b10;
        pi42 *= b20;

        double pi50 = pi40 * a00 + pi41 * a10 + pi42 * a20;
        double pi51 = pi40 * a01 + pi41 * a11 + pi42 * a21;
        double pi52 = pi40 * a02 + pi41 * a12 + pi42 * a22;

        double pa = pi50 * b01 + pi51 * b11 + pi52 * b21;

        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.BETA), lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.ALPHA), lst));
        AssertExtensions.assertEquals(pa, RegularForwardBackwardCalculatorBase.Instance.computeProbability(hmm, EnumSet.of(ComputationType.ALPHA, ComputationType.BETA), lst));
        AssertExtensions.assertEquals(pa, hmm.probability(lst));
    }

}
