package com.async.util;


public class Constants {

	public static final String DB_NAME = Config.getProperty("db.name");
	public static final String DB_HOST = Config.getProperty("db.host");
	public static final String DB_PORT_STRRING = Config.getProperty("db.port");
	public static final Integer DB_PORT;
	
	static {
		// DB port 
		DB_PORT = Integer.parseInt(DB_PORT_STRRING);
	}
	
	public static final String OPERATION = "op";
	public static final String COLLECTION_KEY = "id";
	public static final String JQGRID_EMPTY = "_empty";
	public static final String JQGRID_LEVEL = "level";
	
	public enum DBCollectionEnum {
		
		// @formatter:off
		CUSTOMER_DETAILS("customer_details"),
		ACCESS_USER("access_user"),
		APPOINTMENT("appointment");
		// @formatter:on

		private final String collectionName;

		DBCollectionEnum(String collectionName) {
			this.collectionName = collectionName;
		}

		@Override
		public String toString() {
			return collectionName;
		}
	}
	
	public enum UIOperationsEnum {
		// @formatter:off
		VIEW,
		VIEW_ALL,
		ADD,
		EDIT,
		DELETE;
		// @formatter:on
	}
	
	public enum AppointmentStatusEnum {
		// @formatter:off
		NEW,
		SERVICED;
		// @formatter:on
	}
	
	public enum CriteriaEnum {
		// @formatter:off
		SET("$set");
		private String criteria;
		private CriteriaEnum(String criString) {
			this.criteria = criString;
		}
		public String toString(){
			return this.criteria;
		}
		// @formatter:on
	}
	
}
