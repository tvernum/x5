package org.adjective.x5.types;

public class SuccessResult implements X5Result {
    private X5StreamInfo source;

    public SuccessResult(X5StreamInfo source) {
        this.source = source;
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public boolean isOK() {
        return true;
    }

    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public String error() {
        return null;
    }
}
