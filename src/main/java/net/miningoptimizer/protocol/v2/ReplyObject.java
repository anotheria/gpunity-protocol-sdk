package net.miningoptimizer.protocol.v2;

import org.apache.commons.codec.digest.DigestUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Random;

/**
 * ReplyObject is a holder object for all rest api replies. It contains some status info and additional objects
 * with requested information.
 *
 * @author lrosenberg
 * @since 28.11.2018
 */
@XmlRootElement(name="reply")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReplyObject {
	/**
	 * True if the call was successful.
	 */
	@XmlElement
	private boolean success;

	/**
	 * Random long to ensure different payload.
	 */
	@XmlElement
	private long random = randomProvider.nextLong();
	/**
	 * Optional message in case call failed (exception message).
	 */
	@XmlElement(required = false,nillable = false)
	private String message;

	/**
	 * Map with results object.
	 */
	@XmlElement
	private HashMap<String, Object> results = new HashMap<>();

	/**
	 * Object hash.
	 */
	@XmlElement
	private String hash;

	/**
	 * Random to create new random long values.
	 */
	private static Random randomProvider = new Random(System.nanoTime());

	/**
	 * Creates a new empty result object.
	 */
	public ReplyObject(){
	}

	/**
	 * Creates a new result object with one result.
	 * @param name name of the result bean.
	 * @param result object for the first result bean.
	 */
	public ReplyObject(String name, Object result){
		results.put(name, result);
	}

	/**
	 * Adds
	 * @param name
	 * @param result
	 */
	public void addResult(String name, Object result){
		results.put(name, result);
	}

	/**
	 * Factory method that creates a new reply object for successful request.
	 * @param name
	 * @param result
	 * @return
	 */
	public static ReplyObject success(String name, Object result){
		ReplyObject ret = new ReplyObject(name, result);
		ret.success = true;
		return ret;
	}

	/**
	 * Factory method that creates a new reply object for successful request.
	 * @return
	 */
	public static ReplyObject success(){
		ReplyObject ret = new ReplyObject();
		ret.success = true;
		return ret;
	}

	/**
	 * Factory method that creates a new erroneous reply object.
	 * @param message
	 * @return
	 */
	public static ReplyObject error(String message){
		ReplyObject ret = new ReplyObject();
		ret.success = false;
		ret.message = message;
		return ret;
	}

	public static ReplyObject error(Throwable exc){
		ReplyObject ret = new ReplyObject();
		ret.success = false;
		ret.message = exc.getClass().getSimpleName()+": "+exc.getMessage();
		return ret;
	}

	/**
	 * Same as success(name, result), but instantly signs the reply.
	 * @param name
	 * @param result
	 * @return
	 */
	public static ReplyObject successAndSign(String name, Object result){
		return success(name, result).sign();
	}
	/**
	 * Same as success(), but instantly signs the reply.
	 * @return
	 */
	public static ReplyObject successAndSign(){
		return ReplyObject.success().sign();
	}
	/**
	 * Same as error(message), but instantly signs the reply.
	 * @param message errormessage.
	 * @return signed and ready to use ReplyObject.
	 */
	public static ReplyObject errorAndSign(String message){
		return error(message).sign();
	}

	/**
	 * Same as error(throwable), but instantly signs the reply.
	 * @param throwable caught throwable.
	 * @return
	 */
	public static ReplyObject errorAndSign(Throwable throwable){
		return error(throwable).sign();
	}


	@Override public String toString(){
		StringBuilder ret = new StringBuilder("ReplyObject ");
		ret.append("Success: ").append(success);
		if (message!=null){
			ret.append(", Message: ").append(message);
		}
		ret.append(", Results: ").append(results);
		return ret.toString();
	}

	public boolean isSuccessful(){
		return success;
	}

	public String getMessage(){
		return message;
	}

	public HashMap getResults(){
		return results;
	}

	public long getRandom() {
		return random;
	}

	public String getHash() {
		return hash;
	}

	public ReplyObject sign(String secret){
		String pass = (message == null ? "" : message)
				+ random + results + success + secret;
		hash = DigestUtils.sha256Hex(pass);
		return this;
	}

	public ReplyObject sign(){
		return sign(SecretThreadLocal.getSecretThreadLocal().getSecret());
	}

	public boolean checkSignature(String secret){
		String transmittedHash = hash;

		String pass = (message == null ? "" : message)
				+ random + results + success + secret;
		hash = DigestUtils.sha256Hex(pass);

		boolean result = hash != null && hash.equals(transmittedHash);

		//set back original hash just in case someone wants to check this object second time (it would then pass).
		hash = transmittedHash;

		return result;
	}

	public boolean checkSignature(){
		return checkSignature(SecretThreadLocal.getSecretThreadLocal().getSecret());
	}
}
