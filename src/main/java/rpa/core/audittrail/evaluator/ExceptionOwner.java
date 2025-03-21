package rpa.core.audittrail.evaluator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionOwner {
	private String owner;

	public static class Defaults {
		public static final ExceptionOwner USER = new ExceptionOwner("User");
		public static final ExceptionOwner Bishop = new ExceptionOwner("Bishop");

		private Defaults() {
		}
	}
}
