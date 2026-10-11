package unit_test;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Integration test requires live MySQL database. Unit tests run independently with Mockito.")
class UnitTestApplicationTests {

	@Test
	void contextLoads() {
	}

}
