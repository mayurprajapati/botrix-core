package botrix.internal.playwright.impl;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Dialog {
	private com.microsoft.playwright.Dialog dialog;

	public void accept() {
		dialog.accept();
	}

	public void accept(String textToEnterInPrompt) {
		dialog.accept(textToEnterInPrompt);
	}

	public String defaultValue() {
		return dialog.defaultValue();
	}

	public void dismiss() {
		dialog.dismiss();
	}

	public String message() {
		return dialog.message();
	}

	public DialogType type() {
		return DialogType.valueOf(dialog.type().toUpperCase());
	}

	public static enum DialogType {
		ALERT, //
		BEFOREUNLOAD, //
		CONFIRM, //
		PROMPT;
	}
}
