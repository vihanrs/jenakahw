//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest("/privilege/byloggeduserandmodule/GRN");

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

  textPOID.addEventListener("keyup", () => {
    textFieldValidator(textPOID, "^[P][O][0-9]{9}$", "grn", "purchaseOrderId");
  });

  selectSupplier.addEventListener("change", () => {
    selectDFieldValidator(selectSupplier, "grn", "supplierId"),
      getProductListBySupplier(grn.supplierId.id),
      clearPreviousProducts();
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
      setUnitType(grnProduct.productId);
  });

  textCostPrice.addEventListener("keyup", () => {
    textFieldValidator(
      textCostPrice,
      numberWithdecimals,
      "grnProduct",
      "costPrice"
    ),
      calLineAmount();
  });

  textQty.addEventListener("keyup", () => {
    textFieldValidator(
      textQty,
      "^(([1-9]{1}[0-9]{0,7})|([0-9]{0,8}[.][0-9]{1,2}))$",
      "grnProduct",
      "qty"
    ),
      calLineAmount();
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
  textDiscount.addEventListener("keyup", () => {
    textFieldValidator(textDiscount, numberWithdecimals, "grn", "discount"),
      calGrandTotal();
  });
  textGrandTotal.addEventListener("keyup", () => {
    textFieldValidator(textGrandTotal, numberWithdecimals, "grn", "grandTotal");
  });
  textPaid.addEventListener("keyup", () => {
    textFieldValidator(textPaid, numberWithdecimals, "grn", "paid");
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
    // printViewRecord();
  });

  btnAddProduct.addEventListener("click", () => {
    addProduct();
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

  //get data list from select element
  suppliers = ajaxGetRequest("/supplier/findactivesuppliers");
  fillMoreDataIntoSelect(
    selectSupplier,
    "Select Supplier",
    suppliers,
    "firstName",
    "company"
  );
  selectSupplier.disabled = false;

  // get grn status
  grnStatuses = ajaxGetRequest("/grnstatus/findall");
  fillDataIntoSelect(
    selectGRNStatus,
    "Select Status",
    grnStatuses,
    "name",
    "Received"
  );

  //bind default selected status in to supplier object and set valid color
  grn.grnStatusId = JSON.parse(selectGRNStatus.value);
  selectGRNStatus.style.border = "2px solid #00FF7F";

  //empty all elements
  textPOID.value = "";
  textSupplierInvNo.value = "";
  textNote.value = "";
  textItemCount.value = "";
  textTotalAmount.value = "";
  textDiscount.value = "";
  textGrandTotal.value = "";
  textPaid.value = "";

  // //set default border color
  let elements = [
    textPOID,
    textSupplierInvNo,
    textNote,
    textItemCount,
    textTotalAmount,
    textDiscount,
    textGrandTotal,
    textPaid,
    selectSupplier,
  ];
  setBorderStyle(elements);

  //manage form buttons
  manageFormButtons("insert", userPrivilages);

  // //Call inner form and table refresh function
  refreshInnerFormAndTable();

  //clear product list
  getProductListBySupplier(0);
};

// function for calculate grand total
const calGrandTotal = () => {
  let discount = grn.discount != null ? grn.discount : 0;
  grn.grandTotal = parseFloat(grn.total - discount).toFixed(2);
  textGrandTotal.value = grn.grandTotal;
};

//function for check errors
const checkErrors = () => {
  //need to check all required prperty filds
  let error = "";

  if (grn.supplierId == null) {
    error = error + "Please Select Valid Supplier...!\n";
    selectSupplier.style.border = "1px solid red";
  }

  if (grn.grnStatusId == null) {
    error = error + "Please Select Valid Status...!\n";
    selectPOStatus.style.border = "1px solid red";
  }

  if (parseFloat(grn.totalAmount).toFixed(2) == 0.0) {
    error = error + "Please Select Products...!\n";
  }

  return error;
};

//function for check updates
const checkUpdates = () => {
  let updates = "";

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
      "Products Updated...!\nTotal Amount has changed Rs." +
      oldGrn.total +
      " into Rs." +
      grn.total +
      " \n";
  }

  if (oldGrn.discount != grn.discount) {
    updates +=
      "Discount Amount has changed Rs." +
      oldGrn.discount +
      " into Rs." +
      grn.discount +
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
      grn.supplierId.firstName +
      (grn.supplierId.company != null ? " - " + grn.supplierId.company : "") +
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
          showAlert("success", "Save sucessfully..! " + serverResponse);
          //need to refresh table and form
          refreshAll();
        } else {
          showAlert(
            "error",
            "Save not sucessfully..! have some errors \n" + serverResponse
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
          let updateServiceResponse = ajaxRequestBody(
            "/grn",
            "PUT",
            purchaseOrder
          );
          if (updateServiceResponse == "OK") {
            showAlert("success", "Update sucessfully..!").then(() => {
              //need to refresh table and form
              refreshAll();
            });
          } else {
            showAlert(
              "error",
              "Update not sucessfully..! have some errors \n" +
                updateSeriveResponse
            ).then(() => {});
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

  //empty all elements
  textCostPrice.value = "";
  textQty.value = "";
  textSellPrice.value = "";
  textLineAmount.value = "";
  selectProduct.value = "";
  textUnitType.innerText = "";

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

//function for get product list related to the selected supplier
const getProductListBySupplier = (supplierId) => {
  products = ajaxGetRequest("/product/availablelistWithSupplier/" + supplierId);
  fillMoreDataIntoSelect(
    selectProduct,
    "Select Product",
    products,
    "barcode",
    "name"
  );
  textUnitType.innerText = "";
};

// function for get unit type for selected product
const setUnitType = (product) => {
  textUnitType.innerText = "(" + product.unitTypeId.name + ")";
};

//clear previously tabel added products and refresh when selecting another supplier
const clearPreviousProducts = () => {
  grn.grnHasProducts = [];
  refreshInnerFormAndTable();
};

// function for calculate line amount
const calLineAmount = () => {
  //calculate line amount
  grnProduct.lineAmount =
    grnProduct.costPrice != null && grnProduct.qty != null
      ? parseFloat(grnProduct.costPrice).toFixed(2) *
        parseFloat(grnProduct.qty).toFixed(2)
      : 0;

  //display line amount
  textLineAmount.value =
    grnProduct.lineAmount != 0
      ? parseFloat(grnProduct.lineAmount).toFixed(2)
      : "";
};

// function for get product name and barcode
const getProductName = (rowObject) => {
  return rowObject.productId.barcode + "-" + rowObject.productId.name;
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
        //add object into array
        grn.grnHasProducts.push(grnProduct);
        refreshInnerFormAndTable();
        refreshRemainProductList();
      }
    });
  } else {
    showAlert("error", formErrors).then(() => {});
  }
};

//refresh products to remove already added products from dropdown
const refreshRemainProductList = (selectedProduct) => {
  // filter products already in purchaseOrder.poHasProducts
  const newProductList = products.filter(
    (product) =>
      !grn.grnHasProducts.some(
        (extProduct) => extProduct.productId.id == product.id
      )
  );
  //some Method: Checks if at least one element in purchaseOrder.poHasProducts has a productId.id that matches product.id.
  //If some returns true, it means the product is already selected (some returns false when no matches are found).

  //update dropdown with new product list
  fillMoreDataIntoSelect(
    selectProduct,
    "Select Product",
    newProductList,
    "barcode",
    "name",
    selectedProduct
  );
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

  if (grnProduct.sellPrice == null) {
    error = error + "Please Enter Valid Sell Price...!\n";
    textSellPrice.style.border = "1px solid red";
  }

  if (grnProduct.qty == null) {
    error = error + "Please Enter Valid Qty...!\n";
    textQty.style.border = "1px solid red";
  }

  return error;
};

const refillProduct = (rowObject, rowId) => {
  //remove refilled product from grn.grnHasProducts
  grn.grnHasProducts = grn.grnHasProducts.filter(
    (product) => product.productId.id != rowObject.productId.id
  );

  // refresh inner table
  refreshInnerFormAndTable();

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
  refreshRemainProductList(grnProduct.productId.barcode);
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
  grns.forEach((po, index) => {
    if (userPrivilages.delete && po.grnStatusId.name == "Deleted") {
      //catch the button
      let targetElement =
        grnTable.children[1].children[index].children[6].children[
          userPrivilages.update && userPrivilages.insert ? 2 : 1
        ];
      //add changes
      targetElement.style.pointerEvents = "none";
      targetElement.style.visibility = "hidden";
    }
  });

  $("#grnTable").dataTable();
};

const getSupplier = (rowObject) => {
  return (
    rowObject.supplierId.firstName +
    (rowObject.supplierId.company != null
      ? " - " + rowObject.supplierId.company
      : "")
  );
};

const getPurchaseOrderId = (rowObject) => {
  return rowObject.purchaseOrderId.poCode;
};

const getAddedDate = (rowObject) => {
  return rowObject.addedDateTime.substring(0, 10);
};

const getStatus = (rowObject) => {
  if (rowObject.grnStatusId.name == "Received") {
    return (
      '<p class = "status status-active">' + rowObject.grnStatusId.name + "</p>"
    );
  } else if (rowObject.grnStatusId.name == "Deleted") {
    return (
      '<p class = "status status-error">' + rowObject.grnStatusId.name + "</p>"
    );
  }
};

const refillRecord = (rowObject, rowId) => {
  $("#addNewButton").click();
  //get selected grn data
  grn = ajaxGetRequest("/grn/findbyid/" + rowObject.id);
  oldGrn = JSON.parse(JSON.stringify(grn));

  textPOID.value = grn.purchaseOrderId.poCode;
  textSupplierInvNo.value = grn.supplierInvId != null ? grn.supplierInvId : "";
  textNote.value = grn.note != null ? grn.note : "";
  textDiscount.value = grn.discount != null ? grn.discount : "";
  textPaid.value = grn.paid;

  fillMoreDataIntoSelect(
    selectSupplier,
    "Select Supplier",
    suppliers,
    "firstName",
    "company",
    grn.supplierId.firstName
  );

  // disable changing supplier
  selectSupplier.disabled = true;

  fillDataIntoSelect(
    selectGRNStatus,
    "Select Status",
    grnStatuses,
    "name",
    grn.grnStatusId.name
  );

  //refresh inner form and table to get saved products from purchaseOrder.poHasProducts
  refreshInnerFormAndTable();

  //get suppliers product list and then remove alrady added products
  getProductListBySupplier(grn.supplierId.id);
  refreshRemainProductList();

  setBorderStyle([
    textPOID,
    selectSupplier,
    textSupplierInvNo,
    textNote,
    selectGRNStatus,
    textItemCount,
    textTotalAmount,
    textDiscount,
    textGrandTotal,
    textPaid,
  ]);

  //manage buttons
  manageFormButtons("refill", userPrivilages);
};

// //function for delete record
const deleteRecord = (rowObject, rowId) => {
  //get user confirmation
  let title = "Are you sure!\nYou wants to delete following record? \n";
  let message =
    "GRN : " +
    rowObject.grnCode +
    "\n" +
    "Supplier : " +
    rowObject.supplierId.firstName +
    "\n" +
    "Item Count : " +
    rowObject.itemCount +
    "\n" +
    "Grand Total (Rs.) : " +
    rowObject.grandTotal;

  showConfirm(title, message).then((userConfirm) => {
    if (userConfirm) {
      //response from backend ...
      let serverResponse = ajaxRequestBody("/grn", "DELETE", rowObject); // url,method,object
      //check back end response
      if (serverResponse == "OK") {
        showAlert("success", "Delete successfully..! \n" + serverResponse).then(
          () => {
            // Need to refresh table and form
            refreshAll();
          }
        );
      } else {
        showAlert(
          "error",
          "Delete not successfully..! There were some errors \n" +
            serverResponse
        );
      }
    }
  });
};

const viewRecord = (rowObject, rowId) => {};
