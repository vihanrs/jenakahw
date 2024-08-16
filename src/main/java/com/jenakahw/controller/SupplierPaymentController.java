package com.jenakahw.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.Grn;
import com.jenakahw.domain.GrnHasSupplierPayment;
import com.jenakahw.domain.SupplierPayment;
import com.jenakahw.domain.User;
import com.jenakahw.repository.GrnRepository;
import com.jenakahw.repository.GrnStatusRepository;
import com.jenakahw.repository.GrnSupplierPaymentRepository;
import com.jenakahw.repository.SupplierPaymentRepository;

@RestController
//add class level mapping /supplierpayment
@RequestMapping(value = "/supplierpayment")
public class SupplierPaymentController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private SupplierPaymentRepository supplierPaymentRepository;

	@Autowired
	private GrnController grnController;

	@Autowired
	private GrnRepository grnRepository;

	@Autowired
	private GrnStatusRepository grnStatusRepository;

	@Autowired
	private GrnSupplierPaymentRepository grnSupplierPaymentRepository;

	@Autowired
	private UserController userController;

	@Autowired
	private PrivilegeController privilegeController;

	private static final String MODULE = "Supplier Payment";

	// get mapping for generate product UI
	@GetMapping
	public ModelAndView getProductUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User loggedUser = userController.getLoggedUser();
		String userRole = userController.getLoggedUserRole();

		ModelAndView supplierPaymentView = new ModelAndView();
		supplierPaymentView.addObject("title", "Supplier Payment  | Jenaka Hardware");
		supplierPaymentView.addObject("logusername", auth.getName());
		supplierPaymentView.addObject("loguserrole", userRole);
		supplierPaymentView.addObject("loguserphoto", loggedUser.getUserPhoto());
		supplierPaymentView.setViewName("paymentsupplier.html");
		return supplierPaymentView;
	}

	// get mapping for get all supplier payments
	@GetMapping(value = "/findall", produces = "application/json")
	public List<SupplierPayment> findAll() {
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			if(userController.isLoggedUserHasRole("Cashier")) {
				return supplierPaymentRepository.findAllByUser(userController.getLoggedUser().getId());
			}else {
				return supplierPaymentRepository.findAll(Sort.by(Direction.DESC, "id"));
			}
		} else {
			return null;
		}
	}

	// get mapping for get all supplier payments by added user
	@GetMapping(value = "/findallbyuser/{userid}", produces = "application/json")
	public List<SupplierPayment> findAllByUser(@PathVariable("userid") int userId) {
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return supplierPaymentRepository.findAllByUser(userId);
		} else {
			return null;
		}
	}

	// get mapping for get all grn payments by supplier payment id
	@GetMapping(value = "/findgrnpaymentsbysupplierpayment/{supplierpaymentid}", produces = "application/json")
	public List<GrnHasSupplierPayment> findGrnPaymentsSupplierPayment(@PathVariable("supplierpaymentid") int supplierPaymentId) {
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return grnSupplierPaymentRepository.findGrnPaymentsBySupplierPayment(supplierPaymentId);
		} else {
			return null;
		}
	}

	// post mapping for save new supplier payment
	@PostMapping
	public String saveSupplierPayment(@RequestBody SupplierPayment supplierPayment) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}
		try {
			// get logged user id
			int loggedUserId = userController.getLoggedUser().getId();
			supplierPayment.setAddedDateTime(LocalDateTime.now());
			supplierPayment.setAddedUserId(loggedUserId);

			// set next payment invoice id
			String nextPaymentInvCode = supplierPaymentRepository.getNextPayInvoiceID();
			if (nextPaymentInvCode == null) {
				// formate current date
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
				String formattedDate = LocalDate.now().format(formatter);

				// create new invoice id for start new date
				nextPaymentInvCode = "INVS" + formattedDate + "001";
			}

			supplierPayment.setPaymentInvoiceId(nextPaymentInvCode);

			// save supplier payment
			SupplierPayment newSupplierPayment = supplierPaymentRepository.save(supplierPayment);

			// get paidAmount
			BigDecimal paidAmount = newSupplierPayment.getPaidAmount();

			// get payment incomplete grns by supplier
			List<Grn> incompleteGrns = grnController
					.findAllIncompleteBySupplier(supplierPayment.getSupplierId().getId());

			for (Grn grn : incompleteGrns) {
				if (!paidAmount.equals(BigDecimal.ZERO)) {
					// get full grn object to update grn record
					Grn extGrn = grnRepository.getReferenceById(grn.getId());

					BigDecimal grnBalanceAmount = grn.getBalanceAmount();

					// Compare grnBalanceAmount with paidAmount
					int comparison = grnBalanceAmount.compareTo(paidAmount);
					if (comparison <= 0) {
						// grnBalance <= paidAmount (-1/0)

						// update grn
						extGrn.setBalanceAmount(BigDecimal.ZERO);// set 0 for balance amount
						extGrn.setPaid(grn.getPaid().add(grnBalanceAmount)); // update paid amount
						extGrn.setGrnStatusId(grnStatusRepository.getReferenceById(4)); // set status 'Completed'
						grnRepository.save(extGrn);

						// save new grn has supplier payment record
						GrnHasSupplierPayment grnHasSupplierPayment = new GrnHasSupplierPayment(grnBalanceAmount, grn,
								newSupplierPayment);
						grnSupplierPaymentRepository.save(grnHasSupplierPayment);

						// update remaining paid amount
						paidAmount = paidAmount.subtract(grnBalanceAmount);
					} else {
						// grnBalance > paidAmount (1)

						// update grn
						extGrn.setBalanceAmount(grnBalanceAmount.subtract(paidAmount));
						extGrn.setPaid(grn.getPaid().add(paidAmount)); // update paid amount
						grnRepository.save(extGrn);

						// save new grn has supplier payment record
						GrnHasSupplierPayment grnHasSupplierPayment = new GrnHasSupplierPayment(paidAmount, grn,
								newSupplierPayment);
						grnSupplierPaymentRepository.save(grnHasSupplierPayment);

						// update remaining paid amount
						paidAmount = BigDecimal.ZERO;
					}
				} else {
					break;
				}
			}
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
