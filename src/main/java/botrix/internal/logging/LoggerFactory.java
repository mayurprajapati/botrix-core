package botrix.internal.logging;

import java.time.LocalDate;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.MDC;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class LoggerFactory {
	private static LoggerContext context;
	private static FileAppender<ILoggingEvent> fileAppender;

	static {
		System.setProperty("org.jboss.logging.provider", "slf4j");

//		context = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
//
//		fileAppender = new FileAppender<>();
//		fileAppender.setContext(context);
//		fileAppender.setName("timestamp");
//		// set the file name
//
//		var date = LocalDate.now();
//		var time = LocalTime.now();
//		String logPath = "logs/%s/%s.log".formatted(date, time);
//		fileAppender.setFile(logPath);
//
//		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
//		encoder.setContext(context);
//		encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %level [%c{1.}.%M:%L] - [%thread] - %msg%n");
//		encoder.start();
//
//		fileAppender.setEncoder(encoder);
//		fileAppender.start();
//		MDC.put("logPath", logPath);
	}

	public static Logger getLogger(Class<?> klass) {
//		ch.qos.logback.classic.Logger l = context.getLogger(klass);
		return org.slf4j.LoggerFactory.getLogger(klass);
//		l.addAppender(fileAppender);
//		return l;
	}
}
