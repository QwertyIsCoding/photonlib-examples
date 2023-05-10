package org.aa8426.robot2023.commands;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.aa8426.robot2023.OperatorInterface;
import org.aa8426.robot2023.RobotContainer;

import edu.wpi.first.wpilibj2.command.Command;


public class Autons {
    
    private Map<String, Supplier<Command>> namesAndCommands = new TreeMap<>();        
    private CmdFactory cf;
        
    public Autons(RobotContainer robotContainer, OperatorInterface operatorInterface) {
        this.cf = new CmdFactory(robotContainer, operatorInterface);        
        buildNamesAndCommands();
    }    

    /** Drives "forward" (away from driver), climbing on top of the charging station, and then balances */
    public Command getDriveFwdUntilBalanced() {        
        return cf.driveUntilPitchGreaterThan(0.75, 20) // up the ramp            
            .andThen(cf.driveTime(0.4, 1)) // we should be at the bottom of the ramp, lets get up quicker
            .andThen(cf.balance(-5)); // now lets balance
    }

    /** Drives "backward" (towards driver), climbing on top of the charging station, and then balances */
    public Command getDriveBckwdUntilBalanced() {        
        return cf.driveUntilPitchLessThan(-0.75, -25) // up the ramp (backwards)
            .andThen(cf.driveTime(-0.4, 1)) // we should be at the bottom of the ramp, lets get up quicker
            .andThen(cf.balance(-5)); // now lets balance
    }

    /** Drives up and over the driving station (and clears it) */
    public Command getDriveOver() {
        return cf.retractArm()
            .andThen(cf.driveUntilPitchGreaterThan(0.75, 20)) // up the ramp
            .andThen(cf.stopDriving(0.2))            
            .andThen(cf.driveUntilPitchLessThan(0.45, 4)) // cross the ramp
            .andThen(cf.stopDriving(0.2))
            .andThen(cf.driveUntilPitchGreaterThan(0.45, -7)) // down the ramp
            .andThen(cf.stopDriving(0.2)); // make sure we're over the other side // stop the motors for a moment
             // drive until we're basically flat
            //.andThen(cf.drive(0.45, 0, 0)).withTimeout(1); // drive a little to clear the platform
    }
    
    // Actual autons.
    /*
     * Just go over and balance.
     */
    public Command getBalanceOverCmd() {
        return getDriveOver()
              .andThen(getDriveBckwdUntilBalanced());
    }
    
    // 
    public Command getPlaceConeCmds() {
        return cf.grabCone()
            .andThen(cf.extendArm(2))
            .andThen(cf.ejectCone())
            .andThen(cf.retractArm());
            //.andThen(cf.stopArm());
    }

    public Command getPlaceCubeCmds() {
        return cf.extendArm(2)
        .andThen(cf.ejectCube())        
        .andThen(cf.retractArm());
        //.andThen(cf.stopArm());
    }

    public Command autonCmd(Command ... cmds) {        
        return cf.getSetAngleAdjustment(180).andThen(cmds);
    }
    
    // true autons

    private Map<String, Supplier<Command>> buildNamesAndCommands() {

        namesAndCommands = new LinkedHashMap<>();
                    
        //namesAndCommands.put("placeCone", () -> {return this.autonCmd(getPlaceConeCmds());} );
        //namesAndCommands.put("placeCube", () -> {return autonCmd(getPlaceConeCmds());});

        namesAndCommands.put("balanceFwdCmd", () -> {return autonCmd(getDriveFwdUntilBalanced());});        
        namesAndCommands.put("balanceOverCmd", () -> {return autonCmd(getBalanceOverCmd());});
        namesAndCommands.put("driveOver", () -> {return autonCmd(getDriveOver());});

        namesAndCommands.put("ConeAndBalance", () -> {return autonCmd(getPlaceConeCmds(), getDriveFwdUntilBalanced());});
        namesAndCommands.put("CubeAndBalance", () -> {return autonCmd(getPlaceCubeCmds(), getDriveFwdUntilBalanced());});
        namesAndCommands.put("ConeOverAndBalance", () -> {return autonCmd(getPlaceConeCmds(), getBalanceOverCmd());});
        namesAndCommands.put("CubeOverAndBalance", () -> {return autonCmd(getPlaceCubeCmds(), getBalanceOverCmd());});

        namesAndCommands.put("xOut", () -> {return autonCmd(cf.xOut());});
                
        //namesAndCommands.put("Num1_Cone", () -> {return autonCmd(getPlaceConeCmds(), cf.followPath("ShortDriveout"));});
        //namesAndCommands.put("Num1_Cube", () -> {return autonCmd(getPlaceConeCmds(), cf.followPath("ShortDriveout"));});
        namesAndCommands.put("Num2_Cone", () -> {return autonCmd(getPlaceConeCmds(), cf.followPath("MiddleDriveout"));});
        namesAndCommands.put("Num2_Cube", () -> {return autonCmd(getPlaceCubeCmds(), cf.followPath("MiddleDriveout"));});
        //namesAndCommands.put("Num3_Cone", () -> {return autonCmd(getPlaceConeCmds(), cf.followPath("No1Driveout"));});
        //namesAndCommands.put("Num3_Cube", () -> {return autonCmd(getPlaceConeCmds(), cf.followPath("No1Driveout"));});
                
        return namesAndCommands;
    }

    public Command getCommand(String name) {
        return namesAndCommands.get(name).get();
    }

    public Map<String, Supplier<Command>> getNamesAndCommands() {
        return namesAndCommands;
    }    

}

