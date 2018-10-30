package com.toennies.ci1429.app.hw10.client;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Messages;
import com.toennies.ci1429.app.util.ASCII;

@SuppressWarnings("unchecked")
public class HW10RequestTransformerTest
{
	private static final String ACK = String.valueOf(ASCII.ACK.c);
	private static final String MESSAGE = "abcdef";
	private static final String ACK_MESSAGE = ACK + MESSAGE;
	private static final String MESSAGE_ACK = MESSAGE + ACK;

	private final HW10RequestTransformer transformer = new HW10RequestTransformer(null);

	@Test
	public void testMultiTransformToConInAck()
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Method multiTransformToConIn = this.getMultiTransformToConInMethod();
		List<IMessage> msgs = (List<IMessage>) multiTransformToConIn.invoke(transformer, ACK);
		assertTrue(Messages.equals(transformer.transformToConIn(ACK), msgs.get(0)) && msgs.size() == 1);
	}

	@Test
	public void testMultiTransformToConInMessage()
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Method multiTransformToConIn = this.getMultiTransformToConInMethod();
		List<IMessage> msgs = (List<IMessage>) multiTransformToConIn.invoke(transformer, MESSAGE);
		assertTrue(msgs.size() == 1);
		assertTrue(Messages.equals(transformer.transformToConIn(MESSAGE), msgs.get(0)));
	}

	@Test
	public void testMultiTransformToConInACKMessage()
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Method multiTransformToConIn = this.getMultiTransformToConInMethod();
		List<IMessage> msgs = (List<IMessage>) multiTransformToConIn.invoke(transformer, ACK_MESSAGE);
		assertTrue(msgs.size() == 2);
		assertTrue(Messages.equals(transformer.transformToConIn(ACK), msgs.get(0)));
		assertTrue(Messages.equals(transformer.transformToConIn(MESSAGE), msgs.get(1)));
	}

	@Test
	public void testMultiTransformToConInMessageACK()
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Method multiTransformToConIn = this.getMultiTransformToConInMethod();
		List<IMessage> msgs = (List<IMessage>) multiTransformToConIn.invoke(transformer, MESSAGE_ACK);
		assertTrue(msgs.size() == 2);
		assertTrue(Messages.equals(transformer.transformToConIn(MESSAGE), msgs.get(0)));
		assertTrue(Messages.equals(transformer.transformToConIn(ACK), msgs.get(1)));
	}

	private Method getMultiTransformToConInMethod() throws NoSuchMethodException
	{
		Method method = transformer.getClass().getDeclaredMethod("multiTransformToConIn", String.class);
		method.setAccessible(true);
		return method;
	}
}
