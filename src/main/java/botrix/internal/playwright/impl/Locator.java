package botrix.internal.playwright.impl;

import static java.lang.String.format;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

import com.microsoft.playwright.Locator.WaitForOptions;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

import botrix.internal.logging.LoggerFactory;
import lombok.Getter;

@Getter
public class Locator {
	private static final Logger logger = LoggerFactory.getLogger(Locator.class);

	private com.microsoft.playwright.Locator locator;
	private String name;

	public Locator(com.microsoft.playwright.Locator locator, String name) {
		this.locator = locator;
		this.name = name;

		Validate.isTrue(StringUtils.isNoneBlank(name), "Locator name is blank");
	}

	public Optional<String> getAttribute(String attribute) {
		try {
			String value = locator.getAttribute(attribute);
			return Optional.of(value);
		} catch (TimeoutError | NullPointerException e) {
			return Optional.empty();
		}
	}

	public Page getPage() {
		return new Page(locator.page());
	}

	public InputValueOptions inputValueOptions() {
		return new InputValueOptions(this);
	}

	public Optional<String> inputValue() {
		return inputValueOptions().fulfill();
	}

	public FrameLocator toFrameLocator() {
		/*
		 * If you have a Locator object pointing to an iframe it can be converted to
		 * FrameLocator using :scope CSS selector:
		 */
		com.microsoft.playwright.FrameLocator frameLocator = locator.frameLocator(":scope");
		return new FrameLocator(frameLocator, name);
	}

	public boolean isVisible() {
		return locator.isVisible();
	}

	public boolean isVisible(Duration timeout) {
		try {
			locator.waitFor(new WaitForOptions().setTimeout(timeout.toMillis()));
			return locator.isVisible();
		} catch (TimeoutError e) {
			return false;
		}
	}

	public void click() {
		try {
			locator.click();
//			LoggerFactory.getLogger(Class.forName(Thread.currentThread().getStackTrace()[2].getClassName())).info("Mayur");
			logger.info("Clicked on '{}'", name);
		} catch (Exception e) {
			String msg = format("Error clicking on '%s'", name);
			throw new RuntimeException(msg, e);
		}
	}

	public void clear() {
		locator.fill("");
	}

	public void fill(String value) {
		type(value);
	}

	public void type(String value) {
		typeOptions(value).fulfill();
	}

	public TypeOptions typeOptions(String value) {
		return new TypeOptions(value, this);
	}

	public void hover() {
		locator.hover();
	}

	public Locator first() {
		return new Locator(locator.first(), name);
	}

	public LocatorHoverOptions hoverOptions() {
		return new LocatorHoverOptions(this);
	}

	public void waitFor(Duration timeout) {
		new WaitForLocator(this).setTimeout(timeout).fulfill();
	}

	public void waitForInvisible(Duration timeout) {
		new WaitForLocator(this).setState(WaitForSelectorState.HIDDEN).setTimeout(timeout).fulfill();
	}

	public List<String> selectOptionValue(String value) {
		return locator.selectOption(value);
	}

	public List<String> selectOptionLabel(String label) {
		return locator.selectOption(label);
	}

	public List<String> selectOptionsLabel(List<String> labels) {
		return selectOptions(labels.stream().map(opt -> new SelectOption().setLabel(opt)).collect(Collectors.toList()));
	}

	public List<String> selectOptionsValue(List<String> values) {
		return selectOptions(values.stream().map(opt -> new SelectOption().setValue(opt)).collect(Collectors.toList()));
	}

	public List<String> selectOptionsIndex(List<Integer> indexes) {
		return selectOptions(
				indexes.stream().map(opt -> new SelectOption().setIndex(opt)).collect(Collectors.toList()));
	}

	public List<String> selectOptions(List<SelectOption> options) {
		return new SelectOptions(this, options).fulfill();
	}

	public SelectOptions selectOptionsWithOptions(List<SelectOption> options) {
		return new SelectOptions(this, options);
	}

	public <T> Optional<T> waitUntilNotNull(Function<Locator, T> function, Duration timeout) {
		return waitUntilNotNull(function, timeout, Duration.ofMillis(500));
	}

	public <T> Optional<T> waitUntilNotNull(Function<Locator, T> function, Duration timeout, Duration pollingTime) {
		while (!timeout.isNegative()) {
			timeout = timeout.minus(pollingTime);
			getPage().waitForTimeout(Duration.ofMillis(500));
			try {
				T value = function.apply(this);
				return Optional.of(value);
			} catch (TimeoutError | NullPointerException e) {
			}
		}

		return Optional.empty();
	}

	public String textContent() {
		return locator.textContent();
	}

	public Locator nth(int i) {
		return new Locator(locator.nth(i), name);
	}

	public void press(String string) {
		locator.press(string);
		logger.info("Pressed '{}' on '{}'", string, name);
	}
}
