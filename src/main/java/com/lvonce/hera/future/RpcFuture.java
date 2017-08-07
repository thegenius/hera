package com.lvonce.hera.future;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.context.RpcRequest;
import com.lvonce.hera.context.RpcResponse;
import com.lvonce.hera.exception.RpcException;
import com.lvonce.hera.exception.RpcExecuteException;
import com.lvonce.hera.exception.RpcTimeoutException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class RpcFuture {

	public final static int STATE_AWAIT = 0;
	public final static int STATE_SUCCESS = 1;
	public final static int STATE_EXCEPTION = 2;

	private final ReentrantLock lock;	
	private final CountDownLatch countDownLatch;

	private volatile int state; 
	private Object result;
	private Throwable throwable;
	private RpcRequest request;
	private RpcSuccessCallback successCallback;
	private RpcFailedCallback failedCallback;
	
	public RpcFuture(RpcRequest request) {
		this.state = STATE_AWAIT;
		this.request = request;
		this.lock = new ReentrantLock();
		this.countDownLatch = new CountDownLatch(1);
	}
	
	public Object get() throws RpcExecuteException {
        try {
			this.countDownLatch.await();
			if(this.state == STATE_SUCCESS) {
				return this.result;
			} else if (this.state == STATE_EXCEPTION) {
				throw new RpcExecuteException(this.request.toString(), "remote node exception: " + throwable.toString());
			} else {
				String exceptionMessage = "RpcFuture get() state["+this.state+"] error, the state expect to be SUCCESS or EXCEPTION!";
				throw new RpcExecuteException(this.request.toString(), exceptionMessage);
			}
        } catch (Exception e) {
			throw new RpcExecuteException(this.request.toString(), e.getMessage());
        } 
	}
	
	public Object get(long timeout) throws RpcException {
        try {
			boolean awaitSuccess = this.countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
			if(!awaitSuccess) {
				throw new RpcTimeoutException(this.request.toString());
			}
			if (this.state == STATE_SUCCESS) {
				return this.result;
			} else if (this.state == STATE_EXCEPTION) {
				throw new RpcExecuteException(this.request.toString(), "remote node exception: " + throwable.toString());
			} else {
				String exceptionMessage = "RpcFuture get("+timeout+") state["+this.state+"] error, the state expect to be SUCCESS or EXCEPTION!";
				throw new RpcExecuteException(this.request.toString(), exceptionMessage);
			}
        } catch (Exception e) {
			throw new RpcExecuteException(this.request.toString(), e.getMessage());
        } 
	}
	
	public void accept(RpcResponse response) {
		if(response.isInvokeSuccess()) {				
			this.setResult(response.getResult());	
		} else {
			this.setThrowable(response.getThrowable());				
		}
	} 


	/**
	 * get result successfully
	 * @param result
	 */
	public void setResult(Object result) throws RpcExecuteException {
 		this.lock.lock();
        try {
			RpcLogger.debug(getClass(), "future set result state[" + state + "]");	
			if (this.state == STATE_AWAIT) {
				this.state = STATE_SUCCESS;
				this.result = result;
				if(this.successCallback != null) {
					this.successCallback.apply(this.request, this.result);
				}
				countDownLatch.countDown();
			}
        } catch (Exception e) {
			throw new RpcExecuteException(this.request.toString(), e.getMessage());
        } finally {
            this.lock.unlock();
        }
	}
	
	/**
	 * exception occur when invoke
	 * @param throwable
	 */
	public void setThrowable(Throwable throwable) throws RpcExecuteException {
 		this.lock.lock();
        try {
        	if (this.state == STATE_AWAIT) {
				this.state =  STATE_EXCEPTION;
				this.throwable = throwable;
				if(this.failedCallback != null) {
					this.failedCallback.apply(this.request, (RpcException)this.throwable);
				}
				countDownLatch.countDown();
			}
        } catch (Exception e) {
			throw new RpcExecuteException(this.request.toString(), e.getMessage());
        } finally {
            this.lock.unlock();
        }
	}
	
	//public boolean isDone() {
	//	return state.get() != STATE_AWAIT;
	//}
	
	public void then(RpcSuccessCallback successCallback) {		
		this.lock.lock();
        try {
			if (this.state == STATE_AWAIT) {
				this.successCallback = successCallback;	
			} else if (this.state == STATE_SUCCESS) {
				successCallback.apply(this.request, this.result);
			}
        } catch (Exception e) {
			throw new RpcExecuteException(this.request.toString(), e.getMessage());
        } finally {
            this.lock.unlock();
        }
	}
	
	public void then(RpcSuccessCallback successCallback, RpcFailedCallback failedCallback) {		
 		this.lock.lock();
        try {
			if (this.state == STATE_AWAIT) {
				this.successCallback = successCallback;	
				this.failedCallback = failedCallback;
			} else if (this.state == STATE_SUCCESS) {
				successCallback.apply(this.request, this.result);
			} else if (this.state == STATE_EXCEPTION) {
				failedCallback.apply(this.request, (RpcException)this.throwable);
			}
        } catch (Exception e) {
			throw new RpcExecuteException(this.request.toString(), e.getMessage());
        } finally {
            this.lock.unlock();
        }
	}
}
