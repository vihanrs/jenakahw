package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.Invoice;

public interface InvoiceService {

    List<Invoice> findAll();

    List<Invoice> findAllInDateRange(String fromDate, String toDate);

    List<Invoice> findByStatus(String status);

    List<Invoice> findByCustomerAndIncomplete(int customerId);

    Invoice findByInvoiceId(String invoiceId);

    String saveInvoice(Invoice invoice);

    String deleteInvoice(Invoice invoice);
}
