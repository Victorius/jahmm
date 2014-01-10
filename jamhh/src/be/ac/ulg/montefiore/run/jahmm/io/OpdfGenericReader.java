/*
 * Copyright (c) 2004-2009, Jean-Marc François. All Rights Reserved.
 * Licensed under the New BSD license.  See the LICENSE file.
 */
package be.ac.ulg.montefiore.run.jahmm.io;

import be.ac.ulg.montefiore.run.jahmm.Opdf;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.logging.Logger;

/**
 *
 * @author kommusoft
 */
public class OpdfGenericReader
        extends OpdfReader<Opdf<?>> {

    @Override
    String keyword() {
        throw new AssertionError("Cannot call method");
    }

    @Override
    public Opdf<?> read(StreamTokenizer st)
            throws IOException, FileFormatException {
        if (st.nextToken() != StreamTokenizer.TT_WORD) {
            throw new FileFormatException("Keyword expected");
        }

        for (OpdfReader r : new OpdfReader[]{
            new OpdfIntegerReader(),
            new OpdfGaussianReader(),
            new OpdfGaussianMixtureReader(),
            new OpdfMultiGaussianReader()}) {
            if (r.keyword().equals(st.sval)) {
                st.pushBack();
                return r.read(st);
            }
        }

        throw new FileFormatException("Unknown distribution");
    }
    private static final Logger LOG = Logger.getLogger(OpdfGenericReader.class.getName());
}
