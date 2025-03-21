package botrix.internal.playwright.impl;

import com.microsoft.playwright.Mouse;

public class MouseWrapper {
	private com.microsoft.playwright.Mouse mouse;

	public MouseWrapper(Mouse mouse) {
		this.mouse = mouse;
	}
	
	public void move() {
		mouse.move(0, 0);
	}
}
