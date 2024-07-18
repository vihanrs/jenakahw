// textCostPrice;
// textQty;
// textSellPrice;
// textLineAmount;
// textTotalQty;
// textTotalAmount;
// textDiscount;
// textGrandTotal;
// textPaid;

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
    textFieldValidator(textPOID, "^[PO][0-9]{7}$", "grn", "purchaseOrderId");
  });

  selectSupplier.addEventListener("change", () => {
    selectDFieldValidator(selectSupplier, "grn", "supplierId");
    //   getProductListBySupplier(purchaseOrder.supplierId.id),
    //   clearPreviousProducts();
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

  textCostPrice.addEventListener("keyup", () => {
    textFieldValidator(
      textPurchasePrice,
      numberWithdecimals,
      "grn",
      "purchasePrice"
    ),
      calLineAmount();
  });

  // dateRequiredDate.addEventListener("change", () => {
  //   dateFieldValidator(dateRequiredDate, "purchaseOrder", "requiredDate");
  // });

  // dateRequiredDate.addEventListener("keydown", (event) => {
  //   event.preventDefault();
  // });

  // textNote.addEventListener("keyup", () => {
  //   textFieldValidator(textNote, "^.*$", "purchaseOrder", "note");
  // });

  // selectProduct.addEventListener("change", () => {
  //   selectDFieldValidator(selectProduct, "poProduct", "productId");
  // });

  // textQty.addEventListener("keyup", () => {
  //   textFieldValidator(
  //     textQty,
  //     "^(([1-9]{1}[0-9]{0,7})|([0-9]{0,8}[.][0-9]{1,2}))$",
  //     "poProduct",
  //     "qty"
  //   ),
  //     calLineAmount();
  // });

  // //form reset button function call
  // btnReset.addEventListener("click", () => {
  //   refreshForm();
  // });

  // //record update function call
  // btnUpdate.addEventListener("click", () => {
  //   updateRecord();
  // });

  // //record save function call
  // btnAdd.addEventListener("click", () => {
  //   addRecord();
  // });

  // //record print function call
  // btnViewPrint.addEventListener("click", () => {
  //   printViewRecord();
  // });

  // btnAddProduct.addEventListener("click", () => {
  //   addProduct();
  // });
};

// ********* RESET *********
//function for refresh form and table
const refreshAll = () => {
  //Call form refresh function
  refreshForm();
  //Call table refresh function
  refreshTable();
};

//function for refresh form area
const refreshForm = () => {
  //create empty object
  grn = {};

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

  // // get purchase order status
  // poStatuses = ajaxGetRequest("/purchaseorderstatus/findall");
  // fillDataIntoSelect(
  //   selectPOStatus,
  //   "Select Status",
  //   poStatuses,
  //   "name",
  //   "Requested"
  // );

  // //bind default selected status in to supplier object and set valid color
  // purchaseOrder.purchaseOrderStatusId = JSON.parse(selectPOStatus.value);
  // selectPOStatus.style.border = "2px solid #00FF7F";

  // //empty all elements
  // dateRequiredDate.value = "";
  // textNote.value = "";

  // //set default border color
  // let elements = [selectSupplier, dateRequiredDate, textNote];
  // setBorderStyle(elements);

  // //manage form buttons
  // manageFormButtons("insert", userPrivilages);

  // //Call inner form and table refresh function
  // refreshInnerFormAndTable();
};

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

// ********* TABLE OPERATIONS *********

const getSupplier = (rowObject) => {
  return (
    rowObject.supplierId.firstName +
    (rowObject.supplierId.company != null
      ? " - " + rowObject.supplierId.company
      : "")
  );
};

const getPurchaseOrderId = (rowObject) => {};
const getAddedDate = (rowObject) => {
  return rowObject.addedDateTime.substring(0, 10);
};
const getStatus = (rowObject) => {
  return rowObject.grnStatusId.name;
};

const viewRecord = (rowObject, rowId) => {};
const refillRecord = (rowObject, rowId) => {
  $("#addNewButton").click();
  //get selected grn data
  grn = ajaxGetRequest("/grn/findbyid/" + rowObject.id);
  oldGrn = JSON.parse(JSON.stringify(grn));

  textPOID.value = grn.purchaseOrderId != null ? grn.purchaseOrderId : "";

  fillMoreDataIntoSelect(
    selectSupplier,
    "Select Supplier",
    suppliers,
    "firstName",
    "company",
    grn.supplierId.firstName
  );
};
const deleteRecord = (rowObject, rowId) => {};
