//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest(
    "/privilege/byloggeduserandmodule/Purchase Order"
  );

  //refresh all
  refreshAll();

  //call all event listners
  addEventListeners();
});

// ********* LISTENERS *********

const addEventListeners = () => {
  // let numberWithdecimals = "^(([1-9]{1}[0-9]{0,7})|([0-9]{0,8}[.][0-9]{2}))$";
  // selectSupplier.addEventListener("change", () => {
  //   selectDFieldValidator(selectSupplier, "purchaseOrder", "supplierId"),
  //     getProductListBySupplier(purchaseOrder.supplierId.id),
  //     clearPreviousProducts();
  // });
  // dateRequiredDate.addEventListener("change", () => {
  //   dateFieldValidator(dateRequiredDate, "purchaseOrder", "requiredDate");
  // });
  // dateRequiredDate.addEventListener("keydown", (event) => {
  //   event.preventDefault();
  // });
  // selectPOStatus.addEventListener("change", () => {
  //   selectDFieldValidator(
  //     selectPOStatus,
  //     "purchaseOrder",
  //     "purchaseOrderStatusId"
  //   );
  // });
  // textNote.addEventListener("keyup", () => {
  //   textFieldValidator(textNote, "^.*$", "purchaseOrder", "note");
  // });
  // selectProduct.addEventListener("change", () => {
  //   selectDFieldValidator(selectProduct, "poProduct", "productId"),
  //     setUnitType(poProduct.productId);
  // });
  // textPurchasePrice.addEventListener("keyup", () => {
  //   textFieldValidator(
  //     textPurchasePrice,
  //     numberWithdecimals,
  //     "poProduct",
  //     "purchasePrice"
  //   ),
  //     calLineAmount();
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
  selectPOStatus.addEventListener("change", () => {});
  selectSupplier.addEventListener("change", () => {});

  btnSearch.addEventListener("click", () => {
    searchByFilters();
  });
  btnReset.addEventListener("click", () => {
    resetFilters();
  });
};

// ********* RESET *********
//function for refresh form and table
const refreshAll = () => {
  //array for store data list
  purchaseOrders = ajaxGetRequest("/purchaseorder/findall");
  // call refresh filters function
  refreshFilters();
  //Call table refresh function
  refreshTable();
};

//function for refresh form area
const refreshFilters = () => {
  suppliers = ajaxGetRequest("/supplier/findactivesuppliers");
  fillMoreDataIntoSelect(
    selectSupplier,
    "Select Supplier",
    suppliers,
    "firstName",
    "company"
  );
  selectSupplier.value = "";
  // get purchase order status
  poStatuses = ajaxGetRequest("/purchaseorderstatus/findall");
  fillDataIntoSelect(selectPOStatus, "Select Status", poStatuses, "name");
};

const resetFilters = () => {
  fillMoreDataIntoSelect(
    selectSupplier,
    "Select Supplier",
    suppliers,
    "firstName",
    "company"
  );
  fillDataIntoSelect(selectPOStatus, "Select Status", poStatuses, "name");
};

const searchByFilters = () => {
  let selectedSupplier =
    selectSupplier.value != "" ? JSON.parse(selectSupplier.value) : "";
  let selectedStatus =
    selectPOStatus.value != "" ? JSON.parse(selectPOStatus.value) : "";

  if (selectedSupplier != "" && selectedStatus != "") {
    console.log("T1");
    //array for store data list
    purchaseOrders = ajaxGetRequest(
      "report/reportpurchaseorder/findbystatusandsupplier/" +
        selectedStatus.id +
        "/" +
        selectedSupplier.id
    );
  } else if (selectedSupplier != "") {
    console.log("T2");
    //array for store data list
    purchaseOrders = ajaxGetRequest(
      "report/reportpurchaseorder/findbysupplier/" + selectedSupplier.id
    );
  } else if (selectedStatus != "") {
    console.log("T3");
    //array for store data list
    purchaseOrders = ajaxGetRequest(
      "report/reportpurchaseorder/findbystatus/" + selectedStatus.id
    );
  } else {
    purchaseOrders = ajaxGetRequest("/purchaseorder/findall");
  }
  refreshTable();
};

// ********* TABLE OPERATIONS *********

//function for refresh table records
const refreshTable = () => {
  //object count = table column count
  //String - number/string/date
  //function - object/array/boolean
  //currency - RS
  const displayProperties = [
    { property: "poCode", datatype: "String" },
    { property: getSupplier, datatype: "function" },
    { property: "requiredDate", datatype: "String" },
    { property: getItemCount, datatype: "function" },
    { property: "totalAmount", datatype: "currency" },
    { property: getStatus, datatype: "function" },
  ];

  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys, user privileges)
  fillDataIntoTable(
    purchaseOrdersReportTable,
    purchaseOrders,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    false,
    userPrivilages
  );
};

const viewRecord = () => {};
const refillRecord = () => {};
const deleteRecord = () => {};

//function for get Supplier
const getSupplier = (rowObject) => {
  return (
    rowObject.supplierId.firstName +
    (rowObject.supplierId.company != null
      ? " - " + rowObject.supplierId.company
      : "")
  );
};

const getItemCount = (rowObject) => {
  return rowObject.poHasProducts.length;
};

// function for get Status
const getStatus = (rowObject) => {
  if (rowObject.purchaseOrderStatusId.name == "Requested") {
    return (
      '<p class = "status btn-info">' +
      rowObject.purchaseOrderStatusId.name +
      "</p>"
    );
  } else if (rowObject.purchaseOrderStatusId.name == "Received") {
    return (
      '<p class = "status status-active">' +
      rowObject.purchaseOrderStatusId.name +
      "</p>"
    );
  } else if (rowObject.purchaseOrderStatusId.name == "Canceled") {
    return (
      '<p class = "status status-warning">' +
      rowObject.purchaseOrderStatusId.name +
      "</p>"
    );
  } else if (rowObject.purchaseOrderStatusId.name == "Deleted") {
    return (
      '<p class = "status status-error">' +
      rowObject.purchaseOrderStatusId.name +
      "</p>"
    );
  }
};

// // ********* PRINT OPERATIONS *********

// //print function
// const printViewRecord = () => {
//   newTab = window.open();
//   newTab.document.write(
//     //  link bootstrap css
//     "<head><title>User Details</title>" +
//       '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
//       "<h2>User Details</h2>" +
//       printTable.outerHTML
//   );

//   //triger print() after 1000 milsec time out
//   setTimeout(function () {
//     newTab.print();
//   }, 1000);
// };

// //print all data table after 1000 milsec of new tab opening () - to refresh the new tab elements
// const printFullTable = () => {
//   const newTab = window.open();
//   newTab.document.write(
//     //  link bootstrap css
//     "<head><title>Print Employee</title>" +
//       '<script src="resources/js/jquery.js"></script>' +
//       '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
//       "<h2>Employee Details</h2>" +
//       tableId.outerHTML +
//       '<script>$(".modify-button").css("display","none")</script>'
//   );

//   setTimeout(function () {
//     newTab.print();
//   }, 1000);
// };
