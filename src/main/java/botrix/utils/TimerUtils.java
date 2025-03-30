package botrix.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

import botrix.commons.time.DateUtils;

public class TimerUtils {

	private TimerUtils() {
	}

	/**
	 * Creates new instance of {@link Timer}
	 * 
	 * @return {@link Timer}
	 */
	public static Timer newTimer() {
		return new Timer();
	}

	/**
	 * Schedules the specified task for execution at the specified time. If the time
	 * is in the past, the task is scheduled for immediate execution.
	 * 
	 * @param code task to be scheduled
	 * @param time at which task will be executed
	 * @return {@link Timer}
	 * @see Timer#schedule(TimerTask, java.util.Date)
	 */
	public static Timer at(Runnable code, LocalDateTime time) {
		Timer t = newTimer();
		t.schedule(newTimertask(code), DateUtils.localDateTimeToDate(time));
		return t;
	}

	/**
	 * Schedules the specified task for execution at the specified time and ends at
	 * specified end time. If end is < current time then the task is never scheduled
	 * 
	 * @param code  task to be scheduled
	 * @param start at which task will be executed
	 * @param end   at which task will be terminated
	 * @return {@code true} if task is successfully scheduled else false
	 * @see Timer#schedule(TimerTask, java.util.Date)
	 */
	public static boolean within(Runnable code, LocalDateTime start, LocalDateTime end) {
		if (end.isBefore(LocalDateTime.now()))
			return false;

		Timer t = TimerUtils.at(() -> {
			code.run();
		}, start);

		TimerUtils.at(() -> {
			t.cancel();
		}, end);

		return true;
	}

	/**
	 * Schedules the specified task for execution after the specified delay.
	 * 
	 * @param code  task to be scheduled
	 * @param delay run task after delay
	 * @return {@link Timer}
	 * @see Timer#schedule(TimerTask, long)
	 */
	public static Timer delayed(Runnable code, Duration delay) {
		Timer t = newTimer();
		t.schedule(newTimertask(code), delay.toMillis());
		return t;
	}

	/**
	 * Schedules the specified task for repeated <i>fixed-delay execution</i>,
	 * beginning after the specified delay. Subsequent executions take place at
	 * approximately regular intervals separated by the specified period.
	 * 
	 * @param code   task to be scheduled
	 * @param delay  run after delay
	 * @param period run periodically after {@code period}
	 * @return {@link Timer}
	 * @see Timer#schedule(TimerTask, long, long)
	 */
	public static Timer repeated(Runnable code, Duration delay, Duration period) {
		Timer t = newTimer();
		t.schedule(newTimertask(code), delay.toMillis(), period.toMillis());
		return t;
	}

	/**
	 * Schedules the specified task for repeated <i>fixed-delay execution</i>,
	 * beginning at the specified time. Subsequent executions take place at
	 * approximately regular intervals, separated by the specified period.
	 * 
	 * @param code   task to be scheduled
	 * @param time   time at which task will be executed
	 * @param period run periodically after {@code period}
	 * @return {@link Timer}
	 * @see Timer#schedule(TimerTask, java.util.Date, long)
	 */
	public static Timer repeated(Runnable code, LocalDateTime time, Duration period) {
		Timer t = newTimer();
		t.schedule(newTimertask(code), DateUtils.localDateTimeToDate(time), period.toMillis());
		return t;
	}

	/**
	 * Schedules the specified task for repeated <i>fixed-rate execution</i>,
	 * beginning after the specified delay. Subsequent executions take place at
	 * approximately regular intervals, separated by the specified period.
	 * 
	 * @param code   task to be scheduled
	 * @param delay  run after delay
	 * @param period run periodically after {@code period}
	 * @return {@link Timer}
	 * @see Timer#scheduleAtFixedRate(TimerTask, long, long)
	 */
	public static Timer repeatedFixedRate(Runnable code, Duration delay, Duration period) {
		Timer t = newTimer();
		t.scheduleAtFixedRate(newTimertask(code), delay.toMillis(), period.toMillis());
		return t;
	}

	/**
	 * Schedules the specified task for repeated <i>fixed-rate execution</i>,
	 * beginning after the specified delay. Subsequent executions take place at
	 * approximately regular intervals, separated by the specified period.
	 * 
	 * @param code   task to be scheduled
	 * @param time   time at which task will be executed
	 * @param period run periodically after {@code period}
	 * @return {@link Timer}
	 * @see Timer#scheduleAtFixedRate(TimerTask, java.util.Date, long)
	 */
	public static Timer repeatedFixedRate(Runnable code, LocalDateTime time, Duration period) {
		Timer t = newTimer();
		t.scheduleAtFixedRate(newTimertask(code), DateUtils.localDateTimeToDate(time), period.toMillis());
		return t;
	}

	/**
	 * Creates new instance of {@link TimerTask} which has {@code code} in it
	 * 
	 * @param code {@link Runnable} instance to wrap
	 * @return {@link TimerTask}
	 */
	public static TimerTask newTimertask(Runnable code) {
		return new TimerTask() {
			@Override
			public void run() {
				code.run();
			}
		};
	}
}
