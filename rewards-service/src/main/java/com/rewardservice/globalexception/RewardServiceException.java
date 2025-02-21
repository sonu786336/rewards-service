package com.rewardservice.globalexception;

public class RewardServiceException extends RuntimeException{
    public RewardServiceException(String msg){
        super(msg);
    }
}
