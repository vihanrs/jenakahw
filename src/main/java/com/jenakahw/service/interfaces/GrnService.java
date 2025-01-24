package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.Grn;

public interface GrnService {
	List<Grn> findAll();
    List<Grn> findAllIncompleteBySupplier(int supplierId);
    Grn findByGrnId(Integer grnId);
    String saveGrn(Grn grn);
    String updateGrn(Grn grn);
}
