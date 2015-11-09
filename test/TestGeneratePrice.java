import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import util.ExeReport;
import util.Order;
import exchange.OrderExecutor;
/**
 * Test class for functions of OrderExecutor.
 * @author pennlio
 *
 */
public class TestGeneratePrice {

	OrderExecutor exe = OrderExecutor.getInstance(); //singleton
	
	@SuppressWarnings("deprecation")
	@Test
	public void testFullFillReport(){
		List<ExeReport> reports = new ArrayList<ExeReport>();
		Order order = new Order();
		order.setClOrdID(1001);
		order.setOrderType("1"); // market order
		order.setOrderQty(20);
		reports = exe.generateExeReport(order);
		Assert.assertFalse(reports.isEmpty());
		Assert.assertEquals(reports.size(), 1); 
		int cumQty = 0;
		int orderId = order.getClOrdID();
		int lastExeCounter = 0;
		for (ExeReport rpt: reports){
			Assert.assertTrue(rpt.getExecID() > lastExeCounter);
			lastExeCounter = rpt.getExecID();
			cumQty += rpt.getLastShares();
			Assert.assertTrue(rpt.getLastShares() <= order.getOrderQty());
			Assert.assertTrue(rpt.getLastShares() > 0);
			Assert.assertEquals(orderId, rpt.getOrderID()); // all for same order
			Assert.assertEquals(rpt.getOrdStatus(), 2);
			Assert.assertEquals(rpt.getLeavesQty(), 0);
		}
		Assert.assertEquals(cumQty, order.getOrderQty()); // all the order is executed
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testPatialFillReport(){
		List<ExeReport> reports = new ArrayList<ExeReport>();
		Order order = new Order();
		order.setClOrdID(1002);
		order.setOrderType("2"); // limiteds order
		order.setSide(1); // buy side
		order.setOrderQty(100);
		order.setPrice(20);
		reports = exe.generateExeReport(order);
		Assert.assertFalse(reports.isEmpty());
		Assert.assertTrue(reports.size() <= 2);
		Assert.assertTrue(reports.size() >= 1);
		int cumQty = 0;
		int orderId = order.getClOrdID();
		for (ExeReport rpt: reports){
			cumQty += rpt.getLastShares();
			 //As a buy order, for each exe, both exe price and avg price <= than order price
			Assert.assertTrue(rpt.getLastPx() <= order.getPrice());
			Assert.assertTrue(rpt.getAvgPx() <= order.getPrice());
			//for each purchase quantity <= total order quantity, but larger than 0
			Assert.assertTrue(rpt.getLastShares() <= order.getOrderQty());
			Assert.assertTrue(rpt.getLastShares() > 0);
			//for each exe, the cumulative quantity of past exes  == that of current exe report 
			Assert.assertEquals(cumQty, rpt.getCumQty());
			// the orderId should be the same
			Assert.assertEquals(orderId, rpt.getOrderID()); 
		}
		Assert.assertEquals(cumQty, order.getOrderQty()); // all the order is executed
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testSellOrderReport(){
		List<ExeReport> reports = new ArrayList<ExeReport>();
		Order order = new Order();
		order.setClOrdID(1002);
		order.setOrderType("2"); // pegged order
		order.setSide(2); // sell side
		order.setOrderQty(100);
		order.setPrice(20);
		reports = exe.generateExeReport(order);
		Assert.assertFalse(reports.isEmpty());
		Assert.assertTrue(reports.size() <= 2);
		Assert.assertTrue(reports.size() >= 1);
		int cumQty = 0;
		int orderId = order.getClOrdID();
		for (ExeReport rpt: reports){
			cumQty += rpt.getLastShares();
			 //As a sell order, for each exe, both exe price and avg price >= than order price
			Assert.assertTrue(rpt.getLastPx() >= order.getPrice());
			Assert.assertTrue(rpt.getAvgPx() >= order.getPrice());
			//for each purchase quantity <= total order quantity, but larger than 0
			Assert.assertTrue(rpt.getLastShares() <= order.getOrderQty());
			Assert.assertTrue(rpt.getLastShares() > 0);
			//for each exe, the cumulative quantity of past exes  == that of current exe report 
			Assert.assertEquals(cumQty, rpt.getCumQty());
			// the orderId should be the same
			Assert.assertEquals(orderId, rpt.getOrderID()); 
		}
		Assert.assertEquals(cumQty, order.getOrderQty()); // all the order is executed
	}
}