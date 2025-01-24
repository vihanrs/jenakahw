package com.jenakahw.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.jenakahw.domain.Grn;
import com.jenakahw.domain.GrnHasSupplierPayment;
import com.jenakahw.domain.SupplierPayment;
import com.jenakahw.repository.GrnRepository;
import com.jenakahw.repository.GrnStatusRepository;
import com.jenakahw.repository.GrnSupplierPaymentRepository;
import com.jenakahw.repository.SupplierPaymentRepository;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.SupplierPaymentService;
import com.jenakahw.util.PrivilegeHelper;

import jakarta.transaction.Transactional;

@Service
public class SupplierPaymentServiceImpl implements SupplierPaymentService {
	// Make it final for immutability
	private final SupplierPaymentRepository supplierPaymentRepository;
	private final GrnRepository grnRepository;
	private final GrnStatusRepository grnStatusRepository;
	private final GrnSupplierPaymentRepository grnSupplierPaymentRepository;
	private final PrivilegeHelper privilegeHelper;
	private final AuthService authService;

	private static final String MODULE = "Supplier Payment";

	// Constructor injection
	public SupplierPaymentServiceImpl(SupplierPaymentRepository supplierPaymentRepository, GrnRepository grnRepository,
			GrnStatusRepository grnStatusRepository, GrnSupplierPaymentRepository grnSupplierPaymentRepository,
			PrivilegeHelper privilegeHelper, AuthService authService) {
		this.supplierPaymentRepository = supplierPaymentRepository;
		this.grnRepository = grnRepository;
		this.grnStatusRepository = grnStatusRepository;
		this.grnSupplierPaymentRepository = grnSupplierPaymentRepository;
		this.privilegeHelper = privilegeHelper;
		this.authService = authService;
	}

	@Override
	public List<SupplierPayment> findAll() {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			if (authService.isLoggedUserHasRole("Cashier")) {
				return supplierPaymentRepository.findAllByUser(authService.getLoggedUser().getId());
			} else {
				return supplierPaymentRepository.findAll(Sort.by(Direction.DESC, "id"));
			}
		} else {
			return null;
		}
	}

	@Override
	public List<SupplierPayment> findAllByUser(int userId) {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return supplierPaymentRepository.findAllByUser(userId);
		} else {
			return null;
		}
	}

	@Override
	public List<GrnHasSupplierPayment> findGrnPaymentsBySupplierPayment(int supplierPaymentId) {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return grnSupplierPaymentRepository.findGrnPaymentsBySupplierPayment(supplierPaymentId);
		} else {
			return null;
		}
	}

	@Override
	@Transactional
	public String saveSupplierPayment(SupplierPayment supplierPayment) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}
		try {
			// get logged user id
			int loggedUserId = authService.getLoggedUser().getId();
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
			List<Grn> incompleteGrns = grnRepository
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
