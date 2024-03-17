package org.adjective.x5.types;

public class FailureResult implements X5Result {
    private X5StreamInfo source;
    private final String error;

    public FailureResult(String error, X5StreamInfo source) {
        this.source = source;
        this.error = error;
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public boolean isOK() {
        return false;
    }

    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public String error() {
        return error;
    }
}
