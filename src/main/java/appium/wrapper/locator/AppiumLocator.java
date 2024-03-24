package appium.wrapper.locator;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumBy;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class AppiumLocator {
	private String locatorName;
	private String locatorValue;
	private LocatorType locatorType;

	private AppiumLocator(LocatorType type, String locatorName, String locatorValue) {
		this.locatorType = type;
		this.locatorName = locatorName;
		this.locatorValue = locatorValue;
	}

	public WebElement findOne(SearchContext context) {
		return context.findElement(get());
	}

	public By get() {
		switch (locatorType) {
		case XPATH: {
			return AppiumBy.xpath(locatorValue);
		}
		case ACCESSIBILITY_ID: {
			return AppiumBy.accessibilityId(locatorValue);
		}
		case ANDROID_DATA_MATCHER: {
			return AppiumBy.androidDataMatcher(locatorValue);
		}
		case ANDROID_UI_AUTOMATOR: {
			return AppiumBy.androidUIAutomator(locatorValue);
		}
		case ANDROID_VIEW_MATCHER: {
			return AppiumBy.androidViewMatcher(locatorValue);
		}
		case ANDROID_VIEW_TAG: {
			return AppiumBy.androidViewTag(locatorValue);
		}
		case CLASS_NAME: {
			return AppiumBy.className(locatorValue);
		}
		case CSS_SELECTOR: {
			return AppiumBy.cssSelector(locatorValue);
		}
		case CUSTOM: {
			return AppiumBy.custom(locatorValue);
		}
		case ID: {
			return AppiumBy.id(locatorValue);
		}
		case IMAGE: {
			return AppiumBy.image(locatorValue);
		}
		case IOS_CLASS_CHAIN: {
			return AppiumBy.iOSClassChain(locatorValue);
		}
		case IOS_NSPREDICATE_STRING: {
			return AppiumBy.iOSNsPredicateString(locatorValue);
		}
		case LINK_TEXT: {
			return AppiumBy.linkText(locatorValue);
		}
		case NAME: {
			return AppiumBy.name(locatorValue);
		}
		case PARTIAL_LINK_TEXT: {
			return AppiumBy.partialLinkText(locatorValue);
		}
		case TAG_NAME: {
			return AppiumBy.tagName(locatorValue);
		}
		default:
			throw new IllegalArgumentException("Unexpected locator type: " + locatorType);
		}
	}

	public static AppiumLocator byXpath(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.XPATH, locatorName, locatorValue);
	}

	public static AppiumLocator byAccessibilityId(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.ACCESSIBILITY_ID, locatorName, locatorValue);
	}

	public static AppiumLocator byAndroidDataMatcher(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.ANDROID_DATA_MATCHER, locatorName, locatorValue);
	}

	public static AppiumLocator byAndroidUiAutomator(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.ANDROID_UI_AUTOMATOR, locatorName, locatorValue);
	}

	public static AppiumLocator byAndroidViewMatcher(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.ANDROID_VIEW_MATCHER, locatorName, locatorValue);
	}

	public static AppiumLocator byAndroidViewTag(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.ANDROID_VIEW_TAG, locatorName, locatorValue);
	}

	public static AppiumLocator byClassName(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.CLASS_NAME, locatorName, locatorValue);
	}

	public static AppiumLocator byId(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.ID, locatorName, locatorValue);
	}

	public static AppiumLocator byName(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.NAME, locatorName, locatorValue);
	}

	public static AppiumLocator byCustom(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.CUSTOM, locatorName, locatorValue);
	}

	public static AppiumLocator byImage(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.IMAGE, locatorName, locatorValue);
	}

	public static AppiumLocator byiOSClassChain(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.IOS_CLASS_CHAIN, locatorName, locatorValue);
	}

	public static AppiumLocator byiOSNsPredicateString(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.IOS_NSPREDICATE_STRING, locatorName, locatorValue);
	}

	public static AppiumLocator byLinkText(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.LINK_TEXT, locatorName, locatorValue);
	}

	public static AppiumLocator byPartialLinkText(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.PARTIAL_LINK_TEXT, locatorName, locatorValue);
	}

	public static AppiumLocator byTagName(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.TAG_NAME, locatorName, locatorValue);
	}

	public static AppiumLocator byCssSelector(String locatorName, String locatorValue) {
		return new AppiumLocator(LocatorType.CSS_SELECTOR, locatorName, locatorValue);
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
