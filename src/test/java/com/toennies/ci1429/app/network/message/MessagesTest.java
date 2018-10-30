package com.toennies.ci1429.app.network.message;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MessagesTest
{
	private static final byte[] ARR_NULL = null;

	private static final IMessage NULL = null;

	private static final IMessage MSG_NULL = new Message(ARR_NULL);
	private static final IMessage MSG_NULL2 = new Message(ARR_NULL);

	private static final IMessage MSG_ABC = new Message("abc".getBytes());

	private static final IMessage MSG_ABC_DEF = new Message("abc".getBytes(), "def".getBytes());

	private static final IMessage MSG_DEF = new Message("def".getBytes());

	private static final IMessage MSG_ABC2 = new Message("abc".getBytes());

	@Test
	public void equalsTestSameRef()
	{
		assertTrue(Messages.equals(NULL, NULL));
		assertTrue(Messages.equals(MSG_ABC, MSG_ABC));
		assertTrue(Messages.equals(MSG_NULL, MSG_NULL));
	}

	@Test
	public void equalsTestOneNull()
	{
		assertFalse(Messages.equals(MSG_ABC, NULL));
		assertFalse(Messages.equals(NULL, MSG_ABC));
	}

	@Test
	public void equalsTestDifferentSize()
	{
		assertFalse(Messages.equals(MSG_ABC, MSG_ABC_DEF));
		assertFalse(Messages.equals(MSG_ABC_DEF, MSG_ABC));
	}

	@Test
	public void equalsTestSameSizeDifferentContent()
	{
		assertFalse(Messages.equals(MSG_ABC, MSG_DEF));
		assertFalse(Messages.equals(MSG_DEF, MSG_ABC));
	}

	@Test
	public void equalsTestSameContent()
	{
		assertTrue(Messages.equals(MSG_ABC, MSG_ABC2));
		assertTrue(Messages.equals(MSG_ABC2, MSG_ABC));
	}

	@Test
	public void equalsTestNullContent()
	{
		assertTrue(Messages.equals(MSG_NULL, MSG_NULL2));
		assertTrue(Messages.equals(MSG_NULL2, MSG_NULL));
	}

}
