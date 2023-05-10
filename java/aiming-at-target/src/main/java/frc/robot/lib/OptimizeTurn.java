package org.aa8426.lib;

public class OptimizeTurn {

    static public double calcProportionalPower(double val, double targetVal, double tolerance, double maxVal, 
        double minOutput, double maxOutput) {
        
        double realToTargetDiff = val - targetVal;

        if (Math.abs(realToTargetDiff) < tolerance) {
            //this.operatorInterface.doDriving(0, 0, 0, false, 3, 0); // stop hopefully
            return 0.0;
        }
        
        if (realToTargetDiff > maxVal) {
            realToTargetDiff = maxVal;
        }
        if (realToTargetDiff < (-maxVal)) {
            realToTargetDiff = -maxVal;
        }

        double power = maxOutput * (Math.abs(realToTargetDiff) / maxVal);
        if (power < minOutput) {
            power = minOutput;
        }
        if (realToTargetDiff > 0) {
            return power;
        } else {
            return -power;
        }               
    }
    
    public static void _main(String[] args) {
        for(double pitch=-25;pitch <= 25;pitch=pitch+5) {
            System.out.println(pitch+":"+OptimizeTurn.calcProportionalPower(pitch, 0, 2, 20, 0.1, 0.25));
        }        
    }

    static public double shortestAngle(double currentAngle, double targetAngle) {
        double angle_diff = (targetAngle - currentAngle) % 360;
        if (angle_diff > 180) {
            angle_diff -= 360;
        }
        if (angle_diff < -180) {
          angle_diff  += 360;
        }
        return angle_diff;
    }

    static public double shortestAngleRads(double currentAngle, double targetAngle) {
        double angle_diff = (targetAngle - currentAngle) % (Math.PI*2);
        if (angle_diff > Math.PI) {
            angle_diff -= Math.PI*2;
        }
        if (angle_diff < -(Math.PI)) {
          angle_diff  += Math.PI*2;
        }
        return angle_diff;
    }
  
    /* Proportional within a range */
    static public double getPowerForTurnTo(double currentAngle, double targetAngle, double minPower, double maxPower, double angleTolerance) {
        double angleDiff = shortestAngle(currentAngle, targetAngle);
        if (Math.abs(angleDiff) < angleTolerance) {   
            return 0.0;
        }
        //double range = maxPower - minPower;
        double absPower = maxPower * (Math.abs(angleDiff) / 180);
        if (absPower < minPower) {
            absPower = minPower;
        }
        if (angleDiff > 0) {
            return -absPower;
        } else {
            return absPower;
        }
    }    

    static public double getPowerForTurnToByRads(double currentAngle, double targetAngle, double minPower, double maxPower) {
        //System.out.print(", ?"+Math.toDegrees(currentAngle));
        return getPowerForTurnTo(Math.toDegrees(currentAngle), Math.toDegrees(targetAngle), minPower, maxPower, 2);
    }

    /** This would be better covered by a test, but not sure the best way to write it just yet. */
    static private String optimizedTurn(double currentAngle, double targetAngle) {                  
        double angleDiff = shortestAngle(currentAngle, targetAngle);
        //double power = getPowerForTurnTo(currentAngle, targetAngle, 0.15, 0.75);
        double power = getPowerForTurnTo(currentAngle, targetAngle, 0.1, 1, 2);
        double powerByRads = getPowerForTurnToByRads(Math.toRadians(currentAngle), Math.toRadians(targetAngle), 0.1, 1);
        String fastest;
        if (angleDiff > 2) {
          fastest = "LEFT";
        } else if (angleDiff < -2) {
          fastest = "RIGHT";
        } else {
          fastest = "*";
        }
        System.out.print(String.format(", currentAngle: %2.2f, desiredAngle: %2.2f, angleDiff: %2.2f, fastest: %s, power:%2.2f, powerByRads:%2.2f", 
          currentAngle, targetAngle, angleDiff, fastest, power, powerByRads));
        return fastest;
      }

      public static void __main(String[] args) {
        for(int currentAngle=0;currentAngle<=360;currentAngle=currentAngle+45) {        
          for(int desiredAngle=0;desiredAngle<=360;desiredAngle=desiredAngle+90) {
            printMoveResult(currentAngle, desiredAngle);
          }  
        }         
      }

    private static void printMoveResult(int x, int y) {
        System.out.print(String.format("angle:%3d", x));
        optimizedTurn(x, y);
        System.out.println();
    }
  
}
