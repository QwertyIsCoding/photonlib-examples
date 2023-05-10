package org.aa8426.lib.logging;

import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PowerLog {
    static public void update(PowerDistribution m_pdp) {
        //PowerDistribution examplePD = new PowerDistribution();
        SmartDashboard.putNumber("Power/Voltage", m_pdp.getVoltage());

        //double temperatureCelsius = m_pdp.getTemperature();
        SmartDashboard.putNumber("Temperature (c)", m_pdp.getTemperature());

        //double totalCurrent = m_pdp.getTotalCurrent();
        SmartDashboard.putNumber("Total Current", m_pdp.getTotalCurrent());
    
        // Get the total power of all channels.
        // Power is the bus voltage multiplied by the current with the units Watts.
        //double totalPower = m_pdp.getTotalPower();
        SmartDashboard.putNumber("Total Power", m_pdp.getTotalPower());
    
        // Get the total energy of all channels.
        // Energy is the power summed over time with units Joules.        
        SmartDashboard.putNumber("Total Energy", m_pdp.getTotalEnergy());        

    
        for(int x=0;x<16;x++) {
            SmartDashboard.putNumber("Current Channel "+x, m_pdp.getCurrent(x));            
        }
    }
}
