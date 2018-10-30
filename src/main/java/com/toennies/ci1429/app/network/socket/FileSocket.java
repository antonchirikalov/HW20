/**
 * 
 */
package com.toennies.ci1429.app.network.socket;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeoutException;

import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;

/**
 * A socket that can be used to dump everything given to it into a specific file.
 * @author renkenh
 */
@AtSocket(value="File Socket")
@Parameter(name=FileSocket.PARAM_FILEPATH, isRequired=true, value=FileSocket.DEFAULT_TEMPDIR, toolTip="A file path. '${java.io.tempdir}' as a value writes to the system wide temp dir.")
@Parameter
public class FileSocket implements ISocket
{

	/** File path parameter. If the file path parameter is set to {@link #DEFAULT_TEMPDIR}. Then a temp file is created and used. */
	public static final String PARAM_FILEPATH = "filepath";
	/** CONST to indicate that a temp file should be used to dump the data into. The temp file is called {@link #TEMPFILE}. */
	public static final String DEFAULT_TEMPDIR = "${java.io.tmpdir}";
	/** The name of the temp file - if used. */
	public static final String TEMPFILE = "hw20filesocket.log";
	

	private Path path;
	

	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		String paramPath = config.getEntry(PARAM_FILEPATH);
		if (paramPath.equals(DEFAULT_TEMPDIR))
			paramPath = System.getProperty(DEFAULT_TEMPDIR) + "/" + TEMPFILE;
		this.path = Paths.get(paramPath);
	}


	@Override
	public boolean isConnected()
	{
		return this.path != null;
	}

	@Override
	public byte[] poll()
	{
		//we do not generate any data
		return null;
	}

	public byte[] pop() throws TimeoutException
	{
		throw new TimeoutException("Filesocket does not provide any data.");
	}

	@Override
	public void push(byte[] entity) throws IOException
	{
		Files.write(this.path, entity, StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
	}

	@Override
	public void disconnect()
	{
		this.path = null;
	}

	@Override
	public void shutdown()
	{
		this.disconnect();
	}

}
