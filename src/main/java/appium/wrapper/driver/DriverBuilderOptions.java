package appium.wrapper.driver;

import appium.wrapper.utils.NetUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class DriverBuilderOptions {
	@Builder.Default
	private String debugHost = "127.0.0.1";

	@Builder.Default
	private int debugPort = NetUtils.findFreePort();
}
