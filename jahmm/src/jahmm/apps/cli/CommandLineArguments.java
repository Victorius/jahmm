/*
 * Copyright (c) 2004-2009, Jean-Marc François. All Rights Reserved.
 * Licensed under the New BSD license.  See the LICENSE file.
 */
package jahmm.apps.cli;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.EnumSet;

class CommandLineArguments {

    static void parse(String[] args)
            throws WrongArgumentsException {
        argLoop:
        for (int i = 1; i < args.length; i++) { // args[0] is main action
            for (Arguments arg : Arguments.values()) {
                if (arg.argString.equals(args[i])) {
                    if (arg.getIsSet()) {
                        throw new WrongArgumentsException("Argument '"
                                + args[i] + "' given twice");
                    }
                    arg.setIsSet(true);

                    if (arg.hasAllowedValues()) {
                        if (i == args.length - 1) {
                            throw new WrongArgumentsException("Argument '"
                                    + args[i] + "' calls for an option");
                        }

                        String subArg = args[++i];
                        if (!arg.allowedValues[0].isEmpty()
                                && !Arrays.asList(arg.allowedValues).
                                contains(subArg)) {
                            throw new WrongArgumentsException("Invalid "
                                    + "option '" + subArg + "'");
                        }

                        arg.set(subArg);
                    }

                    continue argLoop;
                }
            }

            throw new WrongArgumentsException("Unknown argument '"
                    + args[i] + "'");
        }
    }

    /**
     * Checks the command line arguments.
     *
     * @param args A set of mandatory arguments. The arguments that directly
     * depends on the opdf are automatically added.
     */
    static void checkArgs(EnumSet<Arguments> args)
            throws WrongArgumentsException {
        if (args.contains(Arguments.OPDF)) {
            String opdf = Arguments.OPDF.get();

            if (opdf.equals("integer")) {
                args.add(Arguments.INTEGER_RANGE);
            } else if (opdf.equals("multi_gaussian")) {
                args.add(Arguments.VECTOR_DIMENSION);
            } else if (opdf.equals("gaussian"))
				; else if (opdf.equals("gaussian_mixture")) {
                args.add(Arguments.NB_GAUSSIANS);
            } else {
                new AssertionError("Unknown observation type '" + opdf + "'");
            }
        }

        for (Arguments arg : args) {
            if (!arg.getIsSet() && !arg.hasDefaultValue()) {
                throw new WrongArgumentsException("Argument '"
                        + arg.argString + "' expected");
            }
        }

        for (Arguments arg : EnumSet.complementOf(args)) {
            if (arg.getIsSet()) {
                throw new WrongArgumentsException("Argument '"
                        + arg.argString + "' not expected");
            }
        }
    }

    static ActionHandler.Actions parseAction(String[] args) {
        if (args.length == 0) {
            return null;
        }

        ActionHandler.Actions mainAction = null;
        for (ActionHandler.Actions action : ActionHandler.Actions.values()) {
            if (action.toString().equals(args[0])) {
                mainAction = action;
            }
        }

        return mainAction;
    }

    static void reset() {
        for (Arguments arg : Arguments.values()) {
            arg.setIsSet(false);
        }
    }

    private CommandLineArguments() {
    }
    /*
     * Allowed command-line arguments description
     * The special allowed arguments list "" means any argument is allowed.
     * The special allowed arguments list "x" means that x is the default
     * value.
     */

    static enum Arguments {

        IN_HMM("-i", "-"),
        IN_KL_HMM("-ikl", ""),
        OUT_HMM("-o", "-"),
        IN_SEQ("-is", ""),
        OUT_SEQS("-os", "-"),
        OPDF("-opdf", "integer", "gaussian", "gaussian_mixture",
                "multi_gaussian"),
        INTEGER_RANGE("-r", ""),
        NB_GAUSSIANS("-ng", ""),
        VECTOR_DIMENSION("-d", ""),
        NB_ITERATIONS("-ni", "10"),
        NB_STATES("-n", "");

        final String argString;       // The expected string for the arg
        final String[] allowedValues; // Accepted values.  If "", any
        private String value;         // Sub-argument value
        private final String defaultValue;
        private boolean isSet;        // Has the argument being read?

        Arguments(String argString, String... allowedValues) {
            this.isSet = false;
            this.argString = argString;
            if (allowedValues.length == 1 && !allowedValues[0].isEmpty()) {
                // Default value given
                this.defaultValue = allowedValues[0];
                this.allowedValues = new String[]{""};
            } else {
                this.defaultValue = null;
                this.allowedValues = allowedValues;
            }
        }

        boolean hasAllowedValues() {
            return allowedValues.length != 0;
        }

        void set(String o) {
            value = o;
        }

        boolean getIsSet() {
            return isSet;
        }

        void setIsSet(boolean isSet) {
            this.isSet = isSet;
        }

        boolean hasDefaultValue() {
            return defaultValue != null;
        }

        String get()
                throws WrongArgumentsException {
            if (!isSet) {
                if (hasDefaultValue()) {
                    return defaultValue;
                } else {
                    throw new WrongArgumentsException("Argument '"
                            + argString + "' expected");
                }
            }

            return value;
        }

        String getDefault() {
            if (!hasDefaultValue()) {
                throw new UnsupportedOperationException("No default value");
            }

            return defaultValue;
        }

        int getAsInt()
                throws WrongArgumentsException {
            int i = -1;

            try {
                i = Integer.parseInt(get());
            } catch (NumberFormatException e) {
                throw new WrongArgumentsException("'" + get()
                        + "' is not a number; number expected");
            }

            return i;
        }

        InputStream getAsInputStream()
                throws FileNotFoundException, WrongArgumentsException {
            if (get().equals("-")) {
                return System.in;
            } else {
                return new FileInputStream(get());
            }
        }

        OutputStream getAsOutputStream()
                throws FileNotFoundException, WrongArgumentsException {
            if (get().equals("-")) {
                return System.out;
            }

            return new FileOutputStream(get());
        }
    };
}
