package io.horizpipes.connector;

public interface SourceOperator {

    void start();

    void stop();

    void requestMetadata();
}
