package com.grafysi.simulator.dbupdate;

public interface RowBundleLoader extends Runnable {

    @Override
    public void run();

    public void stop();
}
