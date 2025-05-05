package github.com.claudevan.capitalgains;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CapitalgainscalculatorApplicationTests {

	private final CapitalGainsCommands calculator = new CapitalGainsCommands(new com.fasterxml.jackson.databind.ObjectMapper());

	@Test
	void testCase1() {
		List<Operation> operations = new ArrayList<>();
		operations.add(new Operation("buy", 100, BigDecimal.valueOf(10.00)));
		operations.add(new Operation("sell", 50, BigDecimal.valueOf(15.00)));
		operations.add(new Operation("sell", 50, BigDecimal.valueOf(15.00)));

		List<TaxResult> results = calculator.calculateTaxes(operations);

		assertEquals(3, results.size());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP), results.get(0).getTax());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP), results.get(1).getTax());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP), results.get(2).getTax());
	}

	@Test
	void testCase2() {
		List<Operation> operations = new ArrayList<>();
		operations.add(new Operation("buy", 10000, BigDecimal.valueOf(10.00)));
		operations.add(new Operation("sell", 5000, BigDecimal.valueOf(20.00)));
		operations.add(new Operation("sell", 5000, BigDecimal.valueOf(5.00)));

		List<TaxResult> results = calculator.calculateTaxes(operations);

		assertEquals(3, results.size());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(0).getTax());
		assertEquals(BigDecimal.valueOf(10000.00).setScale(2), results.get(1).getTax());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(2).getTax());
	}

	@Test
	void testCase3() {
		List<Operation> operations = new ArrayList<>();
		operations.add(new Operation("buy", 10000, BigDecimal.valueOf(10.00)));
		operations.add(new Operation("sell", 5000, BigDecimal.valueOf(5.00)));
		operations.add(new Operation("sell", 3000, BigDecimal.valueOf(20.00)));

		List<TaxResult> results = calculator.calculateTaxes(operations);

		assertEquals(3, results.size());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(0).getTax());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(1).getTax());
		assertEquals(BigDecimal.valueOf(1000.00).setScale(2), results.get(2).getTax());
	}

	@Test
	void testCase4() {
		List<Operation> operations = new ArrayList<>();
		operations.add(new Operation("buy", 10000, BigDecimal.valueOf(10.00)));
		operations.add(new Operation("buy", 5000, BigDecimal.valueOf(25.00)));
		operations.add(new Operation("sell", 10000, BigDecimal.valueOf(15.00)));

		List<TaxResult> results = calculator.calculateTaxes(operations);

		assertEquals(3, results.size());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(0).getTax());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(1).getTax());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(2).getTax());
	}

	@Test
	void testCase5() {
		List<Operation> operations = new ArrayList<>();
		operations.add(new Operation("buy", 10000, BigDecimal.valueOf(10.00)));
		operations.add(new Operation("buy", 5000, BigDecimal.valueOf(25.00)));
		operations.add(new Operation("sell", 10000, BigDecimal.valueOf(15.00)));
		operations.add(new Operation("sell", 5000, BigDecimal.valueOf(25.00)));

		List<TaxResult> results = calculator.calculateTaxes(operations);

		assertEquals(4, results.size());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(0).getTax());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(1).getTax());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(2).getTax());
		assertEquals(BigDecimal.valueOf(10000.00).setScale(2), results.get(3).getTax());
	}

	@Test
	void testCase6() {
		List<Operation> operations = new ArrayList<>();
		operations.add(new Operation("buy", 10000, BigDecimal.valueOf(10.00)));
		operations.add(new Operation("sell", 5000, BigDecimal.valueOf(2.00)));
		operations.add(new Operation("sell", 2000, BigDecimal.valueOf(20.00)));
		operations.add(new Operation("sell", 2000, BigDecimal.valueOf(20.00)));
		operations.add(new Operation("sell", 1000, BigDecimal.valueOf(25.00)));

		List<TaxResult> results = calculator.calculateTaxes(operations);

		assertEquals(5, results.size());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(0).getTax());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(1).getTax());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(2).getTax());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(3).getTax());
		assertEquals(BigDecimal.valueOf(3000.00).setScale(2), results.get(4).getTax());
	}

	@Test
	void testCase7() {
		List<Operation> operations = new ArrayList<>();
		operations.add(new Operation("buy", 10000, BigDecimal.valueOf(10.00)));
		operations.add(new Operation("sell", 5000, BigDecimal.valueOf(2.00)));
		operations.add(new Operation("sell", 2000, BigDecimal.valueOf(20.00)));
		operations.add(new Operation("sell", 2000, BigDecimal.valueOf(20.00)));
		operations.add(new Operation("sell", 1000, BigDecimal.valueOf(25.00)));
		operations.add(new Operation("buy", 10000, BigDecimal.valueOf(20.00)));
		operations.add(new Operation("sell", 5000, BigDecimal.valueOf(15.00)));
		operations.add(new Operation("sell", 4350, BigDecimal.valueOf(30.00)));
		operations.add(new Operation("sell", 650, BigDecimal.valueOf(30.00)));

		List<TaxResult> results = calculator.calculateTaxes(operations);

		assertEquals(9, results.size());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(0).getTax());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(1).getTax());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(2).getTax());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(3).getTax());
		assertEquals(BigDecimal.valueOf(3000.00).setScale(2), results.get(4).getTax());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(5).getTax());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(6).getTax());
		assertEquals(BigDecimal.valueOf(3700.00).setScale(2), results.get(7).getTax());
		assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), results.get(8).getTax());
	}

}
