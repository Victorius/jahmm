/*
 * Copyright (c) 2004-2009, Jean-Marc François. All Rights Reserved.
 * Licensed under the New BSD license.  See the LICENSE file.
 */
package jahmm.io;

import jahmm.observables.ObservationReal;
import jahmm.observables.ObservationVector;
import java.io.IOException;
import java.io.Writer;

/**
 * Writes an {@link ObservationReal ObservationReal} up to (and including) the
 * semi-colon.
 */
public class ObservationVectorWriter
        extends ObservationWriter<ObservationVector> {

    @Override
    public void write(ObservationVector observation, Writer writer)
            throws IOException {
        String s = "[ ";

        for (int i = 0; i < observation.dimension(); i++) {
            s += observation.value(i) + " ";
        }

        writer.write(s + "]; ");
    }
}
