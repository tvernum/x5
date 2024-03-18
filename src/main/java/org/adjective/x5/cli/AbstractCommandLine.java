package org.adjective.x5.cli;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.SuccessResult;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5Result;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.util.Values;

public abstract class AbstractCommandLine implements CommandLine {
    protected static final SuccessResult SUCCESS = new SuccessResult(Values.source("command execution"));

    static X5Result getResult(CommandRunner runner) throws X5Exception {
        if (runner.getValues().hasValue()) {
            final X5Object val = runner.getValues().peek();
            return asResult(val);
        } else {
            return SUCCESS;
        }
    }

    static X5Result asResult(X5Object val) {
        return val.as(X5Type.RESULT).orElse(SUCCESS);
    }
}
