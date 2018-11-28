package net.miningoptimizer.protocol.v2;

/**
 * Threadlocal container for secret. This is helpful to allow convenience methods in ReplyObject.
 *
 * @author lrosenberg
 * @since 27.11.18 23:45
 */
public class SecretThreadLocal {

	/**
	 * Secret for signing.
	 */
	private String secret;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	/**
	 * The thread local variable associated with the current thread.
	 */
	private static InheritableThreadLocal<SecretThreadLocal> secretThreadLocal = new InheritableThreadLocal<SecretThreadLocal>() {
		@Override
		protected synchronized SecretThreadLocal initialValue() {
			return new SecretThreadLocal();
		}

		@Override
		protected SecretThreadLocal childValue(SecretThreadLocal parentValue) {
			SecretThreadLocal ret = new SecretThreadLocal();
			ret.secret = parentValue.secret;
			return ret;
		}
	};

	/**
	 * Returns the SecretThreadLocal assigned to this thread.
	 *
	 * @return previously assigned or new SecretThreadLocal object.
	 */
	public static SecretThreadLocal getSecretThreadLocal(){
		return secretThreadLocal.get();
	}

	/**
	 * Removes the thread local instance of the SecretThreadLocal, in order to prevent webapp redeployment leaks.
	 */
	public static void remove(){
		secretThreadLocal.remove();
	}

}
