package net.miningoptimizer.protocol.v2;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * TODO comment this class
 *
 * @author lrosenberg
 * @since 27.11.18 23:51
 */
public class TestSigning {

	@Before public void setSecret(){
		SecretThreadLocal.getSecretThreadLocal().setSecret("mysecret");
	}

	@Test public void testSign(){
		ReplyObject roUnsigned = ReplyObject.success("dummy", "foo");
		ReplyObject roSigned = ReplyObject.successAndSign("dummy", "foo");
		assertNotNull(roSigned.getHash());
		assertNull(roUnsigned.getHash());
	}

	@Test public void testSignAndCheck(){
		ReplyObject myObject = ReplyObject.successAndSign("foo", "bar");

		//check with invalid key
		assertFalse(myObject.checkSignature("nosecrets"));

		//now the right key
		assertTrue(myObject.checkSignature("mysecret"));
		assertTrue(myObject.checkSignature()); //check transmission via ThreadLocal.
	}
}
