package com.jenakahw.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.jenakahw.domain.Grn;
import com.jenakahw.domain.GrnHasProduct;
import com.jenakahw.domain.Product;
import com.jenakahw.domain.PurchaseOrder;
import com.jenakahw.domain.Stock;
import com.jenakahw.repository.GrnRepository;
import com.jenakahw.repository.GrnStatusRepository;
import com.jenakahw.repository.PurchaseOrderRepository;
import com.jenakahw.repository.PurchaseOrderStatusRepository;
import com.jenakahw.repository.StockRepository;
import com.jenakahw.repository.StockStatusRepository;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.GrnService;
import com.jenakahw.service.interfaces.StockService;
import com.jenakahw.util.PrivilegeHelper;

@Service
public class GrnServiceImpl implements GrnService {
	// Make it final for immutability
	private final GrnRepository grnRepository;
	private final StockRepository stockRepository;
	private final StockStatusRepository stockStatusRepository;
	private final PurchaseOrderRepository purchaseOrderRepository;
	private final PurchaseOrderStatusRepository purchaseOrderStatusRepository;
	private final StockService stockService ;
	private final PrivilegeHelper privilegeHelper;
	private final AuthService authService;

	private static final String MODULE = "GRN";

	// Constructor injection
	public GrnServiceImpl(GrnRepository grnRepository, GrnStatusRepository grnStatusRepository,
			StockRepository stockRepository, StockStatusRepository stockStatusRepository,
			PurchaseOrderRepository purchaseOrderRepository,
			PurchaseOrderStatusRepository purchaseOrderStatusRepository, PrivilegeHelper privilegeHelper,
			AuthService authService,StockService stockService) {
		this.grnRepository = grnRepository;
		this.stockRepository = stockRepository;
		this.stockStatusRepository = stockStatusRepository;
		this.purchaseOrderRepository = purchaseOrderRepository;
		this.purchaseOrderStatusRepository = purchaseOrderStatusRepository;
		this.stockService = stockService;
		this.privilegeHelper = privilegeHelper;
		this.authService = authService;
	}

	@Override
	public List<Grn> findAll() {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return grnRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}
	}

	@Override
	public List<Grn> findAllIncompleteBySupplier(int supplierId) {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return grnRepository.findAllIncompleteBySupplier(supplierId);
		} else {
			return null;
		}
	}

	@Override
	public Grn findByGrnId(Integer grnId) {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return grnRepository.getGrnById(grnId);
		} else {
			return null;
		}
	}

	@Override
	public String saveGrn(Grn grn) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		try {
			// set added user
			grn.setAddedUserId(authService.getLoggedUser().getId());
			// set added date time
			grn.setAddedDateTime(LocalDateTime.now());

			// set po status as 'Received'
			PurchaseOrder currentPO = purchaseOrderRepository.getReferenceById(grn.getPurchaseOrderId().getId());
			currentPO.setPurchaseOrderStatusId(purchaseOrderStatusRepository.getReferenceById(2));
			purchaseOrderRepository.save(currentPO);

			// set next grn code
			String nextGrnCode = grnRepository.getNextGRNCode();
			if (nextGrnCode == null) {
				// formate current date
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
				String formattedDate = LocalDate.now().format(formatter);

				// create new grncode for start new date
				nextGrnCode = "GRN" + formattedDate + "001";
			}

			grn.setGrnCode(nextGrnCode);

			for (GrnHasProduct grnHasProduct : grn.getGrnHasProducts()) {
				grnHasProduct.setGrnId(grn);
			}

			Grn newGrn = grnRepository.save(grn);

			// need to update stock
			for (GrnHasProduct grnHasProduct : newGrn.getGrnHasProducts()) {
				Product product = grnHasProduct.getProductId();
				Stock extStock = stockRepository.getByProductAndPrice(product.getId(), grnHasProduct.getCostPrice());
				if (extStock != null) {
					extStock.setAvailableQty(extStock.getAvailableQty().add(grnHasProduct.getQty()));
					extStock.setTotalQty(extStock.getTotalQty().add(grnHasProduct.getQty()));
					extStock.setStockStatus(stockStatusRepository.getReferenceById(1));
					stockRepository.save(extStock);
					stockService.updateStockStatus(extStock.getId());

				} else {
					Stock newStock = new Stock();
					newStock.setProductId(product);
					newStock.setCostPrice(grnHasProduct.getCostPrice());
					newStock.setSellPrice(grnHasProduct.getSellPrice());
					newStock.setAvailableQty(grnHasProduct.getQty());
					newStock.setTotalQty(grnHasProduct.getQty());
					newStock.setStockStatus(stockStatusRepository.getReferenceById(1));
					Stock savedStock = stockRepository.save(newStock);
					stockService.updateStockStatus(savedStock.getId());
				}
			}
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public String updateGrn(Grn grn) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "update")) {
			return "Access Denied !!!";
		}

		// check for existens
		Grn extGrn = grnRepository.getReferenceById(grn.getId());
		if (extGrn == null) {
			return "GRN Not Exist...!";
		}

		try {
			// set updated user
			grn.setUpdatedUserId(authService.getLoggedUser().getId());
			// set updated date and time
			grn.setLastUpdatedDateTime(LocalDateTime.now());

			for (GrnHasProduct grnHasProduct : grn.getGrnHasProducts()) {
				grnHasProduct.setGrnId(grn);
			}

			grnRepository.save(grn);

			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
