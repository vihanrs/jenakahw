package com.jenakahw.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.jenakahw.domain.POHasProduct;
import com.jenakahw.domain.Product;
import com.jenakahw.domain.PurchaseOrder;
import com.jenakahw.email.EmailDetails;
import com.jenakahw.email.EmailService;
import com.jenakahw.repository.POHasProductRepository;
import com.jenakahw.repository.PurchaseOrderRepository;
import com.jenakahw.repository.PurchaseOrderStatusRepository;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.PurchaseOrderService;
import com.jenakahw.util.PrivilegeHelper;

import jakarta.transaction.Transactional;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

	// Make it final for immutability
	private final PurchaseOrderRepository purchaseOrderRepository;
	private final PurchaseOrderStatusRepository purchaseOrderStatusRepository;
	private final POHasProductRepository poHasProductRepository;
	private final PrivilegeHelper privilegeHelper;
	private final AuthService authService;
	private final EmailService emailService;

	private static final String MODULE = "Purchase Order";

	// Constructor injection
	public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository,
			PurchaseOrderStatusRepository purchaseOrderStatusRepository, POHasProductRepository poHasProductRepository,
			PrivilegeHelper privilegeHelper, AuthService authService, EmailService emailService) {
		this.purchaseOrderRepository = purchaseOrderRepository;
		this.purchaseOrderStatusRepository = purchaseOrderStatusRepository;
		this.poHasProductRepository = poHasProductRepository;
		this.privilegeHelper = privilegeHelper;
		this.authService = authService;
		this.emailService = emailService;
	}

	@Override
	public List<PurchaseOrder> findAll() {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return purchaseOrderRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}
	}

	@Override
	public List<Product> findPOProductsByPOID(Integer poId) {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return poHasProductRepository.findPOProductsByPOID(poId);
		} else {
			return null;
		}
	}

	@Override
	public POHasProduct findByPOIDAndProductId(Integer poId, Integer productId) {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return poHasProductRepository.findByPOIDAndProductId(poId, productId);
		} else {
			return null;
		}
	}

	@Override
	public List<PurchaseOrder> findPOByStatus(Integer poStatusId) {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return purchaseOrderRepository.findPurchaseOrdersByStatus(poStatusId);
		} else {
			return null;
		}
	}

	@Override
	public List<PurchaseOrder> findPurchaseOrdersBySupplier(Integer supplierId) {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return purchaseOrderRepository.findPurchaseOrdersBySupplier(supplierId);
		} else {
			return null;
		}
	}

	@Override
	@Transactional
	public String savePurchaseOrder(PurchaseOrder purchaseOrder) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		try {
			// set added user
			purchaseOrder.setUserId(authService.getLoggedUser().getId());
			// set added date time
			purchaseOrder.setAddedDateTime(LocalDateTime.now());

			// set next pocode
			String nextPOCode = purchaseOrderRepository.getNextPOCode();
			if (nextPOCode == null) {
				// formate current date
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
				String formattedDate = LocalDate.now().format(formatter);

				// create new pocode for start new date
				nextPOCode = "PO" + formattedDate + "001";
			}

			purchaseOrder.setPoCode(nextPOCode);

			for (POHasProduct poHasProduct : purchaseOrder.getPoHasProducts()) {
				poHasProduct.setPurchaseOrderId(purchaseOrder);
			}

			PurchaseOrder newPO = purchaseOrderRepository.save(purchaseOrder);

			if (newPO.getSupplierId().getEmail() != null) {
				// send email
				EmailDetails emailDetails = new EmailDetails();
				emailDetails.setSendTo(newPO.getSupplierId().getEmail());
				emailDetails.setSubject("Jenaka Hardware | New Purchase Order " + newPO.getPoCode());

				String msgHeading = "New purchase order | Jenaka Hardware \n. Please check the order details below. \n\n";
				String msgBody = "Items: \n";
				for (POHasProduct poHasProduct : purchaseOrder.getPoHasProducts()) {
					msgBody += poHasProduct.getProductId().getName() + " - " + poHasProduct.getQty() + " x "
							+ poHasProduct.getPurchasePrice() + " = " + poHasProduct.getLineAmount() + "\n";
				}
				String msgTotalAmount = "Total Amount: " + purchaseOrder.getTotalAmount() + "\n";
				String msgFooter = "Thank you.";
				String fullMsg = msgHeading + msgBody + msgTotalAmount + msgFooter;

				emailDetails.setMsgBody(fullMsg);

				emailService.sendSimpleMail(emailDetails);

			}

			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	@Transactional
	public String updatePurchaseOrder(PurchaseOrder purchaseOrder) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "update")) {
			return "Access Denied !!!";
		}

		// check for existens
		PurchaseOrder extPurchaseOrder = purchaseOrderRepository.getReferenceById(purchaseOrder.getId());
		if (extPurchaseOrder == null) {
			return "Purchase Order Not Exist...!";
		}

		try {
			// set added user
			purchaseOrder.setUpdatedUserId(authService.getLoggedUser().getId());
			// set added date time
			purchaseOrder.setLastUpdatedDateTime(LocalDateTime.now());

			for (POHasProduct poHasProduct : purchaseOrder.getPoHasProducts()) {
				poHasProduct.setPurchaseOrderId(purchaseOrder);
			}

			PurchaseOrder updPO = purchaseOrderRepository.save(purchaseOrder);

			if (updPO.getSupplierId().getEmail() != null) {
				// send email
				EmailDetails emailDetails = new EmailDetails();
				emailDetails.setSendTo(updPO.getSupplierId().getEmail());
				emailDetails.setSubject("Jenaka Hardware | Update Purchase Order " + updPO.getPoCode());

				String msgHeading = "Updated purchase order | Jenaka Hardware \n. Please ignore the previous order request with this Purchase Order ID : "
						+ updPO.getPoCode() + " \n Please check the order details below. \n\n";
				String msgBody = "Items: \n";
				for (POHasProduct poHasProduct : purchaseOrder.getPoHasProducts()) {
					msgBody += poHasProduct.getProductId().getName() + " - " + poHasProduct.getQty() + " x "
							+ poHasProduct.getPurchasePrice() + " = " + poHasProduct.getLineAmount() + "\n";
				}
				String msgTotalAmount = "Total Amount: " + purchaseOrder.getTotalAmount() + "\n";
				String msgFooter = "Thank you.";
				String fullMsg = msgHeading + msgBody + msgTotalAmount + msgFooter;

				emailDetails.setMsgBody(fullMsg);

				emailService.sendSimpleMail(emailDetails);

			}
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	@Transactional
	public String deletePurchaseOrder(PurchaseOrder purchaseOrder) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "delete")) {
			return "Access Denied !!!";
		}

		// check existing
		PurchaseOrder extPurchaseOrder = purchaseOrderRepository.getReferenceById(purchaseOrder.getId());
		if (extPurchaseOrder == null) {
			return "Purchase Order Not Exist..!";
		}

		try {
			// set deleted data and time
			purchaseOrder.setDeletedDateTime(LocalDateTime.now());

			// set deleted user id
			purchaseOrder.setDeletedUserId(authService.getLoggedUser().getId());

			// set Purchase Order statuts to 'Deleted'
			purchaseOrder.setPurchaseOrderStatusId(purchaseOrderStatusRepository.getReferenceById(4));

			for (POHasProduct poHasProduct : purchaseOrder.getPoHasProducts()) {
				poHasProduct.setPurchaseOrderId(purchaseOrder);
			}

			PurchaseOrder delPO = purchaseOrderRepository.save(purchaseOrder);

			if (delPO.getSupplierId().getEmail() != null) {
				// send email
				EmailDetails emailDetails = new EmailDetails();
				emailDetails.setSendTo(delPO.getSupplierId().getEmail());
				emailDetails.setSubject("Jenaka Hardware | Cancel Purchase Order " + delPO.getPoCode());

				String msgHeading = "Cancel purchase order | Jenaka Hardware \n. Please NOTE the previous order request with this Purchase Order ID : "
						+ delPO.getPoCode() + " \n we decied to cancel this order. \n\n";

				String msgFooter = "Thank you.";
				String fullMsg = msgHeading + msgFooter;

				emailDetails.setMsgBody(fullMsg);

				emailService.sendSimpleMail(emailDetails);

			}

			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
