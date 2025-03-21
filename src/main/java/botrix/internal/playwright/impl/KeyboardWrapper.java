package botrix.internal.playwright.impl;

import java.time.Duration;

import com.microsoft.playwright.Keyboard;

import botrix.internal.playwright.Fulfillable;

public class KeyboardWrapper {
	private Keyboard keyboard;

	public KeyboardWrapper(Keyboard keyboard) {
		this.keyboard = keyboard;
	}

	public void down(String key) {
		keyboard.down(key);
	}

	public void up(String key) {
		keyboard.up(key);
	}

	public void insertText(String text) {
		keyboard.insertText(text);
	}

	public void press(String key) {
		keyboard.press(key);
	}

	public void press(String key, Duration delay) {
		pressOptions(key).delay(delay).fulfill();
	}

	public PressOptions pressOptions(String key) {
		return new PressOptions(keyboard, key);
	}

	public void type(String key) {
		keyboard.type(key);
	}

	public void type(String key, Duration delay) {
		typeOptions(key).delay(delay).fulfill();
	}

	public TypeOptions typeOptions(String key) {
		return new TypeOptions(keyboard, key);
	}

	public static class PressOptions implements Fulfillable<Void> {
		private Keyboard keyboard;
		private String key;
		private com.microsoft.playwright.Keyboard.PressOptions pressOptions = new com.microsoft.playwright.Keyboard.PressOptions();

		public PressOptions(Keyboard keyboard, String key) {
			this.keyboard = keyboard;
			this.key = key;
		}

		public PressOptions delay(Duration delay) {
			pressOptions.setDelay(delay.toMillis());
			return this;
		}

		@Override
		public Void fulfill() {
			keyboard.press(key, pressOptions);
			return null;
		}
	}

	public static class TypeOptions implements Fulfillable<Void> {
		private Keyboard keyboard;
		private String key;
		private com.microsoft.playwright.Keyboard.TypeOptions typeOptions = new com.microsoft.playwright.Keyboard.TypeOptions();

		public TypeOptions(Keyboard keyboard, String key) {
			this.keyboard = keyboard;
			this.key = key;
		}

		public TypeOptions delay(Duration delay) {
			typeOptions.setDelay(delay.toMillis());
			return this;
		}

		@Override
		public Void fulfill() {
			keyboard.type(key, typeOptions);
			return null;
		}
	}
}
