package org.aa8426.robot2023.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class TimedCommand extends CommandBase {

    public Timer timer = null;
    public double howLong;
    public Runnable startCode;
    public Runnable duringCode;
    public Runnable endCode;    

    static public TimedCommand getCommand(double howLong, Runnable duringCode) {
        return new TimedCommand(howLong, duringCode);
    }

    public TimedCommand(double howLong, Runnable duringCode) {
        this(howLong, null, duringCode, null);
    }   

    public TimedCommand(double howLong, Runnable startCode, Runnable duringCode, Runnable endCode) {
        this.howLong = howLong;
        this.startCode = startCode;        
        this.duringCode = duringCode;
        this.endCode = endCode;
    }   

    @Override
    public void initialize() {            
        super.initialize();      
        this.timer = new Timer();
        timer.start(); 
        if (startCode != null) {
            startCode.run();            
        }
    }


    @Override
    public boolean isFinished() {        
        return timer.hasElapsed(howLong);        
    }

    @Override
    public void execute() {
        super.execute();
        if (duringCode != null) {
            duringCode.run();            
        }
    }

    @Override
    public void end(boolean interrupted) {    
        super.end(interrupted);
        if (endCode != null) {
            endCode.run();            
        }
    }
   
}