package com.jenakahw.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.jenakahw.domain.Grn;
import com.jenakahw.domain.GrnHasProduct;
import com.jenakahw.domain.Product;
import com.jenakahw.domain.Stock;
import com.jenakahw.repository.GrnRepository;
import com.jenakahw.repository.GrnStatusRepository;
import com.jenakahw.repository.PurchaseOrderRepository;
import com.jenakahw.repository.StockRepository;
import com.jenakahw.repository.StockStatusRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping(value = "/grn")
public class GrnController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private GrnRepository grnRepository;

	@Autowired
	private GrnStatusRepository grnStatusRepository;

	@Autowired
	private PrivilegeController privilegeController;

	@Autowired
	private UserController userController;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private StockStatusRepository stockStatusRepository;

	@Autowired
	private PurchaseOrderRepository purchaseOrderRepository;

	private static final String MODULE = "GRN";

	// GRN UI service [/grn -- return GRN UI]
	@GetMapping
	public ModelAndView grnUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("title", "GRN | Jenaka Hardware");
		modelAndView.addObject("logusername", auth.getName());
		modelAndView.setViewName("grn.html");
		return modelAndView;
	}

	// get mapping for get all grn data -- [/grn/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Grn> findAll() {
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return grnRepository.findAll();
		} else {
			return null;
		}
	}

	// get mapping for get all grn data by grnID -- [/grn/findall]
	@GetMapping(value = "/findbyid/{grnId}", produces = "application/json")
	public Grn findByGrnId(@PathVariable("grnId") Integer grnId) {
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return grnRepository.getGrnById(grnId);
		} else {
			return null;
		}
	}

	// post mapping for save grn
	@Transactional
	@PostMapping
	public String saveGrn(@RequestBody Grn grn) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		try {
			// set added user
			grn.setAddedUserId(userController.getLoggedUser().getId());
			// set added date time
			grn.setAddedDateTime(LocalDateTime.now());

			grn.setPurchaseOrderId(purchaseOrderRepository.getReferenceById(10));

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

				} else {
					Stock newStock = new Stock();
					newStock.setProductId(product);
					newStock.setCostPrice(grnHasProduct.getCostPrice());
					newStock.setSellPrice(grnHasProduct.getSellPrice());
					newStock.setAvailableQty(grnHasProduct.getQty());
					newStock.setTotalQty(grnHasProduct.getQty());
					newStock.setStockStatus(stockStatusRepository.getReferenceById(1));
					stockRepository.save(newStock);
				}
			}
			return "OK";
		} catch (Exception e) {
			return "GRN Save Not Completed : " + e.getMessage();
		}
	}

	// put mapping for update existing grn
	@Transactional
	@PutMapping
	public String updateGrn(@RequestBody Grn grn) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "update")) {
			return "Access Denied !!!";
		}

		// check for existens
		Grn extGrn = grnRepository.getReferenceById(grn.getId());
		if (extGrn == null) {
			return "GRN Update Not Completed : GRN Not Exist...!";
		}

		try {
			// set updated user
			grn.setUpdatedUserId(userController.getLoggedUser().getId());
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

	// delete mapping for delete a GRN
	@Transactional
	@DeleteMapping
	public String deleteGrn(@RequestBody Grn grn) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "delete")) {
			return "Access Denied !!!";
		}

		// check for existens
		Grn extGrn = grnRepository.getReferenceById(grn.getId());
		if (extGrn == null) {
			return "GRN Delete Not Completed : GRN Not Exist...!";
		}

		try {
			// set deleted user
			grn.setDeletedUserId(userController.getLoggedUser().getId());
			// set deleted date and time
			grn.setDeletedDateTime(LocalDateTime.now());

			// set GRN status to 'Deleted'
			grn.setGrnStatusId(grnStatusRepository.getReferenceById(2));

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
