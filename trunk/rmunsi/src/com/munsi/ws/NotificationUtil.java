package com.munsi.ws;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.munsi.dao.ProductDao;
import com.munsi.dao.impl.MongoProductDao;
import com.munsi.pojo.master.Product;
import com.munsi.util.CommonUtil;
import com.munsi.util.ObjectFactory;
import com.munsi.util.ObjectFactory.ObjectEnum;

public class NotificationUtil {

	private final static Set<WebClient> webClientList = new CopyOnWriteArraySet<WebClient>();

	private static Map<String, String> stockShortAlertList = new LinkedHashMap<String, String>();
	private static Map<String, String> stockExpAlertList = new LinkedHashMap<String, String>();

	public static void initNotification() {
		ProductDao productDao = (MongoProductDao) ObjectFactory.getInstance(ObjectEnum.PRODUCT_DAO);
		List<Product> productList = productDao.getAllShortedProdect();
		if (productList != null && productList.size() > 0) {
			for (Product product : productList) {
				String jsonString = CommonUtil.objectToJson(product);
				stockShortAlertList.put(product.get_id(), jsonString);
			}
		}

		productList = productDao.getAllExpireProdect();
		if (productList != null && productList.size() > 0) {
			for (Product product : productList) {
				String jsonString = CommonUtil.objectToJson(product);
				stockExpAlertList.put(product.get_id(), jsonString);
			}
		}
	}

	public static void checkProductForNotification(Product product) {

		if (product.getCurrentStock() <= product.getMinStock()) {
			String jsonString = CommonUtil.objectToJson(product);
			stockShortAlertList.put(product.get_id(), jsonString);
			printAlertList();

		} else {
			String str = stockShortAlertList.remove(product.get_id());
			if (str != null) {
				printAlertList();
			}
		}
	}

	private static void printAlertList() {
		Collection<String> list = stockShortAlertList.values();
		String jsonString = CommonUtil.objectToJson(list);

		for (WebClient webClient : webClientList) {
			webClient.writeResponse(jsonString);
		}
	}

	public static void addWebClient(WebClient webClient) {
		webClientList.add(webClient);
		if (stockShortAlertList.size() > 0) {
			Collection<String> list = stockShortAlertList.values();
			String jsonString = CommonUtil.objectToJson(list);
			webClient.writeResponse(jsonString);
		}
	}

	public static void removeWebClient(WebClient webClient) {
		webClientList.remove(webClient);
	}

	public static void testWS() {
		MongoProductDao ps = new MongoProductDao();
		Product p = ps.getForNotification("4");
		Product p1 = ps.getForNotification("5");
		Product p2 = ps.getForNotification("6");
		Product p3 = ps.getForNotification("7");
		checkProductForNotification(p);
		checkProductForNotification(p1);
		checkProductForNotification(p2);
		checkProductForNotification(p3);
	}

	public static void main(String[] args) {
		try {
			NotificationUtil.initNotification();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
