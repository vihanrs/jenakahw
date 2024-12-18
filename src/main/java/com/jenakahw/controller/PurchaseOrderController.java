package com.jenakahw.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.POHasProduct;
import com.jenakahw.domain.Product;
import com.jenakahw.domain.PurchaseOrder;
import com.jenakahw.domain.User;
import com.jenakahw.email.EmailDetails;
import com.jenakahw.email.EmailService;
import com.jenakahw.repository.POHasProductRepository;
import com.jenakahw.repository.PurchaseOrderRepository;
import com.jenakahw.repository.PurchaseOrderStatusRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping(value = "/purchaseorder") // class level mapping
public class PurchaseOrderController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private PurchaseOrderRepository purchaseOrderRepository;

	@Autowired
	private PurchaseOrderStatusRepository purchaseOrderStatusRepository;

	@Autowired
	private PrivilegeController privilegeController;

	@Autowired
	private UserController userController;

	@Autowired
	private POHasProductRepository poHasProductRepository;

	@Autowired
	private EmailService emailService;

	private static final String MODULE = "Purchase Order";

	// GRN UI service [/purchaseorder -- return Purchase Order UI]
	@GetMapping
	public ModelAndView purchaseOrderUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User loggedUser = userController.getLoggedUser();
		String userRole = userController.getLoggedUserRole();

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("title", "Purchase Order | Jenaka Hardware");
		modelAndView.addObject("logusername", auth.getName());
		modelAndView.addObject("loguserrole", userRole);
		modelAndView.addObject("loguserphoto", loggedUser.getUserPhoto());
		modelAndView.setViewName("purchaseorder.html");
		return modelAndView;
	}

	// get mapping for get all purchaseorder data -- [/purchaseorder/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<PurchaseOrder> findAll() {
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return purchaseOrderRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}
	}

	// get mapping for find purchaseorder products by poid
	// --[/purchaseorder/findpoproductsbypoid/10]
	@GetMapping(value = "/findpoproductsbypoid/{poid}", produces = "application/json")
	public List<Product> findPOProductsByPOID(@PathVariable("poid") Integer poId) {
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return poHasProductRepository.findPOProductsByPOID(poId);
		} else {
			return null;
		}
	}

	// get mapping for find POHasProduct details by poid and product id
	// --[/purchaseorder/findpoproductsbypoid/10]
	@GetMapping(value = "/findpohasproductbypoidandproductid/{poid}/{productid}", produces = "application/json")
	public POHasProduct findByPOIDAndProductId(@PathVariable("poid") Integer poId,
			@PathVariable("productid") Integer productId) {
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return poHasProductRepository.findByPOIDAndProductId(poId, productId);
		} else {
			return null;
		}
	}

	// get mapping for find purchaseorder by status --
	// [/purchaseorder/getpobystatus/1]
	@GetMapping(value = "/getpobystatus/{poStatusId}", produces = "application/json")
	public List<PurchaseOrder> findPOByStatus(@PathVariable("poStatusId") Integer poStatusId) {
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return purchaseOrderRepository.findPurchaseOrdersByStatus(poStatusId);
		} else {
			return null;
		}
	}
	
	@GetMapping(value = "/getpobysupplier/{supplierId}", produces = "application/json")
	public List<PurchaseOrder> findPurchaseOrdersBySupplier(@PathVariable("supplierId") Integer supplierId) {
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return purchaseOrderRepository.findPurchaseOrdersBySupplier(supplierId);
		} else {
			return null;
		}
	}

	// post mapping for save new purchase order
	@Transactional
	@PostMapping
	public String savePurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		try {
			// set added user
			purchaseOrder.setUserId(userController.getLoggedUser().getId());
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

	// put mapping for update existing purchase order
	@Transactional
	@PutMapping
	public String updatePerchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "update")) {
			return "Access Denied !!!";
		}

		// check for existens
		PurchaseOrder extPurchaseOrder = purchaseOrderRepository.getReferenceById(purchaseOrder.getId());
		if (extPurchaseOrder == null) {
			return "Purchase Order Not Exist...!";
		}

		try {
			// set added user
			purchaseOrder.setUpdatedUserId(userController.getLoggedUser().getId());
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
				

				String msgHeading = "Updated purchase order | Jenaka Hardware \n. Please ignore the previous order request with this Purchase Order ID : "+updPO.getPoCode()+" \n Please check the order details below. \n\n";
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

	// delete mapping for delete a purchase order
	@Transactional
	@DeleteMapping
	public String deletePurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "delete")) {
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
			purchaseOrder.setDeletedUserId(userController.getLoggedUser().getId());

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
				

				String msgHeading = "Cancel purchase order | Jenaka Hardware \n. Please NOTE the previous order request with this Purchase Order ID : "+delPO.getPoCode()+" \n we decied to cancel this order. \n\n";
				
				String msgFooter = "Thank you.";
				String fullMsg = msgHeading +  msgFooter;

				emailDetails.setMsgBody(fullMsg);
				
				emailService.sendSimpleMail(emailDetails);

			}

			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
