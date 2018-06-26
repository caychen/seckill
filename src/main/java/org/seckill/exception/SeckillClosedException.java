package org.seckill.exception;


/**
 * 秒杀关闭异常
 * @author Cay
 *
 */
public class SeckillClosedException extends SeckillException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SeckillClosedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public SeckillClosedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	
}
