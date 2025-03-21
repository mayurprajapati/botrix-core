package botrix.internal.playwright.impl;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.playwright.Locator.SelectOptionOptions;
import com.microsoft.playwright.options.SelectOption;

import botrix.internal.playwright.Fulfillable;

public class SelectOptions implements Fulfillable<List<String>> {
	private Locator locator;
	private List<SelectOption> selectOptions = new ArrayList<>();
	private SelectOptionOptions options = new SelectOptionOptions();

	public SelectOptions(Locator locator, List<SelectOption> selectOptions) {
		this.locator = locator;
		this.selectOptions = selectOptions;
	}

	public SelectOptions setForce(boolean force) {
		options.setForce(force);
		return this;
	}

	public SelectOptions setNoWaitAfter(boolean noWaitAfter) {
		options.setNoWaitAfter(noWaitAfter);
		return this;
	}

	public SelectOptions setTimeout(double timeout) {
		this.setTimeout(timeout);
		return this;
	}

	@Override
	public List<String> fulfill() {
		return locator.getLocator().selectOption(selectOptions.toArray(new SelectOption[selectOptions.size()]),
				options);
	}

}
