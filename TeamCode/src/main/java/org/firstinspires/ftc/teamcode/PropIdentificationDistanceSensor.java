package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "Prop Identification with Distance Sensor", group = "none")
public class PropIdentificationDistanceSensor
        extends OpMode {
    DistanceSensor dl;
    DistanceSensor dr;
    
    int PROP_DISTANCE = 20;

    @Override
    public void init() {
        dl = hardwareMap.get(DistanceSensor.class, "dl");
        dr = hardwareMap.get(DistanceSensor.class, "dr");
    }
    
    //Returns the location of the prop based on the distance sensor values
    //1 is the prop in the left location, 2 is the prop in the middle location, and 3 is the prop in the right location
    public int detectProp(boolean telemetryOn) {
        if(telemetryOn) {
            telemetry.addData("dl", dl.getDistance(DistanceUnit.INCH));
            telemetry.addData("dr", dr.getDistance(DistanceUnit.INCH));
        }

        if(dl.getDistance(DistanceUnit.INCH) < PROP_DISTANCE) {
            if(telemetryOn) {
                telemetry.addData("Prop Location: ", 3);
                telemetry.update();
            }
            return 3;
        }
        else if(dr.getDistance(DistanceUnit.INCH) < PROP_DISTANCE) {
            if(telemetryOn) {
                telemetry.addData("Prop Location: ", 1);
                telemetry.update();
            }
            return 1;
        }
        else {
            if(telemetryOn) {
                telemetry.addData("Prop Location: ", 2);
                telemetry.update();
            }
            return 2;
        }
    }
    
    @Override
    public void loop() {
    }
}
