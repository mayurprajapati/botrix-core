package appium.wrapper.locator;

import org.openqa.selenium.By;

import io.appium.java_client.AppiumBy;
import lombok.Getter;

@Getter
public class AppiumLocator {
	private String locatorName;
	private String locatorValue;
	private LocatorType locatorType;

	private AppiumLocator(LocatorType type, String locatorName, String locatorValue) {
		this.locatorType = type;
		this.locatorName = locatorName;
		this.locatorValue = locatorValue;
	}

	public By get() {
		switch (locatorType) {
		case XPATH: {
			return AppiumBy.xpath(locatorName);
		}
		case ACCESSIBILITY_ID: {
			return AppiumBy.accessibilityId(locatorName);
		}
		case ANDROID_DATA_MATCHER: {
			return AppiumBy.androidDataMatcher(locatorName);
		}
		case ANDROID_UI_AUTOMATOR: {
			return AppiumBy.androidUIAutomator(locatorName);
		}
		case ANDROID_VIEW_MATCHER: {
			return AppiumBy.androidViewMatcher(locatorName);
		}
		case ANDROID_VIEW_TAG: {
			return AppiumBy.androidViewTag(locatorName);
		}
		case CLASS_NAME: {
			return AppiumBy.className(locatorName);
		}
		case CSS_SELECTOR: {
			return AppiumBy.cssSelector(locatorName);
		}
		case CUSTOM: {
			return AppiumBy.custom(locatorName);
		}
		case ID: {
			return AppiumBy.id(locatorName);
		}
		case IMAGE: {
			return AppiumBy.image(locatorName);
		}
		case IOS_CLASS_CHAIN: {
			return AppiumBy.iOSClassChain(locatorName);
		}
		case IOS_NSPREDICATE_STRING: {
			return AppiumBy.iOSNsPredicateString(locatorName);
		}
		case LINK_TEXT: {
			return AppiumBy.linkText(locatorName);
		}
		case NAME: {
			return AppiumBy.name(locatorName);
		}
		case PARTIAL_LINK_TEXT: {
			return AppiumBy.partialLinkText(locatorName);
		}
		case TAG_NAME: {
			return AppiumBy.tagName(locatorName);
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + locatorType);
		}
	}

	public static AppiumLocator byXpath(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.XPATH, locatorName, locatorValue);
	}

	public static enum LocatorType {
		// @formatter:off
		XPATH,
		ACCESSIBILITY_ID,
		ANDROID_DATA_MATCHER,
		ANDROID_UI_AUTOMATOR,
		ANDROID_VIEW_MATCHER,
		ANDROID_VIEW_TAG,
		CLASS_NAME,
		ID,
		NAME,
		CUSTOM,
		IMAGE,
		IOS_CLASS_CHAIN,
		IOS_NSPREDICATE_STRING,
		LINK_TEXT,
		PARTIAL_LINK_TEXT,
		TAG_NAME,
		CSS_SELECTOR,
		;
		// @formatter:on
	}
}
