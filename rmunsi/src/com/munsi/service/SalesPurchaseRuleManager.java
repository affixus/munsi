package com.munsi.service;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.munsi.pojo.invoice.purchase.PurchaseInvoice;
import com.munsi.pojo.invoice.purchase.PurchaseProduct;
import com.munsi.pojo.invoice.sales.SalesInvoice;
import com.munsi.pojo.invoice.sales.SalesProduct;
import com.munsi.pojo.master.Product;
import com.munsi.pojo.master.ProductBatch;
import com.munsi.util.ObjectFactory;
import com.munsi.util.ObjectFactory.ObjectEnum;

public class SalesPurchaseRuleManager {

	private static final Logger LOG = Logger.getLogger(SalesPurchaseRuleManager.class);
	private ProductServeice productService;

	public SalesPurchaseRuleManager() {
		productService = (ProductServeice) ObjectFactory.getInstance(ObjectEnum.PRODUCT_SERVICE);
	}

	public void applySalesInvoiceRule(SalesInvoice sInvoice) {

		try {
			Double sumOfNetPaybleProductPrice = 0.0;

			for (SalesProduct sProduct : sInvoice.getSalesProductList()) {

				Integer totalQuantity = null;
				Double derPrice = null;
				Double derTaxPrice = null;
				Double derDiscountPrice = null;
				Double netPaybleProductPrice = null;

				totalQuantity = sProduct.getFreeQuantity() + sProduct.getQuantity();
				derPrice = (double) (sProduct.getSalesRate() * sProduct.getQuantity());
				derTaxPrice = (derPrice * sProduct.getDerSumOfProudctTax()) / 100.0;
				derDiscountPrice = ((derTaxPrice + derPrice) * sProduct.getRawDiscountPercent()) / 100.0;
				netPaybleProductPrice = derPrice + derTaxPrice - derDiscountPrice;

				sProduct.setTotalQuantity(totalQuantity);
				sProduct.setDerPrice(derPrice);
				sProduct.setDerTaxPrice(derTaxPrice);
				sProduct.setDerDiscountPrice(derDiscountPrice);
				sProduct.setNetPaybleProductPrice(netPaybleProductPrice);

				sumOfNetPaybleProductPrice += netPaybleProductPrice;
			}

			Double invoiceTaxPrice = (sumOfNetPaybleProductPrice * sInvoice.getInvoiceTaxPercent()) / 100.0;
			Double invoiceDiscountPrice = sInvoice.getInvoiceDiscountPrice();
			Integer numberOfItem = sInvoice.getSalesProductList().size();
			Double netPayblePrice = sumOfNetPaybleProductPrice + invoiceTaxPrice - invoiceDiscountPrice;

			sInvoice.setSumOfNetPaybleProductPrice(sumOfNetPaybleProductPrice);
			sInvoice.setNumberOfItem(numberOfItem);
			sInvoice.setNetPayblePrice(netPayblePrice);

		} catch (Exception e) {
			LOG.error(e);
		}

	}

	public void applyPurchaseInvoiceRule(PurchaseInvoice pInvoice) {
		try {
			Double sumOfNetPaybleProductPrice = 0.0;
			Double sumOfNetTaxPrice = 0.0;
			for (PurchaseProduct pProduct : pInvoice.getPurchaseProductList()) {

				Integer totalQuantity = null;
				Double derPrice = null;
				Double derTaxPrice = null;
				Double derDiscountPrice = null;
				Double netPaybleProductPrice = null;

				totalQuantity = pProduct.getFreeQuantity() + pProduct.getQuantity();
				derPrice = (double) (pProduct.getPurchaseRate() * pProduct.getQuantity());
				derTaxPrice = (derPrice * pProduct.getDerSumOfProudctTax()) / 100.0;
				derDiscountPrice = ((derTaxPrice + derPrice) * pProduct.getRawDiscountPercent()) / 100.0;
				netPaybleProductPrice = derPrice + derTaxPrice - derDiscountPrice;

				pProduct.setTotalQuantity(totalQuantity);
				pProduct.setDerPrice(derPrice);
				pProduct.setDerTaxPrice(derTaxPrice);
				pProduct.setDerDiscountPrice(derDiscountPrice);
				pProduct.setNetPaybleProductPrice(netPaybleProductPrice);

				sumOfNetTaxPrice += derTaxPrice;
				sumOfNetPaybleProductPrice += netPaybleProductPrice;
			}

			Double invoiceTaxPrice = sumOfNetTaxPrice;
			Double invoiceDiscountPrice = pInvoice.getInvoiceDiscountPrice();
			Integer numberOfItem = pInvoice.getPurchaseProductList().size();
			Double freight = pInvoice.getFreight();
			Double roundOfAmount = pInvoice.getRoundOfAmount();
			Double netPayblePrice = sumOfNetPaybleProductPrice + freight - invoiceDiscountPrice + roundOfAmount;

			pInvoice.setInvoiceTaxPrice(invoiceTaxPrice);
			pInvoice.setInvoiceTaxPercent((invoiceTaxPrice / sumOfNetPaybleProductPrice) * 100);
			pInvoice.setInvoiceDiscountPercent((invoiceDiscountPrice / sumOfNetPaybleProductPrice) * 100);
			pInvoice.setSumOfNetPaybleProductPrice(sumOfNetPaybleProductPrice);
			pInvoice.setNumberOfItem(numberOfItem);
			pInvoice.setNetPayblePrice(netPayblePrice);
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	public void applyCustomerUpdates(SalesInvoice sInvoice) {
		try {
			Double netPayblePrice = sInvoice.getNetPayblePrice();
			Double paidAmount = sInvoice.getPaidAmount();

			Float outStandingAmount = sInvoice.getCustomer().getOutStandingAmount();
			outStandingAmount = outStandingAmount != null ? outStandingAmount : 0.0f;

			Double plusMinusAmount = paidAmount - netPayblePrice;

			if (plusMinusAmount > 0) {
				outStandingAmount = (float) (outStandingAmount - plusMinusAmount);
			} else {
				outStandingAmount = (float) (outStandingAmount + Math.abs(plusMinusAmount));
			}

			sInvoice.getCustomer().setOutStandingAmount(outStandingAmount);

		} catch (Exception e) {
			LOG.error(e);
		}
	}

	public void applySupplierUpdates(PurchaseInvoice pInvoice) {

	}

	public void applyInventoryUpdatesSales(Object sInvoice) {
		Set<? extends Product> productList = null;
		SalesInvoice salesInvoice = (SalesInvoice) sInvoice;
		productList = salesInvoice.getSalesProductList();

		for (Product product : productList) {
			Product productMaster = productService.get(product.get_id());
			Set<ProductBatch> batchList = productMaster.getBatchList();
			Integer currentMasterStock = productMaster.getCurrentStock();
			currentMasterStock = currentMasterStock != null ? currentMasterStock : 0;

			ProductBatch newProductBatch = new ProductBatch();
			newProductBatch.setBatchNumber(product.getBatchNumber());
			// Finding the existing batch to update
			if (batchList != null && batchList.contains(newProductBatch)) {
				SalesProduct salesProduct = (SalesProduct) product;
				currentMasterStock -= salesProduct.getTotalQuantity();

				for (ProductBatch pbatch : batchList) {
					if (pbatch.equals(newProductBatch)) {
						pbatch.setBatchCurrentStock(pbatch.getBatchCurrentStock() - salesProduct.getTotalQuantity());
						pbatch.setSaleRate(salesProduct.getSalesRate());
						break;
					}
				}
				productMaster.setSalesRate(salesProduct.getSalesRate());
				productMaster.setCurrentStock(currentMasterStock);
				productMaster.setBatchList(batchList);
				productService.update(productMaster);
			}

		}

	}

	public void applyInventoryUpdatesPurchase(Object sInvoice) {
		try {
			Set<? extends Product> productList = null;
			PurchaseInvoice purchaseInvoice = (PurchaseInvoice) sInvoice;
			productList = purchaseInvoice.getPurchaseProductList();

			for (Product product : productList) {
				Product productMaster = productService.get(product.get_id());
				Set<ProductBatch> batchList = productMaster.getBatchList();
				Integer currentMasterStock = productMaster.getCurrentStock();
				currentMasterStock = currentMasterStock != null ? currentMasterStock : 0;

				PurchaseProduct purchaseProduct = (PurchaseProduct) product;
				currentMasterStock += purchaseProduct.getTotalQuantity();

				ProductBatch newProductBatch = new ProductBatch();
				newProductBatch.setBatchNumber(product.getBatchNumber());
				// Finding the existing batch to update
				if (batchList != null && batchList.contains(newProductBatch)) {
					for (ProductBatch pbatch : batchList) {
						if (pbatch.equals(newProductBatch)) {
							pbatch.setBatchCurrentStock(pbatch.getBatchCurrentStock() + purchaseProduct.getTotalQuantity());
							pbatch.setExpiryDate(purchaseProduct.getExpiryDate());
							pbatch.setManufactureDate(purchaseProduct.getManufactureDate());
							pbatch.setMrp(purchaseProduct.getMrp());
							pbatch.setSaleRate(purchaseProduct.getSalesRate());
							pbatch.setPurchaseRate(purchaseProduct.getPurchaseRate());
							break;
						}
					}
				} else {// Adding new Batch logic goes here
					if (batchList == null) {
						batchList = new HashSet<>();
					}
					newProductBatch.setBatchCurrentStock(purchaseProduct.getTotalQuantity().longValue());
					newProductBatch.setExpiryDate(purchaseProduct.getExpiryDate());
					newProductBatch.setManufactureDate(purchaseProduct.getManufactureDate());
					newProductBatch.setMrp(purchaseProduct.getMrp());
					newProductBatch.setSaleRate(purchaseProduct.getSalesRate());
					newProductBatch.setPurchaseRate(purchaseProduct.getPurchaseRate());
					batchList.add(newProductBatch);
				}
				productMaster.setMrp(purchaseProduct.getMrp());
				productMaster.setSalesRate(purchaseProduct.getSalesRate());
				productMaster.setPurchaseRate(purchaseProduct.getPurchaseRate());

				productMaster.setCurrentStock(currentMasterStock);
				productMaster.setBatchList(batchList);
				productService.update(productMaster);
			}

		} catch (Exception e) {
			LOG.error(e);
		}
	}

	public static void main(String[] args) {
		SalesPurchaseRuleManager rManager = (SalesPurchaseRuleManager) ObjectFactory.getInstance(ObjectEnum.SALES_PURCHASE_RULE);
		rManager.applySalesInvoiceRule(null);
	}

}
