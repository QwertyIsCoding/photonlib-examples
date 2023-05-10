package org.aa8426.robot2023;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import org.aa8426.robot2023.commands.Autons;

import edu.wpi.first.cscore.VideoSource;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;

public class OurShuffleboard {
    
    private static final String DEFAULT_AUTO_NAME = "Num2_Cone";

    SendableChooser<String> autoChooser = new SendableChooser<>();
        
    @SuppressWarnings("unused")
    private RobotState robotState;

    @SuppressWarnings("unused")
    private RobotContainer robotContainer;    
    
    ShuffleboardTab rubyTab;
    
    public OurShuffleboard(RobotState robotState, RobotContainer robotContainer, Autons autons, OperatorInterface operatorInterface) {        
        
        Map<String, Supplier<Command>> autonCommands = autons.getNamesAndCommands();
        rubyTab = Shuffleboard.getTab("Ruby");            

        this.robotState = robotState;
        this.robotContainer = robotContainer;        

        for(Entry<String, Supplier<Command>> e:autonCommands.entrySet()) {
            if (DEFAULT_AUTO_NAME.equals(e.getKey())) {
                autoChooser.setDefaultOption(e.getKey(), e.getKey());
            } else {
                autoChooser.addOption(e.getKey(), e.getKey());
            }
        }        
        rubyTab
            .add("Auton", autoChooser)            
            .withPosition(1, 0)
            .withSize(3, 1);

        rubyTab.addBoolean("Arm Check", () -> buttonIsPressed(operatorInterface.armPad)).withPosition(0, 0);
        rubyTab.addBoolean("Drive Check", () -> buttonIsPressed(operatorInterface.drivePad)).withPosition(0, 1);        
        
        rubyTab.add(VideoSource.enumerateSources()[0]).withSize(3, 3).withPosition(1, 2);
                        
        Shuffleboard.selectTab("Ruby");                
    }

    public String getAutonName() {
        String name = autoChooser.getSelected();
        if (name == null) {
            name = DEFAULT_AUTO_NAME;
        }
        return name;
    }

    public boolean buttonIsPressed(XboxController pad) {
        boolean isTrue = pad.getAButton() || pad.getBButton() || pad.getXButton() || pad.getYButton();        
        return isTrue;
    }

    
}
