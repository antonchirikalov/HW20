package com.toennies.ci1429.app.network.test;

import java.io.IOException;

public interface IScriptReader
{
    Script read(String scriptName) throws IOException;
}
