package com.toennies.ci1429.app.network.test;

import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.test.impl.URIScriptReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ScriptDevice implements IScriptDevice {

    private final static Logger logger = LogManager.getLogger();
    protected IConfigContainer config;
    private ScriptRunner scriptRunner;
    private IScriptReader scriptReader = new URIScriptReader();

    private boolean hasHandshakeScriptUri = false;
    public final static String PARAM_HANDSHAKE_SCRIPT_URI = "Handshake_Script_URI";
    public final static String PARAM_TEST_SCRIPT = "Script_URI";

    private final ReentrantLock activateLock = new ReentrantLock();
    private final ReentrantLock runningLock = new ReentrantLock();
    private final Condition waitForActivate = this.activateLock.newCondition();

    private boolean isActivated = false;

    private final BlockingDeque<byte[]> requestQueue = new LinkedBlockingDeque<>();
    private final BlockingDeque<byte[]> responseQueue = new LinkedBlockingDeque<>();

    public IConfigContainer getConfig() {
        return config;
    }

    public ScriptDevice(IConfigContainer config) {
        this.config = config;
    }

    private final class ScriptRunner extends Thread {

        {
            this.setDaemon(true);
        }

        public final AtomicBoolean isRunning = new AtomicBoolean(true);
        private Script script;

        public ScriptRunner(Script script) {
            this.script = script;
        }

        @Override
        public void run() {

            ScriptDevice.this.runningLock.lock();
            try {

                Queue<Token> cmd = script.getTokens();
                Token token;
                ScriptCommandFactory commandFactory = new ScriptCommandFactory(ScriptDevice.this);

                while ((token = cmd.poll()) != null && isRunning.get() && !this.isInterrupted()) {
                    IScriptCommand command = commandFactory.getScriptCommand(token);
                    command.execute();
                }

                if (cmd.size() == 0) {
                    logger.info("Test Device: Script finished!");
                } else {
                    logger.warn("Test Device: Script finished with {} unprocessed commands!", cmd.size());
                }

            } catch (IOException e) {
                logger.error("Test Device: Script aborted due to the error: ", e);
                ensureShutdown();
            } finally {
                ScriptDevice.this.runningLock.unlock();
            }
        }

    }

    @Override
    public void runScript() throws IOException {

        try {

            Script script = new Script();
            this.hasHandshakeScriptUri = config.hasEntry(PARAM_HANDSHAKE_SCRIPT_URI);

            //read handshake script file if handshake should be performed
            if (this.hasHandshakeScriptUri) {
                String handshakeScriptUri = config.getEntry(PARAM_HANDSHAKE_SCRIPT_URI);
                script = scriptReader.read(handshakeScriptUri);
            }

            // script file for command (weigh, item_add, etc...)
            String scriptURI = config.getEntry(PARAM_TEST_SCRIPT);
            script.getTokens().addAll(scriptReader.read(scriptURI).getTokens());

            this.activateLock.lock();
            try {
                this.scriptRunner = new ScriptRunner(script);
                this.scriptRunner.start();
                logger.debug("Test Device: Start running script '{}'", scriptURI);
                int paramTimeOut = config.hasEntry(IScriptDevice.PARAM_TIMEOUT) ? config.getIntEntry(IScriptDevice.PARAM_TIMEOUT) : 0;
                try {
                    this.waitForActivate.await(paramTimeOut, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    //ignore
                }
                if (!this.isActivated) {
                    logger.error("Can't activate TestDevice for script '{}'. Check ACTIVATE command or timeout", scriptURI);
                    ensureShutdown();
                } else {
                    logger.info("Test Device is activated");
                }
            } finally {
                this.activateLock.unlock();
            }

        } catch (NoSuchElementException | IOException e) {
            throw new IOException("Could not start script. " + e.getMessage());
        }

    }

    /**
     inheritDoc
     */
    @Override
    public byte[] takeResponse(int waitMillis) throws TimeoutException {
        try {
            byte[] polledResponse = responseQueue.poll(waitMillis, TimeUnit.MILLISECONDS);

            if (polledResponse == null) throw new InterruptedException();

            return polledResponse;
        } catch (InterruptedException e) {
            throw new TimeoutException("takeResponse() is interrupted or returned null.");
        }
    }

    /**
     inheritDoc
     */
    @Override
    public byte[] takeRequest() throws InterruptedException {
        return requestQueue.take();
    }

    @Override
    public void addRequest(byte[] entity) {
        requestQueue.add(entity);
    }

    @Override
    public void addResponse(byte[] entity) {
        responseQueue.add(entity);
    }

    @Override
    public boolean isActivated() {
        return this.scriptRunner != null && this.isActivated;
    }

    @Override
    public void deactivate() {
        ensureShutdown();
        logger.info("Test Device is deactivated");
    }

    private void ensureShutdown() {
        this.isActivated = false;
        if (this.scriptRunner != null)
        {
            this.scriptRunner.isRunning.set(false);
            this.scriptRunner.interrupt();
        }
        this.scriptRunner = null;
    }

    @Override
    public void activate() {

        this.activateLock.lock();

        try {
            this.isActivated = true;
            this.waitForActivate.signalAll();
        } finally {
            this.activateLock.unlock();
        }

    }

}
