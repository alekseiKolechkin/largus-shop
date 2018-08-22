package ru.largusshop.internal_orders;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InternalOrdersApplicationTests {

	@Test
	public void contextLoads() {
		Integer cost = 1/2;
		System.out.println(cost);
	}

}
