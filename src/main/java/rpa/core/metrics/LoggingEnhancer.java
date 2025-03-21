//package rpa.core.metrics;
//
//import com.google.cloud.logging.LogEntry;
//
//import rpa.core.driver.G;
//
//public class LoggingEnhancer implements com.google.cloud.logging.LoggingEnhancer {
//
//	@Override
//	public void enhanceLogEntry(LogEntry.Builder logEntry) {
//		logEntry.addLabel("masterUuid", Thread.currentThread().getName());
//		if (G.executionMetrics != null) {
//			logEntry.addLabel("BishopAccount", G.executionMetrics.getBishopAccount());
//			logEntry.addLabel("flowId", G.executionMetrics.getFlowId());
//			logEntry.addLabel("objectNumber", G.executionMetrics.getObjectNumber());
//		}
//	}
//}
