package frc.robot.subsystems.drive;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.InvertedValue;
import frc.robot.Constants;
/** 左右两个 TalonFX 的开环示例。构造一次，运行时仅设置输出和读取状态。 */
public class DriveIOReal implements DriveIO {
  private final TalonFX left = new TalonFX(Constants.LEFT_LEADER);
  private final TalonFX right = new TalonFX(Constants.RIGHT_LEADER);
  private double leftRequest, rightRequest;
  public DriveIOReal() {
    // 左右镜像装配的常见配置，必须架空逐侧验证；具体车不一定适用。
    configure(left, true);
    configure(right, false);
  }
  private static void configure(TalonFX motor, boolean inverted) {
    var config = new TalonFXConfiguration();
    config.MotorOutput.Inverted = inverted ? InvertedValue.Clockwise_Positive
        : InvertedValue.CounterClockwise_Positive;
    config.CurrentLimits.StatorCurrentLimitEnable = true;
    config.CurrentLimits.StatorCurrentLimit = Constants.STATOR_LIMIT_AMPS;
    var result = motor.getConfigurator().apply(config);
    if (!result.isOK()) throw new IllegalStateException("TalonFX 配置失败: "+result);
  }
  @Override public void setOutputs(double l, double r) {
    leftRequest=l; rightRequest=r;
    // 只控制左ID3和右ID1，不创建或输出其他机构。
    left.set(l); right.set(r);
  }
  @Override public void updateInputs(Inputs inputs) {
    inputs.leftOutput=leftRequest; inputs.rightOutput=rightRequest;
    inputs.leftMotorRps=left.getVelocity().getValueAsDouble();
    inputs.rightMotorRps=right.getVelocity().getValueAsDouble();
  }
}
