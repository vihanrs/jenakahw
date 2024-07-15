//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest(
    "/privilege/byloggeduserandmodule/Purchase Order"
  );

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

  selectSupplier.addEventListener("change", () => {
    selectDFieldValidator(selectSupplier, "purchaseOrder", "supplierId"),
      getProductListBySupplier(purchaseOrder.supplierId.id),
      clearPreviousProducts();
  });

  dateRequiredDate.addEventListener("change", () => {
    dateFieldValidator(dateRequiredDate, "purchaseOrder", "requiredDate");
  });

  selectPOStatus.addEventListener("change", () => {
    selectDFieldValidator(
      selectPOStatus,
      "purchaseOrder",
      "purchaseOrderStatusId"
    );
  });

  textNote.addEventListener("keyup", () => {
    textFieldValidator(textNote, "^.*$", "purchaseOrder", "note");
  });

  selectProduct.addEventListener("change", () => {
    selectDFieldValidator(selectProduct, "poProduct", "productId");
  });

  textPurchasePrice.addEventListener("keyup", () => {
    textFieldValidator(
      textPurchasePrice,
      numberWithdecimals,
      "poProduct",
      "purchasePrice"
    ),
      calLineAmount();
  });

  textQty.addEventListener("keyup", () => {
    textFieldValidator(textQty, numberWithdecimals, "poProduct", "qty"),
      calLineAmount();
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

  btnAddProduct.addEventListener("click", () => {
    addProduct();
    // calPurchaseOrderTotal();
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

//function for refresh form area
const refreshForm = () => {
  //create empty object
  purchaseOrder = {};

  //create new array for products
  purchaseOrder.poHasProducts = [];
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

  // get purchase order status
  poStatuses = ajaxGetRequest("/purchaseorderstatus/findall");
  fillDataIntoSelect(
    selectPOStatus,
    "Select Status",
    poStatuses,
    "name",
    "Requested"
  );

  //bind default selected status in to supplier object and set valid color
  purchaseOrder.purchaseOrderStatusId = JSON.parse(selectPOStatus.value);
  selectPOStatus.style.border = "2px solid #00FF7F";

  //empty all elements
  dateRequiredDate.value = "";
  textNote.value = "";

  //set default border color
  let elements = [selectSupplier, dateRequiredDate, textNote];
  setBorderStyle(elements);

  //set min max values for required date
  let minDate = new Date();
  let maxDate = new Date();

  let minMonth = minDate.getMonth() + 1; //getMonth() returns 0-11, then add 1 to get cuurent month
  if (minMonth < 10) {
    minMonth = "0" + minMonth;
  }

  let minDay = minDate.getDate();
  if (minDay < 10) {
    minDay = "0" + minDay;
  }

  // set current date as min date
  dateRequiredDate.min = minDate.getFullYear() + "-" + minMonth + "-" + minDay;

  let maxMonth = maxDate.getMonth() + 2; // add 2 to get next month from current month
  if (maxMonth < 10) {
    maxMonth = "0" + maxMonth;
  }

  let maxDay = maxDate.getDate();
  if (maxDay < 10) {
    maxDay = "0" + maxDay;
  }

  // set max date for next 30 days
  dateRequiredDate.max = maxDate.getFullYear() + "-" + maxMonth + "-" + maxDay;

  // dateRequiredDate.max =currentDate.getFullYear+"-"+currentDay
  //manage form buttons
  manageFormButtons("insert", userPrivilages);

  //Call inner form and table refresh function
  refreshInnerFormAndTable();

  //clear product list
  getProductListBySupplier(0);
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
};

//clear previously added products and refresh when selecting another supplier
const clearPreviousProducts = () => {
  purchaseOrder.poHasProducts = [];
  refreshInnerFormAndTable();
};

//function for refresh inner product form/table area
const refreshInnerFormAndTable = () => {
  poProduct = {};

  //empty all elements
  textPurchasePrice.value = "";
  textQty.value = "";
  textLineAmount.value = "";
  selectProduct.value = "";

  //set default border color
  let elements = [selectProduct, textPurchasePrice, textQty];
  setBorderStyle(elements);

  const displayProperties = [
    { property: getProductName, datatype: "function" },
    { property: "purchasePrice", datatype: "currency" },
    { property: "qty", datatype: "String" },
    { property: "lineAmount", datatype: "currency" },
  ];

  //call the function (tableID,dataList,display property list,refill function name, delete function name, button visibilitys)
  fillDataIntoInnerTable(
    productsTable,
    purchaseOrder.poHasProducts,
    displayProperties,
    refillProduct,
    deleteProduct
  );

  calculatePOTotal();
};

// ********* INNER FORM/TABLE OPERATIONS *********
// function for get product name and barcode
const getProductName = (rowObject) => {
  return rowObject.productId.barcode + "-" + rowObject.productId.name;
};

// function for calculate line amount
const calLineAmount = () => {
  //calculate line amount
  poProduct.lineAmount =
    poProduct.purchasePrice != null && poProduct.qty != null
      ? poProduct.purchasePrice * poProduct.qty
      : 0;

  //display line amount
  textLineAmount.value =
    poProduct.lineAmount != 0
      ? parseFloat(poProduct.purchasePrice * poProduct.qty).toFixed(2)
      : "";
};

//function for caluclate purchase order total
const calculatePOTotal = () => {
  let poTotal = 0;
  purchaseOrder.poHasProducts.forEach((product) => {
    poTotal += parseFloat(product.lineAmount);
  });

  //bind value to totalAmount
  purchaseOrder.totalAmount = parseFloat(poTotal).toFixed(2);

  textTotalAmount.value = purchaseOrder.totalAmount;
};

//function for refill selected product
const refillProduct = (rowObject, rowId) => {
  //remove refilled product from purchaseOrder.poHasProducts
  purchaseOrder.poHasProducts = purchaseOrder.poHasProducts.filter(
    (product) => product.productId.id !== rowObject.productId.id
  );

  // refresh inner table
  refreshInnerFormAndTable();

  //fill product data into relavent fields
  poProduct = JSON.parse(JSON.stringify(rowObject));
  textPurchasePrice.value = parseFloat(poProduct.purchasePrice).toFixed(2);
  textQty.value = parseFloat(poProduct.qty).toFixed(2);
  textLineAmount.value = parseFloat(poProduct.lineAmount).toFixed(2);

  //set valid border color
  let elements = [selectProduct, textPurchasePrice, textQty];
  setBorderStyle(elements, "2px solid #00FF7F");

  //update product list to add refiled product again
  refreshRemainProductList(poProduct.productId.barcode);
};

//function for delete selected product
const deleteProduct = (rowObject, rowId) => {
  // get user confirmation
  let userConfirm = window.confirm(
    "Are you sure you want to delete this product...?\n" +
      rowObject.productId.barcode +
      " - " +
      rowObject.productId.name
  );

  if (userConfirm) {
    //remove refilled product from purchaseOrder.poHasProducts
    purchaseOrder.poHasProducts = purchaseOrder.poHasProducts.filter(
      (product) => product.productId.id !== rowObject.productId.id
    );

    // refresh inner table
    refreshInnerFormAndTable();

    //update product list to add deleted product again
    refreshRemainProductList();
  }
};

//function for check inner form errors
const checkInnerFormErrors = () => {
  let error = "";

  if (poProduct.productId == null) {
    error = error + "Please Select Product...!\n";
    selectProduct.style.border = "1px solid red";
  }

  if (poProduct.purchasePrice == null) {
    error = error + "Please Enter Valid Purchase Price...!\n";
    textPurchasePrice.style.border = "1px solid red";
  }

  if (poProduct.qty == null) {
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
    let userConfirm = window.confirm(
      "Are you sure to add following product..?\n" +
        "\nProduct Name : " +
        poProduct.productId.name +
        "\nPurchase Price : " +
        poProduct.purchasePrice +
        "\nQty : " +
        poProduct.qty
    );

    if (userConfirm) {
      //add object into array
      purchaseOrder.poHasProducts.push(poProduct);
      refreshInnerFormAndTable();
      refreshRemainProductList();
    }
  } else {
    alert("Error\n" + formErrors);
  }
};

//refresh products to remove already added products from dropdown
const refreshRemainProductList = (selectedProduct) => {
  // filter products already in purchaseOrder.poHasProducts
  const newProductList = products.filter(
    (product) =>
      !purchaseOrder.poHasProducts.some(
        (extProduct) => extProduct.productId.id === product.id
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

// ********* MAIN FORM OPERATIONS *********

// //function for check errors
const checkErrors = () => {
  //need to check all required prperty filds
  let error = "";

  if (purchaseOrder.supplierId == null) {
    error = error + "Please Select Valid Supplier...!\n";
    selectSupplier.style.border = "1px solid red";
  }
  if (purchaseOrder.requiredDate == null) {
    error = error + "Please Select Valid Required Date...!\n";
    dateRequiredDate.style.border = "1px solid red";
  }
  if (purchaseOrder.purchaseOrderStatusId == null) {
    error = error + "Please Select Valid Status...!\n";
    selectPOStatus.style.border = "1px solid red";
  }

  if (parseFloat(purchaseOrder.totalAmount).toFixed(2) == 0.0) {
    error = error + "Please Select Products...!\n";
  }

  return error;
};

//function for check updates
const checkUpdates = () => {
  let updates = ""; //when using 'let' this object only usable is this functional area

  if (oldPurchaseOrder.requiredDate != purchaseOrder.requiredDate) {
    updates +=
      "Required Date has changed " +
      oldPurchaseOrder.requiredDate +
      " into " +
      purchaseOrder.requiredDate +
      " \n";
  }
  if (
    oldPurchaseOrder.purchaseOrderStatusId.id !=
    purchaseOrder.purchaseOrderStatusId.id
  ) {
    updates +=
      "Purchase Order Status has changed " +
      oldPurchaseOrder.purchaseOrderStatusId.name +
      " into " +
      purchaseOrder.purchaseOrderStatusId.name +
      " \n";
  }
  if (oldPurchaseOrder.note != purchaseOrder.note) {
    updates +=
      "Note has changed " +
      (oldPurchaseOrder.note ?? "-") +
      " into " +
      (purchaseOrder.note ?? "-") +
      " \n";
  }
  if (oldPurchaseOrder.totalAmount != purchaseOrder.totalAmount) {
    updates +=
      "Total Amount has changed Rs." +
      oldPurchaseOrder.totalAmount +
      " into Rs." +
      purchaseOrder.totalAmount +
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
    let userConfirm = window.confirm(
      "Are you sure to add following record..?\n" +
        "\nSupplier : " +
        purchaseOrder.supplierId.firstName +
        (purchaseOrder.supplierId.company != null
          ? " - " + purchaseOrder.supplierId.company
          : "") +
        "\nRequired Date : " +
        purchaseOrder.requiredDate +
        "\nTotal Amount (Rs.) : " +
        purchaseOrder.totalAmount
    );

    if (userConfirm) {
      //pass data into back end
      let serverResponse = ajaxRequestBody(
        "/purchaseorder",
        "POST",
        purchaseOrder
      ); // url,method,object

      //check back end response
      if (serverResponse == "OK") {
        alert("Save sucessfully..! " + serverResponse);
        //need to refresh table and form
        refreshAll();
      } else {
        alert("Save not sucessfully..! have some errors \n" + serverResponse);
      }
    }
  } else {
    alert("Error\n" + formErrors);
  }
};

//function for update record
const updateRecord = () => {
  let errors = checkErrors();
  if (errors == "") {
    let updates = checkUpdates();
    if (updates != "") {
      let userConfirm = confirm(
        "Are you sure you want to update following changes...?\n" + updates
      );
      if (userConfirm) {
        let updateServiceResponse = ajaxRequestBody(
          "/purchaseorder",
          "PUT",
          purchaseOrder
        );
        if (updateServiceResponse == "OK") {
          alert("Update sucessfully..! ");
          //need to refresh table and form
          refreshAll();
        } else {
          alert(
            "Update not sucessfully..! have some errors \n" +
              updateSeriveResponse
          );
        }
      }
    } else {
      alert("Nothing to Update...!");
    }
  } else {
    alert("Cannot update!!!\nForm has following errors \n" + errors);
  }
};

//function for refresh table records
const refreshTable = () => {
  //array for store data list
  purchaseOrders = ajaxGetRequest("/purchaseorder/findall");

  //object count = table column count
  //String - number/string/date
  //function - object/array/boolean
  //currency - RS
  const displayProperties = [
    { property: "poCode", datatype: "String" },
    { property: getSupplier, datatype: "function" },
    { property: "requiredDate", datatype: "String" },
    { property: "totalAmount", datatype: "currency" },
    { property: getStatus, datatype: "function" },
  ];

  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys, user privileges)
  fillDataIntoTable(
    purchaseOrdersTable,
    purchaseOrders,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    true,
    userPrivilages
  );

  //hide delete button when status is 'deleted'
  purchaseOrders.forEach((po, index) => {
    if (userPrivilages.delete && po.purchaseOrderStatusId.name == "Deleted") {
      //catch the button
      let targetElement =
        purchaseOrdersTable.children[1].children[index].children[6].children[
          userPrivilages.update && userPrivilages.insert ? 2 : 1
        ];
      //add changes
      targetElement.style.pointerEvents = "none";
      targetElement.style.visibility = "hidden";
    }
  });

  $("#purchaseOrdersTable").dataTable();
};

// // ********* TABLE OPERATIONS *********

// //function for get Supplier
const getSupplier = (rowObject) => {
  return (
    rowObject.supplierId.firstName +
    (rowObject.supplierId.company != null
      ? " - " + rowObject.supplierId.company
      : "")
  );
};

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

const viewRecord = () => {};
// //function for view record
// const viewRecord = (rowObject, rowId) => {
//   //need to get full object
//   let printObj = rowObject;

//   tdFirstName.innerText = printObj.firstName;
//   tdLastName.innerText = printObj.lastName;
//   tdContact.innerText = printObj.contact;
//   tdNIC.innerText = printObj.nic;
//   tdGender.innerText = printObj.gender;
//   tdEmail.innerText = printObj.email;
//   tdUsername.innerText = printObj.username;

//   tdRole.innerText = printObj.roles.map((role) => role.name).join(", ");

//   tdStatus.innerText = printObj.userStatusId.name;
//   //open model
//   $("#modelDetailedView").modal("show");
// };

//function for refill record
const refillRecord = (rowObject, rowId) => {
  $("#addNewButton").click();

  // default = rowObject;
  purchaseOrder = JSON.parse(JSON.stringify(rowObject));
  oldPurchaseOrder = JSON.parse(JSON.stringify(rowObject));

  dateRequiredDate.value = purchaseOrder.requiredDate;
  //set optional fields
  if (purchaseOrder.note != null) textNote.value = purchaseOrder.note;
  else textNote.value = "";

  //if we have optional join column then need to check null
  fillDataIntoSelect(
    selectPOStatus,
    "Select Status",
    poStatuses,
    "name",
    purchaseOrder.purchaseOrderStatusId.name
  );

  // set supplier
  fillMoreDataIntoSelect(
    selectSupplier,
    "Select Supplier",
    suppliers,
    "firstName",
    "company",
    purchaseOrder.supplierId.firstName
  );

  // disable changing supplier
  selectSupplier.disabled = true;

  //refresh inner form and table to get saved products from purchaseOrder.poHasProducts
  refreshInnerFormAndTable();

  //get suppliers product list and then remove alrady added products
  getProductListBySupplier(purchaseOrder.supplierId.id);
  refreshRemainProductList();

  setBorderStyle([selectPOStatus, selectSupplier]);

  //manage buttons
  manageFormButtons("refill", userPrivilages);
};

// //function for delete record
const deleteRecord = (rowObject, rowId) => {
  //get user confirmation
  const userConfirm = confirm(
    "Are you sure!\nYou wants to delete following record? \n" +
      "Supplier : " +
      rowObject.supplierId.firstName +
      "\n" +
      "Required Date : " +
      rowObject.requiredDate +
      "\n" +
      "Total Amount (Rs.) : " +
      rowObject.totalAmount
  );

  if (userConfirm) {
    //response from backend ...
    let serverResponse = ajaxRequestBody("/purchaseorder", "DELETE", rowObject); // url,method,object
    //check back end response
    if (serverResponse == "OK") {
      alert("Delete sucessfully..! \n" + serverResponse);
      //need to refresh table and form
      refreshAll();
    } else {
      alert("Delete not sucessfully..! have some errors \n" + serverResponse);
    }
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
