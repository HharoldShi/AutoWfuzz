package AbstractionFuncs;

/**
 * An interface that defines an input/output abstraction functions on a message
 * 
 * @author Serge Gorbunov
 */
public interface AbstractionFunct {
	/**
	 * 
	 * @param input Input message to be abstracted
	 * @return An abstracted message of the string
	 */
	public String abstractMsg( String input ) ;
	
	/** 
	 * Every abstraction function (ie abstractMsg is allowed to have a skip string.
	 * That is when this string is returned from the abstractMsg function, the msg
	 * will be included during construction of FSM. And it wont be modified during
	 * fuzzing
	 * 
	 * @return	Some random string indicating that the msg should not/cannot be abstracted
	 */
	public String getSkipString() ;
}
