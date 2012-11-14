package com.ccc.cccframework.webapp_concurrency;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

/**
 * This is a task executor managed by users.
 * @author Adam Gibson
 *
 */
public class UserThreadExecutor extends ConcurrentTaskExecutor{
	
	
	/**
	 * This will return the task executor for a given user
	 * @param userName the user to get the executor for
	 * @return the executor for the given user
	 */
	public static UserThreadExecutor getForUser(String userName) {
		return tasks.get(userName);
	}
	
	/**
	 * This will create an executor for the given user
	 * @param userName the name of the user to create an executor for
	 */
	public static void addForUser(String userName) {
		tasks.put(userName,new UserThreadExecutor());
	}
	/**
	 * This will submit the given task for execution for the given user
	 * @param userName the user to submit the task for
	 * @param task the task to run
	 */
	public static void executeTaskForUser(String userName,Runnable task) {
	
		
		UserThreadExecutor exec=tasks.get(userName);
		if(exec!=null) {
			exec.execute(task);
		}
		else {
			addForUser(userName);
			 exec=tasks.get(userName);
			 exec.execute(task);
		}
	}//end executeTaskForUser
	
	
	
	public static void removeExecutor(String userName) {
		tasks.remove(userName);
	}
	private static Map<String,UserThreadExecutor> tasks = new HashMap<String,UserThreadExecutor>();
}
