package io.github.brenoepics.core.types;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HandleResult {
		private boolean delete;
		private boolean handle;
		private String error = "";

		public HandleResult(boolean delete, boolean handle) {
				this.delete = delete;
				this.handle = handle;
		}
}
