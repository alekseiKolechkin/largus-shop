package ru.largusshop.internal_orders;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.largusshop.internal_orders.service.ExcelService;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InternalOrdersApplicationTests {
	@Autowired
	ExcelService excelService;

	@Test
	public void contextLoads() throws IOException {
//		excelService.processExcel();
		Integer cost = 1/2;
		System.out.println(cost);
	}

}
