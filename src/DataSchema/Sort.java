package DataSchema;

public class Sort {
		// list of attributes
		private String name;
		
		// Attribute constructor
		public Sort (String name) {
			this.name = name;
		}

		// getters and setters
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		
		// toString method in order to print the corresponding MCMT declaration
		@Override
		public String toString() {
			return ":smt(define--type " + name + ")\n";
		}
		
		
}
