package rpa.core.entities;

import rpa.core.driver.G;
import rpa.core.driver.SystemProperties;

public class Reporting {

	public static void sendExtractAudit(String status, String message) {
		StringBuilder auditMessage = new StringBuilder();
		if (SystemProperties.COMPLETED.equals(status)) {
			auditMessage.append(String.format("Successfully extracted %s: %s from %s for Project %s.",
					G.executionMetrics.getObject(), G.executionMetrics.getObjectNumber(),
					G.executionMetrics.getCurrentSystem(), G.executionMetrics.getBotInstall().getProject()));
		} else {
			auditMessage.append(String.format("Successfully extracted %s: %s from %s for Project %s.",
					G.executionMetrics.getObject(), G.executionMetrics.getObjectNumber(),
					G.executionMetrics.getCurrentSystem(), G.executionMetrics.getBotInstall().getProject()));
		}

	}

}
