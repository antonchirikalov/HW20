package com.toennies.ci1429.app.util;

import com.toennies.ci1406.lib.utils.AnnotationUtils;
import com.toennies.ci1429.app.network.protocol.AtProtocol;
import com.toennies.ci1429.app.network.socket.AtSocket;

/**
 * Class that provides some static methods for working with {@link AtSocket}
 * and {@link AtProtocol} description information.
 */
public class DescriptionUtil {

	/**
	 * Returns the description of a class. Provided class needs to be marked by
	 * {@link AtSocket} or {@link AtSocket} annotation.
	 * 
	 */
	public static String getDescriptionByClass(Class<?> clazz) {
		AtSocket connectorAnnotation = AnnotationUtils.<AtSocket>returnAnnotationOfClass(clazz,
				AtSocket.class);
		AtProtocol protocolAnnotation = AnnotationUtils.<AtProtocol>returnAnnotationOfClass(clazz, AtProtocol.class);

		return getDescriptionByAnnotation(connectorAnnotation, protocolAnnotation);
	}

	/**
	 * Ether a class is marked by {@link AtProtocol} or {@link AtSocket}
	 * annotation. This methods returns the description information acording
	 * which Annoation marks the respective class.
	 */
	private static String getDescriptionByAnnotation(AtSocket atc, AtProtocol atp) {
		if (atc != null) {
			return atc.value();
		}
		if (atp != null) {
			return atp.value();
		}
		return null;
	}


	private DescriptionUtil() {
		//no instance
	}

}
