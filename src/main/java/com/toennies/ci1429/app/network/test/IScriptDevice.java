package com.toennies.ci1429.app.network.test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface IScriptDevice {

    String PARAM_TIMEOUT = "timeout";

    /**
     Implementation should perform actions for running script
     */
    void runScript() throws IOException;

    void deactivate();

    boolean isActivated();

    /**
     Method that sets activate flag which corresponds Activate command in scriptfile
     if script file misses Activate command device will be stay deactivated
     */
    void activate();

    void addRequest(byte[] entity);

    void addResponse(byte[] entity);

    /**
     * Blocking operation to retrieve request which should come to device. Wait until request entity comes to device.
     * While device is active.
     * @return request sent to device from pipeline. Never <code>null</code>.
     * @throws InterruptedException If waiting for request is interrupted, e.g. when deactivate command is called.
     */
    byte[] takeRequest() throws InterruptedException;


    /**
     * Blocking operation to retrieve response from device. Wait until response comes from device.
     * Uses the initial specified timeout.
     * @param waitMillis The amount of time to wait for response before throwing a {@link TimeoutException}.
     * @return response sent from device to pipeline. Never <code>null</code>.
     * @throws TimeoutException If the default amount of time elapses noticeable before data becomes available.
     */
    byte[] takeResponse(int waitMillis) throws TimeoutException;

}
