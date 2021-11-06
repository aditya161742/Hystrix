package com.hystrix.test;

import java.sql.SQLException;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@RestController
public class HystrixTest {
	
	int counter =0;
	
	@RequestMapping(value = "/")
	@HystrixCommand(fallbackMethod = "fallback_hello", commandProperties = {
			   @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
			})
	public String hello() throws InterruptedException {
	   Thread.sleep(3000);
	   return "Welcome Hystrix";
	}
	
	
	@RequestMapping(value = "/excpetion_case")
	@HystrixCommand(fallbackMethod = "exception_fallback")
	public String exceptionOccurs() throws InterruptedException {
	  
	   throw new InterruptedException();
	}
	
	
	@RequestMapping("/retry_case")
    @Retryable(value = { SQLException.class }, maxAttempts = 3,backoff=@Backoff(delay=5000))
    public String simpleRetry() throws SQLException {
        counter++;
        System.out.println("Billing Service Failed "+ counter);
        throw new SQLException();

    }

    @Recover
    public String recover(SQLException t){
    	System.out.println("Service recovering");
        return "Service recovered from billing service failure.";
    }
    
	private String fallback_hello() {
		   return "Request fails. It takes long time to response";
	}
	
	private String exception_fallback() {
		   return "SQL Exception Occured";
	}
	
}
