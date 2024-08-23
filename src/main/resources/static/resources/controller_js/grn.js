//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest("/privilege/byloggeduserandmodule/GRN");
  manageNavBar();
  //refresh all
  refreshAll();

  //set default selected section
  if (userPrivilages.insert) {
    showDefaultSection("addNewButton", "addNewSection");
  } else {
    showDefaultSection("viewAllButton", "viewAllSection");
    addAccordion.style.display = "none";
  }
  //call all event listners
  addEventListeners();
});

// ********* LISTENERS *********
const addEventListeners = () => {
  let numberWithdecimals = "^(([1-9]{1}[0-9]{0,7})|([0-9]{0,8}[.][0-9]{2}))$";
  let qtyPattern =
    "^(([1-9]{1}[0-9]{0,7})|([0-9]{1}[.][0-9]{1,3})|([1-9]{1}[0-9]{0,7}[.][0-9]{1,3}))$";

  selectPOID.addEventListener("change", () => {
    selectDFieldValidator(selectPOID, "grn", "purchaseOrderId"),
      clearPreviousProducts(),
      getSupplierByPO(grn.purchaseOrderId.supplierId),
      getProductListByPO(grn.purchaseOrderId.id);
  });

  textSupplierInvNo.addEventListener("keyup", () => {
    textFieldValidator(textSupplierInvNo, "", "grn", "supplierInvId");
  });

  textNote.addEventListener("keyup", () => {
    textFieldValidator(textNote, "^.*$", "grn", "note");
  });

  selectGRNStatus.addEventListener("change", () => {
    selectDFieldValidator(selectGRNStatus, "grn", "grnStatusId");
  });

  selectProduct.addEventListener("change", () => {
    selectDFieldValidator(selectProduct, "grnProduct", "productId"),
      setUnitType(grnProduct.productId),
      setPOValuesForSelectedProduct(
        grn.purchaseOrderId.id,
        grnProduct.productId.id
      );
  });

  textCostPrice.addEventListener("keyup", () => {
    textFieldValidator(
      textCostPrice,
      numberWithdecimals,
      "grnProduct",
      "costPrice"
    ),
      calLineAmountAndSellPrice();
  });

  textQty.addEventListener("keyup", () => {
    qtyFieldValidator(textQty, qtyPattern, "grnProduct", "qty", maxQty),
      calLineAmountAndSellPrice();
  });

  textSellPrice.addEventListener("keyup", () => {
    textFieldValidator(
      textSellPrice,
      numberWithdecimals,
      "grnProduct",
      "sellPrice"
    );
  });

  textLineAmount.addEventListener("keyup", () => {
    textFieldValidator(
      textLineAmount,
      numberWithdecimals,
      "grnProduct",
      "lineAmount"
    );
  });

  textItemCount.addEventListener("keyup", () => {
    textFieldValidator(textItemCount, numberWithdecimals, "grn", "itemCount");
  });

  textTotalAmount.addEventListener("keyup", () => {
    textFieldValidator(textTotalAmount, numberWithdecimals, "grn", "total");
  });

  discountPrecentageCheck.addEventListener("change", () => {
    discountValidator(
      textDiscount,
      textTotalAmount,
      discountPrecentageCheck,
      "grn",
      "discount"
    ),
      calGrandTotal();
  });

  textDiscount.addEventListener("keyup", () => {
    discountValidator(
      textDiscount,
      textTotalAmount,
      discountPrecentageCheck,
      "grn",
      "discount"
    ),
      calGrandTotal();
  });

  textGrandTotal.addEventListener("keyup", () => {
    textFieldValidator(textGrandTotal, numberWithdecimals, "grn", "grandTotal");
  });

  textPaid.addEventListener("keyup", () => {
    textFieldValidator(textPaid, numberWithdecimals, "grn", "paid");
  });

  btnAddProduct.addEventListener("click", () => {
    addProduct();
  });

  btnProductReset.addEventListener("click", () => {
    clearProductSelect();
  });

  //form reset button function call
  btnReset.addEventListener("click", () => {
    refreshForm();
  });

  //record update function call
  btnUpdate.addEventListener("click", () => {
    updateRecord();
  });

  //record save function call
  btnAdd.addEventListener("click", () => {
    addRecord();
  });

  //record print function call
  btnViewPrint.addEventListener("click", () => {
    printViewRecord();
  });

  //record print function call
  btnPrintFullTable.addEventListener("click", () => {
    printFullTable();
  });
};

// ********* RESET *********
//function for refresh form and table
const refreshAll = () => {
  //Call form refresh function
  refreshForm();
  //Call table refresh function
  refreshTable();
};

// ********* MAIN FORM OPERATIONS *********

//function for refresh form area
const refreshForm = () => {
  //create empty object
  grn = {};
  grn.paid = 0;

  //create new array for products
  grn.grnHasProducts = [];

  //get data list of 'Requested' purchase orders
  purchaseOrders = ajaxGetRequest("/purchaseorder/getpobystatus/1");
  fillDataIntoSelect(selectPOID, "Select PO ID", purchaseOrders, "poCode");

  // get grn status
  grnStatuses = ajaxGetRequest("/grnstatus/findall");
  fillDataIntoSelect(
    selectGRNStatus,
    "Select Status",
    grnStatuses,
    "name",
    "Received"
  );

  statusDiv.classList.add("d-none");
  divPaidAmount.classList.add("d-none");

  //bind default selected status in to supplier object and set valid color
  grn.grnStatusId = JSON.parse(selectGRNStatus.value);
  selectGRNStatus.style.border = "2px solid #00FF7F";

  //empty all elements
  selectPOID.value = "";
  selectPOID.disabled = false;
  textSupplier.value = "";
  textSupplierInvNo.value = "";
  textNote.value = "";
  textItemCount.value = "";
  textTotalAmount.value = "";
  textDiscount.value = "";
  textGrandTotal.value = "";
  textPaid.value = "";
  dicountPrecentageView.innerText = "";
  discountPrecentageCheck.checked = false;
  discountPrecentageCheck.disabled = false;
  textDiscount.disabled = false;

  // //set default border color
  let elements = [
    selectPOID,
    textSupplier,
    textSupplierInvNo,
    textNote,
    textItemCount,
    textTotalAmount,
    textDiscount,
    textGrandTotal,
    textPaid,
  ];
  setBorderStyle(elements);

  //manage form buttons
  manageFormButtons("insert", userPrivilages);

  // //Call inner form and table refresh function
  refreshInnerFormAndTable();

  //clear product list
  getProductListByPO(0);
};

//function for get product list related to the selected supplier
const getProductListByPO = (purchaseOrderId) => {
  products = ajaxGetRequest(
    "/purchaseorder/findpoproductsbypoid/" + purchaseOrderId
  );

  // add barcode and name as values in poHasProducts object
  // poHasProducts = poHasProducts.map((poHasProduct) => ({
  //   barcode: poHasProduct.productId.barcode,
  //   name: poHasProduct.productId.name,
  //   profitRate: poHasProduct.productId.profitRate,
  //   ...poHasProduct,
  // }));

  fillMoreDataIntoSelect(
    selectProduct,
    "Select Product",
    products,
    "barcode",
    "name"
  );
  textUnitType.innerText = "";
};

// function for fill supplier when selecting po id
const getSupplierByPO = (supplier) => {
  textSupplier.value =
    supplier.firstName +
    (supplier.company != null ? " - " + supplier.company : "");

  grn.supplierId = supplier.id;
  textSupplier.style.border = "2px solid #00FF7F";
};

//function for check errors
const checkErrors = () => {
  //need to check all required prperty filds
  let error = "";

  if (grn.purchaseOrderId == null) {
    error = error + "Please Select Valid Purchase Order...!\n";
    selectPOID.style.border = "1px solid red";
  }

  if (grn.grnStatusId == null) {
    error = error + "Please Select Valid Status...!\n";
    selectPOStatus.style.border = "1px solid red";
  }

  if (grn.grnHasProducts.length == 0) {
    error = error + "Please Select Products...!\n";
  }

  return error;
};

//function for check updates
const checkUpdates = () => {
  let updates = "";

  if (oldGrn.supplierInvId != grn.supplierInvId) {
    updates +=
      "Supplier Invoice No. has changed " +
      (oldGrn.supplierInvId ?? "-") +
      " into " +
      (grn.supplierInvId ?? "-") +
      " \n";
  }

  if (oldGrn.grnStatusId.id != grn.grnStatusId.id) {
    updates +=
      "GRN Status has changed " +
      oldGrn.grnStatusId.name +
      " into " +
      grn.grnStatusId.name +
      " \n";
  }

  if (oldGrn.note != grn.note) {
    updates +=
      "Note has changed " +
      (oldGrn.note ?? "-") +
      " into " +
      (grn.note ?? "-") +
      " \n";
  }

  if (oldGrn.total != grn.total) {
    updates +=
      "Total Amount has changed Rs." +
      oldGrn.total +
      " into Rs." +
      grn.total +
      " \n";
  }

  if (oldGrn.discount != grn.discount) {
    updates +=
      "Discount Amount has changed Rs." +
      (oldGrn.discount ?? "-") +
      " into Rs." +
      (grn.discount ?? "-") +
      " \n";
  }

  if (oldGrn.grandTotal != grn.grandTotal) {
    updates +=
      "Products Updated\n" +
      "Grand Total Amount has changed Rs." +
      oldGrn.grandTotal +
      " into Rs." +
      grn.grandTotal +
      " \n";
  }

  return updates;
};

//function for add record
const addRecord = () => {
  //check form errors -
  let formErrors = checkErrors();
  if (formErrors == "") {
    //get user confirmation
    let title = "Are you sure to add following record..?\n";
    let message =
      "Supplier : " +
      grn.purchaseOrderId.supplierId.firstName +
      (grn.supplierId.company != null ? " - " + grn.supplierId.company : "") +
      "\nPO ID : " +
      grn.purchaseOrderId.poCode +
      "\nItem Count : " +
      grn.itemCount +
      "\nTotal Amount (Rs.) : " +
      grn.total +
      "\nDiscount Amount (Rs.) : " +
      (grn.discount != null ? grn.discount : "0.00") +
      "\nGrand Total (Rs.) : " +
      grn.grandTotal;
    showConfirm(title, message).then((userConfirm) => {
      if (userConfirm) {
        //pass data into back end
        let serverResponse = ajaxRequestBody("/grn", "POST", grn); // url,method,object

        //check back end response
        if (serverResponse == "OK") {
          showAlert("success", "GRN Save successfully..! ");
          //need to refresh table and form
          refreshAll();
        } else {
          showAlert(
            "error",
            "GRN save not successfully..! have some errors \n" + serverResponse
          );
        }
      }
    });
  } else {
    showAlert("error", formErrors);
  }
};

//function for update record
const updateRecord = () => {
  let errors = checkErrors();
  if (errors == "") {
    let updates = checkUpdates();
    if (updates != "") {
      let title = "Are you sure you want to update following changes...?";
      let message = updates;
      showConfirm(title, message).then((userConfirm) => {
        if (userConfirm) {
          let updateServiceResponse = ajaxRequestBody("/grn", "PUT", grn);
          if (updateServiceResponse == "OK") {
            showAlert("success", "GRN Update successfully..!").then(() => {
              //need to refresh table and form
              refreshAll();
            });
          } else {
            showAlert(
              "error",
              "GRN update not successfully..! have some errors \n" +
                updateServiceResponse
            );
          }
        }
      });
    } else {
      showAlert("warning", "Nothing to Update...!");
    }
  } else {
    showAlert("error", "Cannot update!!!\n\n" + formErrors);
  }
};

// ********* INNER FORM/TABLE OPERATIONS *********

//function for refresh inner product form/table area
const refreshInnerFormAndTable = () => {
  grnProduct = {};
  maxQty = 0;

  //empty all elements
  textCostPrice.value = "";
  textQty.value = "";
  textSellPrice.value = "";
  textLineAmount.value = "";
  selectProduct.value = "";
  textUnitType.innerText = "";
  lblCostPrice.innerText = "";
  lblQty.innerText = "";
  refillProductRowId = null;
  selectProduct.disabled = false;
  innerFormTable.classList.remove("d-none");

  //set default border color
  let elements = [
    selectProduct,
    textCostPrice,
    textQty,
    textSellPrice,
    textLineAmount,
  ];
  setBorderStyle(elements);

  const displayProperties = [
    { property: getProductName, datatype: "function" },
    { property: "costPrice", datatype: "currency" },
    { property: "qty", datatype: "String" },
    { property: "sellPrice", datatype: "currency" },
    { property: "lineAmount", datatype: "currency" },
  ];

  //call the function (tableID,dataList,display property list,refill function name, delete function name, button visibilitys)
  fillDataIntoInnerTable(
    productsTable,
    grn.grnHasProducts,
    displayProperties,
    refillProduct,
    deleteProduct
  );

  calculateGRNTotal();
  getItemCount();
  calGrandTotal();
};

//clear previously tabel added products and refresh when selecting another po id
const clearPreviousProducts = () => {
  grn.grnHasProducts = [];
  refreshInnerFormAndTable();
};

// function for get product name and barcode
const getProductName = (rowObject) => {
  return rowObject.productId.barcode + "-" + rowObject.productId.name;
};

//function for caluclate total amount
const calculateGRNTotal = () => {
  let grnTotal = 0;
  grn.grnHasProducts.forEach((product) => {
    grnTotal += parseFloat(product.lineAmount);
  });

  //bind value to totalAmount
  grn.total = parseFloat(grnTotal).toFixed(2);

  textTotalAmount.value = grn.total;
};

// function for get total item count
const getItemCount = () => {
  let count = grn.grnHasProducts.length;
  textItemCount.value = count;
  grn.itemCount = count;
};

// function for calculate grand total
const calGrandTotal = () => {
  let discount = grn.discount != null ? grn.discount : 0;

  grn.grandTotal = parseFloat(grn.total - discount).toFixed(2);
  textGrandTotal.value = grn.grandTotal;
  grn.balanceAmount = grn.grandTotal;

  //set precentage label value
  dicountPrecentageView.innerText = discountPrecentageCheck.checked
    ? "Rs." + parseFloat(discount).toFixed(2)
    : calDiscountPrecentage(grn.total, discount) + "%";

  if (textPaid.value != "" && parseFloat(textPaid.value) != 0) {
    paidAmount = textPaid.value;
    // need to do
  }
};

// function for get unit type for selected product
const setUnitType = (product) => {
  textUnitType.innerText = "(" + product.unitTypeId.name + ")";
};

// function for set purchase order values as hints
const setPOValuesForSelectedProduct = (
  purchaseOrderId,
  productId,
  isRefill = false
) => {
  poValues = ajaxGetRequest(
    "/purchaseorder/findpohasproductbypoidandproductid/" +
      purchaseOrderId +
      "/" +
      productId
  );

  if (!isRefill) {
    textCostPrice.value = poValues.purchasePrice;
    textQty.value = poValues.qty;
    grnProduct.qty = poValues.qty;
    grnProduct.costPrice = poValues.purchasePrice;
  }
  //set label values
  lblCostPrice.innerText =
    " Rs." + parseFloat(poValues.purchasePrice).toFixed(2);
  lblQty.innerText = " " + poValues.qty;
  // set max qty limit
  maxQty = poValues.qty;

  calLineAmountAndSellPrice();
};

// function for calculate line amount
const calLineAmountAndSellPrice = () => {
  //calculate line amount
  grnProduct.lineAmount =
    grnProduct.costPrice != null && grnProduct.qty != null
      ? parseFloat(grnProduct.costPrice) * parseFloat(grnProduct.qty)
      : 0;

  //display line amount
  textLineAmount.value =
    grnProduct.lineAmount != 0
      ? parseFloat(grnProduct.lineAmount).toFixed(2)
      : "";

  //cal sell price by profit rate
  grnProduct.sellPrice =
    grnProduct.costPrice != null && grnProduct.qty != null
      ? (parseFloat(grnProduct.costPrice) *
          (100 + parseFloat(grnProduct.productId.profitRate))) /
        100
      : 0;

  // display sell price
  textSellPrice.value =
    grnProduct.sellPrice != 0
      ? parseFloat(grnProduct.sellPrice).toFixed(2)
      : "";
};

//function for check inner form errors
const checkInnerFormErrors = () => {
  let error = "";

  if (grnProduct.productId == null) {
    error = error + "Please Select Product...!\n";
    selectProduct.style.border = "1px solid red";
  }

  if (grnProduct.costPrice == null) {
    error = error + "Please Enter Valid Cost Price...!\n";
    textCostPrice.style.border = "1px solid red";
  }

  if (grnProduct.qty == null) {
    error = error + "Please Enter Valid Qty...!\n";
    textQty.style.border = "1px solid red";
  }

  return error;
};

// fucntion for add product to inner table
const addProduct = () => {
  // check errors
  let formErrors = checkInnerFormErrors();
  if (formErrors == "") {
    // get user confirmation
    let title = "Are you sure to add following product..?";
    let message =
      "Product Name : " +
      grnProduct.productId.name +
      "\nPurchase Price : " +
      grnProduct.costPrice +
      "\nQty : " +
      grnProduct.qty;

    showConfirm(title, message).then((userConfirm) => {
      if (userConfirm) {
        if (refillProductRowId != null) {
          // update object in array
          updateProduct(grnProduct);
        } else {
          //add object into array
          grn.grnHasProducts.push(grnProduct);
        }
        refreshInnerFormAndTable();
        refreshRemainProductList();
      }
    });
  } else {
    showAlert("error", formErrors);
  }
};

// function for reset update refilled product
const updateProduct = (updatedProduct) => {
  grn.grnHasProducts[refillProductRowId].costPrice = updatedProduct.costPrice;

  grn.grnHasProducts[refillProductRowId].qty = updatedProduct.qty;
  grn.grnHasProducts[refillProductRowId].sellPrice = updatedProduct.sellPrice;
  grn.grnHasProducts[refillProductRowId].lineAmount = updatedProduct.lineAmount;

  refreshInnerFormAndTable();
};

// function for reset product selection data
const clearProductSelect = () => {
  if (refillProductRowId != null) {
    products = products.filter(
      (product) =>
        product.id != grn.grnHasProducts[refillProductRowId].productId.id
    );
  }

  refreshInnerFormAndTable();
  refreshRemainProductList();
};

//refresh products to remove already added products from dropdown
const refreshRemainProductList = (selectedProduct, refillProduct = null) => {
  // filter products already in purchaseOrder.poHasProducts
  products = products.filter(
    (product) =>
      !grn.grnHasProducts.some(
        (extProduct) => extProduct.productId.id == product.id
      )
  );
  //some Method: Checks if at least one element in purchaseOrder.poHasProducts has a productId.id that matches product.id.
  //If some returns true, it means the product is already selected (some returns false when no matches are found).

  if (refillProduct != null) {
    products.push(refillProduct);
  }

  //update dropdown with new product list
  fillMoreDataIntoSelect(
    selectProduct,
    "Select Product",
    products,
    "barcode",
    "name",
    selectedProduct
  );
};

// function for refill product
const refillProduct = (rowObject, rowId) => {
  //remove refilled product from grn.grnHasProducts
  // grn.grnHasProducts = grn.grnHasProducts.filter(
  //   (product) => product.productId.id != rowObject.productId.id
  // );

  // refresh inner table
  // refreshInnerFormAndTable();

  refillProductRowId = rowId;
  selectProduct.disabled = true;

  //fill product data into relavent fields
  grnProduct = JSON.parse(JSON.stringify(rowObject));
  textCostPrice.value = parseFloat(grnProduct.costPrice).toFixed(2);
  textSellPrice.value = parseFloat(grnProduct.sellPrice).toFixed(2);
  textQty.value = parseFloat(grnProduct.qty).toFixed(2);
  textLineAmount.value = parseFloat(grnProduct.lineAmount).toFixed(2);

  //set valid border color
  let elements = [selectProduct, textCostPrice, textSellPrice, textQty];
  setBorderStyle(elements, "2px solid #00FF7F");

  //update product list to add refiled product again
  refreshRemainProductList(grnProduct.productId.barcode, grnProduct.productId);
  setPOValuesForSelectedProduct(
    grn.purchaseOrderId.id,
    grnProduct.productId.id,
    true
  );
};

//function for delete selected product
const deleteProduct = (rowObject, rowId) => {
  // get user confirmation
  let title = "Are you sure you want to delete this product...?\n";
  let message = rowObject.productId.barcode + " - " + rowObject.productId.name;

  showConfirm(title, message).then((userConfirm) => {
    if (userConfirm) {
      //remove deleted product from grn.grnHasProducts
      grn.grnHasProducts = grn.grnHasProducts.filter(
        (product) => product.productId.id != rowObject.productId.id
      );

      // refresh inner table
      refreshInnerFormAndTable();

      //update product list to add deleted product again
      refreshRemainProductList();
    }
  });
};

// ********* TABLE OPERATIONS *********

//function for refresh table records
const refreshTable = () => {
  //array for store data list
  grns = ajaxGetRequest("/grn/findall");

  //object count = table column count
  //String - number/string/date
  //function - object/array/boolean
  //currency - RS
  const displayProperties = [
    { property: "grnCode", datatype: "String" },
    { property: getSupplier, datatype: "function" },
    { property: getPurchaseOrderId, datatype: "function" },
    { property: getAddedDate, datatype: "function" },
    { property: "itemCount", datatype: "String" },
    { property: "grandTotal", datatype: "currency" },
    { property: getStatus, datatype: "function" },
  ];

  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys, user privileges)
  fillDataIntoTable(
    grnTable,
    grns,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    true,
    userPrivilages
  );

  //hide delete button when status is 'deleted'
  grns.forEach((grn, index) => {
    if (
      userPrivilages.delete &&
      (grn.grnStatusId.name == "Deleted" ||
        grn.grnStatusId.name == "Received" ||
        grn.grnStatusId.name == "Completed")
    ) {
      //catch the button
      let targetElement =
        grnTable.children[1].children[index].children[8].children[
          userPrivilages.update && userPrivilages.insert ? 2 : 1
        ];
      //add changes
      targetElement.style.pointerEvents = "none";
      targetElement.style.display = "none";
      targetElement.style.visibility = "hidden";
    }

    if (userPrivilages.update && grn.grnStatusId.name == "Completed") {
      //catch the button
      let targetElement =
        grnTable.children[1].children[index].children[8].children[1];
      //add changes
      targetElement.style.pointerEvents = "none";
      targetElement.style.visibility = "hidden";
    }
  });

  $("#grnTable").dataTable();
};

// function for get supplier
const getSupplier = (rowObject) => {
  return (
    rowObject.purchaseOrderId.supplierId.firstName +
    (rowObject.purchaseOrderId.supplierId.company != null
      ? " - " + rowObject.purchaseOrderId.supplierId.company
      : "")
  );
};

// function for get purchase order code
const getPurchaseOrderId = (rowObject) => {
  return rowObject.purchaseOrderId.poCode;
};

// function for get grn added date
const getAddedDate = (rowObject) => {
  return rowObject.addedDateTime.substring(0, 10);
};

// function for get status
const getStatus = (rowObject) => {
  if (rowObject.grnStatusId.name == "Completed") {
    return (
      '<p class = "status status-active">' + rowObject.grnStatusId.name + "</p>"
    );
  } else if (rowObject.grnStatusId.name == "Deleted") {
    return (
      '<p class = "status status-error">' + rowObject.grnStatusId.name + "</p>"
    );
  } else if (rowObject.grnStatusId.name == "Received") {
    return (
      '<p class = "status status-warning">' +
      rowObject.grnStatusId.name +
      "</p>"
    );
  }
};

// function for refill grn data
const refillRecord = (rowObject, rowId) => {
  $("#addNewButton").click();
  //get selected grn data
  grn = ajaxGetRequest("/grn/findbyid/" + rowObject.id);
  oldGrn = JSON.parse(JSON.stringify(grn));

  // selectPOID.value = grn.purchaseOrderId.poCode;
  textSupplierInvNo.value = grn.supplierInvId != null ? grn.supplierInvId : "";
  textNote.value = grn.note != null ? grn.note : "";
  textDiscount.value =
    grn.discount != null ? parseFloat(grn.discount).toFixed(2) : "";
  dicountPrecentageView.innerText =
    grn.discount != null
      ? calDiscountPrecentage(grn.total, grn.discount) + "%"
      : "";
  textPaid.value = grn.paid;

  // if some payment already done then cannot change the discount
  if (grn.paid != 0) {
    discountPrecentageCheck.disabled = true;
    textDiscount.disabled = true;
  } else {
    discountPrecentageCheck.disabled = false;
    textDiscount.disabled = false;
  }
  textSupplier.value =
    grn.purchaseOrderId.supplierId.firstName +
    (grn.purchaseOrderId.supplierId.company != null
      ? " - " + grn.purchaseOrderId.supplierId.company
      : "");

  // disable changing POID
  selectPOID.disabled = true;

  fillDataIntoSelect(
    selectGRNStatus,
    "Select Status",
    grnStatuses,
    "name",
    grn.grnStatusId.name
  );

  //add selected grn pocode to the dropdown
  purchaseOrders.push(grn.purchaseOrderId);

  fillDataIntoSelect(
    selectPOID,
    "Select Purcahse Order ID",
    purchaseOrders,
    "poCode",
    grn.purchaseOrderId.poCode
  );

  statusDiv.classList.remove("d-none");
  divPaidAmount.classList.remove("d-none");
  //refresh inner form and table to get saved products from grn.grnHasProducts
  refreshInnerFormAndTable();

  //get purchase order product list and then remove alrady added products
  getProductListByPO(grn.purchaseOrderId.id);
  refreshRemainProductList();

  setBorderStyle([
    selectPOID,
    textSupplier,
    textSupplierInvNo,
    textNote,
    textItemCount,
    textTotalAmount,
    textDiscount,
    textGrandTotal,
    textPaid,
    selectGRNStatus,
  ]);

  //hide innerform buttons
  innerFormTable.classList.add("d-none");

  //hide inner table buttons when refill

  for (i = 0; i < grn.grnHasProducts.length; i++) {
    let targetElement0 =
      productsTable.children[1].children[i].children[6].children[0];
    let targetElement =
      productsTable.children[1].children[i].children[6].children[1];
    //add changes
    targetElement0.style.pointerEvents = "none";
    targetElement0.style.visibility = "hidden";
    targetElement.style.pointerEvents = "none";
    targetElement.style.visibility = "hidden";
  }

  //manage buttons
  manageFormButtons("refill", userPrivilages);
};

// function for calculate discount percentage from discount amount
const calDiscountPrecentage = (total, discount) => {
  let percentage = 0;
  if (total == 0) {
    return percentage;
  }
  percentage = (discount / total) * 100;
  return parseFloat(percentage).toFixed(2);
};
// //function for delete record
const deleteRecord = (rowObject, rowId) => {
  //get user confirmation
  // let title = "Are you sure!\nYou wants to delete following record? \n";
  // let message =
  //   "GRN : " +
  //   rowObject.grnCode +
  //   "\n" +
  //   "Supplier : " +
  //   rowObject.purchaseOrderId.supplierId.firstName +
  //   "\n" +
  //   "Item Count : " +
  //   rowObject.itemCount +
  //   "\n" +
  //   "Grand Total (Rs.) : " +
  //   rowObject.grandTotal;
  // showConfirm(title, message).then((userConfirm) => {
  //   if (userConfirm) {
  //     //response from backend ...
  //     let serverResponse = ajaxRequestBody("/grn", "DELETE", rowObject); // url,method,object
  //     //check back end response
  //     if (serverResponse == "OK") {
  //       showAlert("success", "Delete successfully..!").then(() => {
  //         // Need to refresh table and form
  //         refreshAll();
  //       });
  //     } else {
  //       showAlert(
  //         "error",
  //         "Delete not successfully..! There were some errors \n" +
  //           serverResponse
  //       );
  //     }
  //   }
  // });
};

//function for view record
const viewRecord = (rowObject, rowId) => {
  //need to get full object
  let printObj = rowObject;

  tdGRNID.innerText = printObj.grnCode;
  tdPOID.innerText = printObj.purchaseOrderId.poCode;
  tdSupplier.innerText =
    printObj.purchaseOrderId.supplierId.firstName +
      " " +
      printObj.purchaseOrderId.supplierId.company ?? "";
  tdItemCount.innerText = printObj.itemCount;
  tdTotal.innerText = "Rs." + parseFloat(printObj.total).toFixed(2);
  tdDiscount.innerText =
    printObj.discount != null
      ? "Rs." + parseFloat(printObj.discount).toFixed(2)
      : "Rs.0.00";
  tdGrandTotal.innerText = "Rs." + parseFloat(printObj.grandTotal).toFixed(2);
  tdStatus.innerText = printObj.grnStatusId.name;
  tdCreatedDate.innerText = printObj.addedDateTime.split("T")[0];
  tdPaid.innerText = "Rs." + parseFloat(printObj.paid).toFixed(2);
  getGRNProductsForPrint(printObj);
  //open model
  $("#modelDetailedView").modal("show");
};

// funtion for get purchase order product list for print
const getGRNProductsForPrint = (printObj) => {
  // remove the previously added dynamic rows
  document.querySelectorAll(".dynamic-row").forEach((row) => row.remove());

  printObj.grnHasProducts.forEach((ele) => {
    const tr = document.createElement("tr");
    tr.classList.add("dynamic-row");
    const tdProductName = document.createElement("td");
    const tdPurchasePrice = document.createElement("td");
    const tdQty = document.createElement("td");
    const tdSellPrice = document.createElement("td");
    const tdLineAmount = document.createElement("td");

    tdProductName.innerText =
      ele.productId.brandId.name + " - " + ele.productId.name;
    tdPurchasePrice.innerText = "Rs." + parseFloat(ele.costPrice).toFixed(2);
    tdQty.innerText = ele.qty + " (" + ele.productId.unitTypeId.name + ")";
    tdSellPrice.innerText = "Rs." + parseFloat(ele.sellPrice).toFixed(2);
    tdLineAmount.innerText = "Rs." + parseFloat(ele.lineAmount).toFixed(2);

    tr.appendChild(tdProductName);
    tr.appendChild(tdPurchasePrice);
    tr.appendChild(tdQty);
    tr.appendChild(tdSellPrice);
    tr.appendChild(tdLineAmount);
    printTable.appendChild(tr);
  });
};

// ********* PRINT OPERATIONS *********

//print function
const printViewRecord = () => {
  newTab = window.open();
  newTab.document.write(
    //  link bootstrap css
    "<head><title>GRN Details</title>" +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
      "<h2 style = 'font-weight:bold'>GRN Details</h2>" +
      printView.outerHTML
  );

  //triger print() after 1000 milsec time out
  setTimeout(function () {
    newTab.print();
  }, 1000);
};

//print all data table after 1000 milsec of new tab opening () - to refresh the new tab elements
const printFullTable = () => {
  const newTab = window.open();
  newTab.document.write(
    //  link bootstrap css
    "<head><title>Print GRNs</title>" +
      '<script src="resources/js/jquery.js"></script>' +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" />' +
      '<link rel="stylesheet" href="resources/css/common.css" /></head>' +
      "<h2 style = 'font-weight:bold'>GRNs Details</h2>" +
      grnTable.outerHTML +
      '<script>$("#modifyButtons").css("display","none");$(".table-buttons").hide();</script>'
  );

  setTimeout(function () {
    newTab.print();
  }, 1000);
};
