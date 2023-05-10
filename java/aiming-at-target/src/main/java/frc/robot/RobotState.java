package org.aa8426.robot2023;

/** 
 * Keeps track of states of the robot that have interactions/implications across subsystems.
 * 
 * For example, you don't want to rotate when the arm is extended, or extend when you are rotating)
 * 
 * Subsystem states that don't interact across 
 */
public class RobotState {
    
    @SuppressWarnings("unused")
    private RobotContainer robotContainer;
    
    @SuppressWarnings("unused")
    private GameMode gameMode = GameMode.DISABLED;

    public enum GameMode {
        TELEOP, AUTO, DISABLED
    }    

    public RobotState() {
        
    }

    public void setRobotContainer(RobotContainer robotContainer) {
        this.robotContainer = robotContainer;
    }

    public boolean robotAllowedToRotate() {
        return true;    
    }

    public boolean armAllowedToExtend() {
        return true;
    }
    

    
    
}
