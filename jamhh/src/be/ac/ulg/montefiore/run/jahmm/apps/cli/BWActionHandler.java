/*
 * Copyright (c) 2004-2009, Jean-Marc François. All Rights Reserved.
 * Licensed under the New BSD license.  See the LICENSE file.
 */
package be.ac.ulg.montefiore.run.jahmm.apps.cli;

import be.ac.ulg.montefiore.run.jahmm.CentroidFactory;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.Observation;
import be.ac.ulg.montefiore.run.jahmm.Opdf;
import be.ac.ulg.montefiore.run.jahmm.apps.cli.CommandLineArguments.Arguments;
import static be.ac.ulg.montefiore.run.jahmm.apps.cli.CommandLineArguments.checkArgs;
import static be.ac.ulg.montefiore.run.jahmm.apps.cli.Types.relatedObjs;
import be.ac.ulg.montefiore.run.jahmm.io.FileFormatException;
import be.ac.ulg.montefiore.run.jahmm.io.HmmReader;
import static be.ac.ulg.montefiore.run.jahmm.io.HmmReader.read;
import be.ac.ulg.montefiore.run.jahmm.io.HmmWriter;
import static be.ac.ulg.montefiore.run.jahmm.io.HmmWriter.write;
import be.ac.ulg.montefiore.run.jahmm.io.OpdfReader;
import be.ac.ulg.montefiore.run.jahmm.io.OpdfWriter;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchScaledLearner;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.EnumSet;
import static java.util.EnumSet.of;
import java.util.List;
import java.util.logging.Logger;

/**
 * Applies the Baum-Welch learning algorithm.
 */
class BWActionHandler
        extends ActionHandler {

    @Override
    public void act()
            throws IOException, FileFormatException,
            AbnormalTerminationException {
        EnumSet<Arguments> args = of(
                Arguments.OPDF,
                Arguments.OUT_HMM,
                Arguments.IN_HMM,
                Arguments.IN_SEQ,
                Arguments.NB_ITERATIONS);
        checkArgs(args);

        int nbIterations = Arguments.NB_ITERATIONS.getAsInt();
        OutputStream outStream = Arguments.OUT_HMM.getAsOutputStream();
        Writer hmmWriter = new OutputStreamWriter(outStream);
        InputStream hmmStream = Arguments.IN_HMM.getAsInputStream();
        InputStream seqStream = Arguments.IN_SEQ.getAsInputStream();
        Reader hmmReader = new InputStreamReader(hmmStream, Cli.CHARSET);
        Reader seqReader = new InputStreamReader(seqStream, Cli.CHARSET);

        learn(relatedObjs(), hmmReader, seqReader, hmmWriter,
                nbIterations);

        hmmWriter.flush();
    }

    private <O extends Observation & CentroidFactory<O>> void
            learn(RelatedObjs<O> relatedObjs, Reader hmmFileReader,
                    Reader seqFileReader, Writer hmmFileWriter,
                    int nbIterations)
            throws IOException, FileFormatException {
        List<List<O>> seqs = relatedObjs.readSequences(seqFileReader);
        OpdfReader<? extends Opdf<O>> opdfReader = relatedObjs.opdfReader();
        OpdfWriter<? extends Opdf<O>> opdfWriter = relatedObjs.opdfWriter();

        Hmm<O> initHmm = read(hmmFileReader, opdfReader);
        BaumWelchLearner bw = new BaumWelchScaledLearner();
        bw.setNbIterations(nbIterations);
        Hmm<O> hmm = bw.learn(initHmm, seqs);
        write(hmmFileWriter, opdfWriter, hmm);
    }
    private static final Logger LOG = Logger.getLogger(BWActionHandler.class.getName());
}
