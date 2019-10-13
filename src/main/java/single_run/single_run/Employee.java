package single_run.single_run;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Employee {

	private String firstName;
	private String lastName;
	private int age;
	private String about;
	private List<String> interests;	
	
}
