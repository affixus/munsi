package com.munsi.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.QueryOperators;
import com.mongodb.util.JSON;
import com.munsi.dao.ProductDao;
import com.munsi.pojo.master.Product;
import com.munsi.pojo.master.ProductBatch;
import com.munsi.pojo.master.Tax;
import com.munsi.util.CommonUtil;
import com.munsi.util.Config;
import com.munsi.util.Constants.DBCollectionEnum;
import com.munsi.util.MongoUtil;

public class MongoProductDao implements ProductDao {
	private static final Logger LOG = Logger.getLogger(MongoProductDao.class);

	public static final String KEY_MANUFACTURER_XID = "manufacturerXid";
	public static final String KEY_TAX_LIST_XID = "taxListXid";
	public static final String KEY_PRODUCT_GROUP_XID = "productGroupXid";
	public static final String KEY_PRODUCT_SUBGROUP_XID = "productSubGroupXid";

	public static final String KEY_MANUFACTURER = "manufacturer";
	public static final String KEY_PRODUCT_GROUP = "productGroup";
	public static final String KEY_PRODUCT_SUBGROUP = "productSubGroup";
	public static final String KEY_TAX_LIST = "taxList";

	private String collProduct = DBCollectionEnum.MAST_PRODUCT.toString();
	private String collManufacturer = DBCollectionEnum.MAST_MANUFACTURER.toString();
	private String collTax = DBCollectionEnum.MAST_TAX.toString();
	private String collProductGroup = DBCollectionEnum.MAST_PRODUCT_GROUP.toString();

	private DB mongoDB = MongoUtil.getDB();

	@Override
	public Boolean create(Product product) {
		try {
			Date date = new Date();
			product.setCtime(date);
			product.setUtime(date);
			String _id = MongoUtil.getNextSequence(DBCollectionEnum.MAST_PRODUCT).toString();
			product.set_id(_id);

			DBCollection collection = mongoDB.getCollection(collProduct);
			String jsonString = CommonUtil.objectToJson(product);

			DBObject dbObject = (DBObject) JSON.parse(jsonString);

			BasicDBList basicDBList = new BasicDBList();
			if (product.getTaxList() != null) {
				for (Tax tax : product.getTaxList()) {
					DBRef taxRef = new DBRef(mongoDB, collTax, tax.get_id());
					basicDBList.add(taxRef);
				}
			}

			DBRef manufacturerRef = new DBRef(mongoDB, collManufacturer, product.getManufacturer().get_id());
			DBRef productGroupRef = new DBRef(mongoDB, collProductGroup, product.getProductGroup().get_id());
			DBRef productSubGroupRef = new DBRef(mongoDB, collProductGroup, product.getProductSubGroup().get_id());

			dbObject.put(KEY_TAX_LIST_XID, basicDBList);
			dbObject.put(KEY_MANUFACTURER_XID, manufacturerRef);
			dbObject.put(KEY_PRODUCT_GROUP_XID, productGroupRef);
			dbObject.put(KEY_PRODUCT_SUBGROUP_XID, productSubGroupRef);

			dbObject.removeField(KEY_MANUFACTURER);
			dbObject.removeField(KEY_TAX_LIST);
			dbObject.removeField(KEY_PRODUCT_GROUP);
			dbObject.removeField(KEY_PRODUCT_SUBGROUP);

			collection.insert(dbObject);
			return true;

		} catch (Exception exception) {
			LOG.equals(exception);
		}
		return false;
	}

	@Override
	public Boolean update(Product product) {
		try {
			Date date = new Date();
			product.setUtime(date);

			DBCollection collection = mongoDB.getCollection(collProduct);
			String jsonString = CommonUtil.objectToJson(product);

			DBObject dbObject = (DBObject) JSON.parse(jsonString);
			if (product.getTaxList() != null) {
				BasicDBList basicDBList = new BasicDBList();
				for (Tax tax : product.getTaxList()) {
					DBRef taxRef = new DBRef(mongoDB, collTax, tax.get_id());
					basicDBList.add(taxRef);
				}
				dbObject.put(KEY_TAX_LIST_XID, basicDBList);
				dbObject.removeField(KEY_TAX_LIST);
			}
			if (product.getManufacturer() != null) {
				DBRef manufacturerRef = new DBRef(mongoDB, collManufacturer, product.getManufacturer().get_id());
				dbObject.put(KEY_MANUFACTURER_XID, manufacturerRef);
				dbObject.removeField(KEY_MANUFACTURER);
			}
			if (product.getProductGroup() != null) {
				DBRef productGroupRef = new DBRef(mongoDB, collProductGroup, product.getProductGroup().get_id());
				dbObject.put(KEY_PRODUCT_GROUP_XID, productGroupRef);
				dbObject.removeField(KEY_PRODUCT_GROUP);
			}
			if (product.getProductSubGroup() != null) {
				DBRef productSubGroupRef = new DBRef(mongoDB, collProductGroup, product.getProductSubGroup().get_id());
				dbObject.put(KEY_PRODUCT_SUBGROUP_XID, productSubGroupRef);
				dbObject.removeField(KEY_PRODUCT_SUBGROUP);
			}
			dbObject.removeField("_id");

			DBObject query = new BasicDBObject("_id", product.get_id());
			DBObject updateObj = new BasicDBObject("$set", dbObject);

			collection.update(query, updateObj);
			return true;

		} catch (Exception exception) {
			LOG.equals(exception);
		}
		return false;

	}

	@Override
	public Boolean delete(String _id) {
		Product product = new Product();
		product.set_id(_id);
		product.setDeleted(true);
		product.setUtime(new Date());
		return update(product);
	}

	@Override
	public Product get(String _id) {
		return get(_id, false); // _id of product, withReferences - false
	}

	@Override
	public Product get(String _id, Boolean withReferences) {
		try {
			DBCollection collection = mongoDB.getCollection(collProduct);
			DBObject query = new BasicDBObject("_id", _id);
			DBObject dbObject = collection.findOne(query);

			if (withReferences == true) {
				BasicDBList basicDBList = (BasicDBList) dbObject.get(KEY_TAX_LIST_XID);

				BasicDBList taxDBOList = new BasicDBList();
				if (basicDBList != null) {
					DBRef[] taxDBRefList = (DBRef[]) basicDBList.toArray();

					for (DBRef taxDBRef : taxDBRefList) {
						DBObject taxDBObject = taxDBRef.fetch();
						taxDBOList.add(taxDBObject);
					}
				}

				DBObject manufacturerDBO = ((DBRef) dbObject.get(KEY_MANUFACTURER_XID)).fetch();
				DBObject productGroupDBO = ((DBRef) dbObject.get(KEY_PRODUCT_GROUP_XID)).fetch();
				DBObject productSubGroupDBO = ((DBRef) dbObject.get(KEY_PRODUCT_SUBGROUP_XID)).fetch();

				dbObject.put(KEY_TAX_LIST, taxDBOList);
				dbObject.put(KEY_MANUFACTURER, manufacturerDBO);
				dbObject.put(KEY_PRODUCT_GROUP, productGroupDBO);
				dbObject.put(KEY_PRODUCT_SUBGROUP, productSubGroupDBO);
			}

			dbObject.removeField(KEY_TAX_LIST_XID);
			dbObject.removeField(KEY_MANUFACTURER_XID);
			dbObject.removeField(KEY_PRODUCT_GROUP_XID);
			dbObject.removeField(KEY_PRODUCT_SUBGROUP_XID);

			String jsonString = JSON.serialize(dbObject);
			Product product = (Product) CommonUtil.jsonToObject(jsonString, Product.class.getName());

			return product;

		} catch (Exception exception) {
			LOG.equals(exception);
		}
		return null;
	}

	@Override
	public List<Product> getAll() {
		return getAll(false);
	}

	@Override
	public List<Product> getAll(Boolean withReferences) {
		try {
			DBCollection collection = mongoDB.getCollection(collProduct);
			DBObject deletedQuery = MongoUtil.getQueryToCheckDeleted();
			DBCursor dbCursor = collection.find(deletedQuery);

			List<Product> productList = new ArrayList<>();

			while (dbCursor.hasNext()) {
				DBObject dbObject = dbCursor.next();

				if (withReferences == true) {

					BasicDBList basicDBList = (BasicDBList) dbObject.get(KEY_TAX_LIST_XID);

					BasicDBList taxDBOList = new BasicDBList();
					if (basicDBList != null) {
						DBRef[] taxDBRefList = (DBRef[]) basicDBList.toArray();

						for (DBRef taxDBRef : taxDBRefList) {
							DBObject taxDBObject = taxDBRef.fetch();
							taxDBOList.add(taxDBObject);
						}
					}

					DBObject manufacturerDBO = ((DBRef) dbObject.get(KEY_MANUFACTURER_XID)).fetch();
					DBObject productGroupDBO = ((DBRef) dbObject.get(KEY_PRODUCT_GROUP_XID)).fetch();
					DBObject productSubGroupDBO = ((DBRef) dbObject.get(KEY_PRODUCT_SUBGROUP_XID)).fetch();

					dbObject.put(KEY_TAX_LIST, taxDBOList);
					dbObject.put(KEY_MANUFACTURER_XID, manufacturerDBO);
					dbObject.put(KEY_PRODUCT_GROUP, productGroupDBO);
					dbObject.put(KEY_PRODUCT_SUBGROUP, productSubGroupDBO);

				}

				dbObject.removeField(KEY_TAX_LIST_XID);
				dbObject.removeField(KEY_MANUFACTURER_XID);
				dbObject.removeField(KEY_PRODUCT_GROUP_XID);
				dbObject.removeField(KEY_PRODUCT_SUBGROUP_XID);

				String jsonString = JSON.serialize(dbObject);
				Product product = (Product) CommonUtil.jsonToObject(jsonString, Product.class.getName());
				productList.add(product);
			}

			return productList;

		} catch (Exception exception) {
			LOG.equals(exception);
		}
		return null;
	}

	@Override
	public Product getProductByCode(String code, Boolean withReferences, Boolean excludeExpiredBatch, Boolean excludeZeroStock) {
		DBObject queryObject = new BasicDBObject("code", code);
		return getProductByQuery(queryObject, withReferences, excludeExpiredBatch, excludeZeroStock);
	}

	@Override
	public Product getProductByBarCode(String barCode, Boolean withReferences, Boolean excludeExpiredBatch, Boolean excludeZeroStock) {
		DBObject queryObject = new BasicDBObject("barCode", barCode);
		return getProductByQuery(queryObject, withReferences, excludeExpiredBatch, excludeZeroStock);
	}

	@Override
	public Product getProductByName(String name, Boolean withReferences, Boolean excludeExpiredBatch, Boolean excludeZeroStock) {
		DBObject queryObject = new BasicDBObject("name", name);
		return getProductByQuery(queryObject, withReferences, excludeExpiredBatch, excludeZeroStock);
	}

	private Product getProductByQuery(DBObject queryObject, Boolean withReferences, Boolean excludeExpiredBatch, Boolean excludeZeroStock) {
		try {
			DBCollection collection = mongoDB.getCollection(collProduct);
			DBObject dbObject = collection.findOne(queryObject);

			if (withReferences == true) {
				@SuppressWarnings("unchecked")
				List<DBRef> basicDBList = (List<DBRef>) dbObject.get(KEY_TAX_LIST_XID);

				BasicDBList taxDBOList = new BasicDBList();
				if (basicDBList != null) {
					for (DBRef taxDBRef : basicDBList) {
						DBObject taxDBObject = taxDBRef.fetch();
						taxDBOList.add(taxDBObject);
					}
				}

				DBObject manufacturerDBO = ((DBRef) dbObject.get(KEY_MANUFACTURER_XID)).fetch();
				DBObject productGroupDBO = ((DBRef) dbObject.get(KEY_PRODUCT_GROUP_XID)).fetch();
				DBObject productSubGroupDBO = ((DBRef) dbObject.get(KEY_PRODUCT_SUBGROUP_XID)).fetch();

				dbObject.put(KEY_TAX_LIST, taxDBOList);
				dbObject.put(KEY_MANUFACTURER, manufacturerDBO);
				dbObject.put(KEY_PRODUCT_GROUP, productGroupDBO);
				dbObject.put(KEY_PRODUCT_SUBGROUP, productSubGroupDBO);
			}

			dbObject.removeField(KEY_TAX_LIST_XID);
			dbObject.removeField(KEY_MANUFACTURER_XID);
			dbObject.removeField(KEY_PRODUCT_GROUP_XID);
			dbObject.removeField(KEY_PRODUCT_SUBGROUP_XID);

			String jsonString = JSON.serialize(dbObject);
			Product product = (Product) CommonUtil.jsonToObject(jsonString, Product.class.getName());

			if (excludeExpiredBatch || excludeZeroStock) {
				// Remove batch of 0 stock and expired stock
				Set<ProductBatch> productBatchList = product.getBatchList();
				if (productBatchList != null && productBatchList.size() > 0) {
					Iterator<ProductBatch> i = productBatchList.iterator();
					while (i.hasNext()) {
						ProductBatch b = i.next();

						if (excludeZeroStock && b.getBatchCurrentStock() != null && b.getBatchCurrentStock() <= 0) {
							i.remove();
						} else if (excludeExpiredBatch && b.getExpiryDate() != null && !new Date().before(b.getExpiryDate())) {
							i.remove();
						}
					}
				}
			}
			return product;

		} catch (Exception exception) {
			exception.printStackTrace();
			LOG.equals(exception);
		}
		return null;
	}

	@Override
	public int getAvailableStock(String _id) {
		DBCollection collection = mongoDB.getCollection(collProduct);
		DBObject query = new BasicDBObject("_id", _id);
		DBObject pCurrentStockKey = new BasicDBObject("currentStock", 1);
		BasicDBObject dbObject = (BasicDBObject) collection.findOne(query, pCurrentStockKey);
		Integer currStock = dbObject.getInt("currentStock", -1);
		return currStock;
	}

	@Override
	public ProductBatch getBatchInfo(String _id, String batchNo) {
		try {
			DBCollection collection = mongoDB.getCollection(collProduct);
			DBObject idQuery = new BasicDBObject("_id", _id);

			DBObject pBatchNo = new BasicDBObject("batchNumber", batchNo);
			DBObject elementMatch = new BasicDBObject(QueryOperators.ELEM_MATCH, pBatchNo);
			DBObject batchList = new BasicDBObject("batchList", elementMatch);

			DBObject pRow = collection.findOne(idQuery, batchList);
			BasicDBList basicDBList = (BasicDBList) pRow.get("batchList");
			DBObject singleBatch = (DBObject) basicDBList.get(0);
			String jsonString = JSON.serialize(singleBatch);

			ProductBatch productBatch = (ProductBatch) CommonUtil.jsonToObject(jsonString, ProductBatch.class.getName());

			return productBatch;

		} catch (Exception exception) {
			LOG.equals(exception);
		}
		return null;
	}

	@Override
	public List<ProductBatch> getBatchList(String _id) {
		try {
			DBCollection collection = mongoDB.getCollection(collProduct);
			DBObject batchKey = new BasicDBObject("batchList", 1);
			DBObject query = new BasicDBObject("_id", _id);
			DBObject dbObject = collection.findOne(query, batchKey);
			BasicDBList basicDBList = (BasicDBList) dbObject.get("batchList");
			if (basicDBList == null || basicDBList.size() == 0) {
				return null;
			}

			List<ProductBatch> productBatchList = null;
			productBatchList = new ArrayList<>();
			for (int i = 0; i < basicDBList.size(); i++) {
				DBObject obj = (DBObject) basicDBList.get(i);
				String jsonString = JSON.serialize(obj);
				ProductBatch productBatch = (ProductBatch) CommonUtil.jsonToObject(jsonString, ProductBatch.class);
				productBatchList.add(productBatch);
			}
			return productBatchList;
		} catch (Exception exception) {
			LOG.equals(exception);
		}
		return null;
	}

	@Override
	public Product getForNotification(String _id) {
		try {
			DBCollection collection = mongoDB.getCollection(collProduct);
			DBObject query = new BasicDBObject("_id", _id);
			DBObject fields = new BasicDBObject("code", 1).append("name", 1).append("minStock", 1).append("currentStock", 1);

			DBObject dbObject = collection.findOne(query, fields);

			String jsonString = JSON.serialize(dbObject);
			Product product = (Product) CommonUtil.jsonToObject(jsonString, Product.class.getName());

			return product;

		} catch (Exception exception) {
			LOG.equals(exception);
		}
		return null;
	}

	@Override
	public List<Product> getAllShortedProdect() {
		try {
			DBCollection collection = mongoDB.getCollection(collProduct);
			DBObject delQuery = MongoUtil.getQueryToCheckDeleted();
			DBObject fieldQuery = new BasicDBObject(QueryOperators.WHERE, "function() { return this.currentStock <= this.minStock } ");
			BasicDBList andQuery = new BasicDBList();
			andQuery.add(delQuery);
			andQuery.add(fieldQuery);
			DBObject finalQuery = new BasicDBObject("$and", andQuery);
			DBObject fields = new BasicDBObject("code", 1).append("name", 1).append("minStock", 1).append("currentStock", 1);
			DBCursor dbCursor = collection.find(finalQuery, fields);
			if (dbCursor == null) {
				return null;
			}
			List<Product> productList = new ArrayList<Product>();

			while (dbCursor.hasNext()) {
				DBObject dbObject = dbCursor.next();
				String jsonString = JSON.serialize(dbObject);
				Product product = (Product) CommonUtil.jsonToObject(jsonString, Product.class.getName());
				productList.add(product);
			}
			return productList;
		} catch (Exception exception) {
			LOG.equals(exception);
		}
		return null;
	}

	@Override
	public List<Product> getAllExpireProdect() {
		try {
			DBCollection collection = mongoDB.getCollection(collProduct);
			DBObject delQuery = MongoUtil.getQueryToCheckDeleted();

			Calendar calendar = Calendar.getInstance();
			String strDays = Config.getProperty("expiry.warning.days", "10");
			int days = Integer.parseInt(strDays);
			calendar.add(Calendar.DATE, days);
			long dateInLong = calendar.getTimeInMillis();

			DBObject batchQuery = new BasicDBObject("batchCurrentStock", new BasicDBObject(QueryOperators.GTE, 1)).append("expiryDate", new BasicDBObject(QueryOperators.LTE, dateInLong));
			DBObject elementMatch = new BasicDBObject(QueryOperators.ELEM_MATCH, batchQuery);
			DBObject batchList = new BasicDBObject("batchList", elementMatch);

			BasicDBList andQuery = new BasicDBList();
			andQuery.add(delQuery);
			andQuery.add(batchList);

			DBObject finalQuery = new BasicDBObject("$and", andQuery);
			DBObject fields = new BasicDBObject("code", 1).append("name", 1).append("batchList", 1);
			DBCursor dbCursor = collection.find(finalQuery, fields);
			if (dbCursor == null) {
				return null;
			}
			List<Product> productList = new ArrayList<>();
			while (dbCursor.hasNext()) {
				DBObject dbObject = dbCursor.next();
				String jsonString = JSON.serialize(dbObject);
				Product product = (Product) CommonUtil.jsonToObject(jsonString, Product.class.getName());
				Set<ProductBatch> productBatchList = product.getBatchList();

				if (productBatchList != null && productBatchList.size() > 0) {
					Iterator<ProductBatch> bIter = productBatchList.iterator();

					while (bIter.hasNext()) {
						ProductBatch batch = bIter.next();
						Date expDate = batch.getExpiryDate();
						if (expDate == null || expDate.after(calendar.getTime())) {
							bIter.remove();
						}
					}
				}
				productList.add(product);
			}
			return productList;

		} catch (Exception exception) {
			LOG.equals(exception);
		}
		return null;
	}

	/*public static void main(String[] args) {
		MongoProductDao mpd = new MongoProductDao();
	}*/

}
