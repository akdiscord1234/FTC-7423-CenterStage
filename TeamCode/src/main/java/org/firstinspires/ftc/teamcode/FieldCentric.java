package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
@TeleOp
public class FieldCentric extends LinearOpMode {

    private static final boolean USE_WEBCAM = false;  // true for webcam, false for phone camera
    DcMotor fl, fr, bl, br;
    private AprilTagProcessor aprilTag;
    DistanceSensor dl,dr;
    private VisionPortal visionPortal;
    public double motorPowerMultiplier = 1;
    private static final int maxDistance = 20;
    @Override
    public void runOpMode(){
        fl = hardwareMap.dcMotor.get("fl");
        bl = hardwareMap.dcMotor.get("bl");
        fr = hardwareMap.dcMotor.get("fr");
        br = hardwareMap.dcMotor.get("br");

        fr.setDirection(DcMotorSimple.Direction.REVERSE);
        br.setDirection(DcMotorSimple.Direction.REVERSE);

        dl = hardwareMap.get(DistanceSensor.class,"dl");
        dr = hardwareMap.get(DistanceSensor.class,"dr");


        initAprilTag();

        BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        imu.initialize(parameters);

        waitForStart();
        while (opModeIsActive()){
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            for (AprilTagDetection detection : currentDetections) {
                if(detection.metadata!=null){
                    telemetry.addData("aprilTag","detected");
                    telemetry.addData("id",detection.id);
                    if ((dr.getDistance(DistanceUnit.INCH) < maxDistance)||((detection.ftcPose.y < maxDistance) && (detection.id == 1 || detection.id == 2 || detection.id == 3 || detection.id == 4 || detection.id == 5 || detection.id == 6))) {
                        motorPowerMultiplier = 0.2;
                    } else {
                        motorPowerMultiplier = 1;
                    }
                }
            }
            if((dr.getDistance(DistanceUnit.INCH) < maxDistance)){
                motorPowerMultiplier = 0.2;
            }
            else{
                motorPowerMultiplier = 1;
            }
            telemetry.addData("distance",dr.getDistance(DistanceUnit.INCH));
            telemetry.addData("motorPower",motorPowerMultiplier);
            double y = -gamepad1.left_stick_y; // Remember, this is reversed!
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;
            telemetry.addData("x",x);
            telemetry.addData("y",y);
            double botHeading = -imu.getAngularOrientation().firstAngle;
            double rotX = x * Math.cos(botHeading) - y * Math.sin(botHeading);
            double rotY = x * Math.sin(botHeading) + y * Math.cos(botHeading);


            if(gamepad1.left_stick_y>0){
                motorPowerMultiplier = 0.7;
            }
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double flp = -(rotY + rotX + rx) / denominator;
            double blp = -(rotY - rotX + rx) / denominator;
            double frp = -(rotY - rotX - rx) / denominator;
            double brp = -(rotY + rotX - rx) / denominator;

            flp = flp * (1 - gamepad1.right_trigger);
            blp = blp * (1 - gamepad1.right_trigger);
            frp = frp * (1 - gamepad1.right_trigger);
            brp = brp * (1 - gamepad1.right_trigger);
            flp = flp * (1 + 2*gamepad1.left_trigger);
            blp = blp * (1 + 2*gamepad1.left_trigger);
            frp = frp * (1 + 2*gamepad1.left_trigger);
            brp = brp * (1 + 2*gamepad1.left_trigger);

            fl.setPower(motorPowerMultiplier*0.49*flp);
            bl.setPower(motorPowerMultiplier*0.49*blp);
            fr.setPower(motorPowerMultiplier*0.49*frp);
            br.setPower(motorPowerMultiplier*0.49*brp);

            telemetry.update();
        }
    }
    private void initAprilTag(){
        aprilTag = AprilTagProcessor.easyCreateWithDefaults();

        // Create the vision portal the easy way.
        if (USE_WEBCAM) {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                    hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTag);
        } else {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                    BuiltinCameraDirection.BACK, aprilTag);
        }
    }
}
