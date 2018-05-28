//Christopher Kilian
//CS 420 - Spring 2018
//Programming Project #3 - Isolation Game

package isolationgame;

import java.time.Instant;

//for easy checking of time remaining
public class MoveTimer {
    private long maxTimeMillis;
    private long startTime;
    
    //Constructor - max time must be passed in number of milliseconds
    //Note that the timer starts as soon as it's instantiated
    public MoveTimer(long maxTime){
        //if a negative value is passed, default to 500 milliseconds (no negative times are allowed)
        if(maxTime > 0){
            maxTimeMillis = maxTime;
        }else{
            maxTimeMillis = 500;
        }
        
        Instant instant = Instant.now();
        startTime = instant.toEpochMilli();
    }
    
    //returns the time elapsed since the timer started in milliseconds
    public long getElapsedTime(){
        Instant instant = Instant.now();
        return (instant.toEpochMilli() - startTime);
    }
    
    //returns true if the elapsed time is greater than or equal to the specified MAX time
    public boolean isTimeElapsed(){
        Instant instant = Instant.now();
        boolean elapsed = false;
        if((instant.toEpochMilli() - startTime) >= maxTimeMillis){
            elapsed = true;
        }
        
        return elapsed;
    }
    
}
