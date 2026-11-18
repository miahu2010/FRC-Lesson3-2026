package frc.robot;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.*;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.subsystems.drive.*;
/** 第三课：在 Disabled 时选择实验，再在模拟 GUI 点击 Autonomous。 */
public class RobotContainer {
 private final DriveSubsystem drive;
 private final XboxController driver=new XboxController(Constants.DRIVER_PORT);
 private final SendableChooser<Command> chooser=new SendableChooser<>();
 public RobotContainer(){
  if(RobotBase.isReal()&&!Constants.ALLOW_REAL_HARDWARE) throw new IllegalStateException("实车硬件尚未放行");
  drive=new DriveSubsystem(RobotBase.isReal()?new DriveIOReal():new DriveIOSim());
  // Teleop, left stick Y drives and right stick X turns.
  drive.setDefaultCommand(drive.runEnd(()->{
   if(DriverStation.isTeleopEnabled() && driver.isConnected() && driver.getLeftBumperButton()){
    double forward=MathUtil.applyDeadband(-driver.getLeftY(),Constants.DEADBAND);
    double turn=MathUtil.applyDeadband(driver.getRightX(),Constants.DEADBAND);
    drive.arcade(forward,turn);
   }else{drive.stop();}
  },drive::stop));
  chooser.setDefaultOption("0 Stop",Commands.none());
  chooser.addOption("1 Open loop 0.15",drive.runEnd(()->drive.setOutputs(0.15,0.15),drive::stop).withTimeout(6));
  chooser.addOption("2 Arcade forward and turn",drive.runEnd(()->drive.arcade(0.5,0.2),drive::stop).withTimeout(6));
  if(RobotBase.isSimulation()) chooser.addOption("3 Closed loop 12 motor RPS",Commands.runOnce(drive::resetPID,drive).andThen(drive.runEnd(()->drive.velocity(12,12),drive::stop).withTimeout(6)));
  chooser.addOption("4 Left only 0.06",drive.runEnd(()->drive.setOutputs(0.06,0),drive::stop).withTimeout(1));
  chooser.addOption("5 Right only 0.06",drive.runEnd(()->drive.setOutputs(0,0.06),drive::stop).withTimeout(1));
  SmartDashboard.putData("AutoChooser",chooser);
 }
 public Command getAutonomousCommand(){return chooser.getSelected();}
 public void stopAll(){drive.stop();}
}
