//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest("/privilege/byloggeduserandmodule/Invoice");

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
  let namePattern = "^[A-Z][A-Za-z ]{1,19}[A-Za-z]$";
  let contactPattern = "^[0][7][01245678][0-9]{7}$";
  let qtyPattern =
    "^(([1-9]{1}[0-9]{0,7})|([0-9]{1}[.][0-9]{1,3})|([1-9]{1}[0-9]{0,7}[.][0-9]{1,3}))$";
  // textCustomerContact;
  // textCustomerName;
  // selectStatus;
  textCustomerName.addEventListener("keyup", () => {
    textFieldValidator(textCustomerName, namePattern, "customer", "fullName");
  });

  textCustomerContact.addEventListener("keyup", () => {
    textFieldValidator(
      textCustomerContact,
      contactPattern,
      "customer",
      "contact"
    ),
      getCustomerByContact(customer.contact);
  });

  textProduct.addEventListener("keyup", () => {
    getProductList(textProduct);
  });

  textProduct.addEventListener("input", () => {
    invoiceDataListValidator(textProduct, "stocks", "invProduct", "allData"),
      getProductValues();
  });

  selectStatus.addEventListener("change", () => {
    selectDFieldValidator(selectStatus, "invoice", "invoiceStatusId");
  });

  textQty.addEventListener("keyup", () => {
    qtyFieldValidator(
      textQty,
      qtyPattern,
      "invProduct",
      "qty",
      invProduct.stockId.availableQty
    ),
      calLineAmount();
  });

  textItemCount.addEventListener("keyup", () => {
    textFieldValidator(
      textItemCount,
      numberWithdecimals,
      "invoice",
      "itemCount"
    );
  });

  dateFromDate.addEventListener("change", () => {
    checkDates();
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

  //product add function call
  btnAddProduct.addEventListener("click", () => {
    addProduct();
  });

  //product selection reset function call
  btnProductReset.addEventListener("click", () => {
    resetProductSelection();
  });

  //product selection reset function call
  btnSearch.addEventListener("click", () => {
    refreshTable();
  });

  //record print function call
  btnViewPrint.addEventListener("click", () => {
    printViewRecord();
  });

  //print full table function call
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

// ********* FORM OPERATIONS *********

//function for refresh form area
const refreshForm = () => {
  //create empty object
  customer = {};
  invoice = {};
  invoice.invoiceHasProducts = new Array();

  // get status
  statuses = ajaxGetRequest("/invoicestatus/findall");
  fillDataIntoSelect(
    selectStatus,
    "Select Status",
    statuses,
    "name",
    "Pending"
  );
  statusDiv.classList.add("d-none");
  invoice.invoiceStatusId = JSON.parse(selectStatus.value);
  selectStatus.style.border = "2px solid #00FF7F";

  //empty all elements
  textCustomerContact.value = "";
  textCustomerName.value = "";
  dateInvoiceDate.value = new Date().toISOString().split("T")[0];
  contactRegisterCheck.classList.add("d-none");

  //set default border color
  let elements = [
    textCustomerName,
    textCustomerContact,
    textItemCount,
    textTotalAmount,
  ];
  setBorderStyle(elements);

  refreshInnerFormAndTable();

  // related to the table
  const today = new Date();

  // Get the current year, month, and day using local time
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, "0"); // Months are zero-based
  const day = String(today.getDate()).padStart(2, "0");

  const currentDate = year + "-" + month + "-" + day;
  dateFromDate.value = currentDate;
  dateToDate.value = currentDate;

  dateFromDate.max = currentDate;
  dateToDate.max = currentDate;
  //manage form buttons
  manageFormButtons("insert", userPrivilages);
};

// function for check customer by contact
const getCustomerByContact = (contact) => {
  if (contact != null) {
    customer = ajaxGetRequest("/customer/getByContact/" + contact);

    if (customer.contact != null) {
      contactRegisterCheck.classList.add("d-none");
      textCustomerName.value = customer.fullName;
      // textCustomerName.disabled = true;
    } else {
      contactRegisterCheck.classList.remove("d-none");
      textCustomerName.value = "";
      setBorderStyle([textCustomerName]);
      customer = {};
      customer.contact = contact;
    }
  } else {
    textCustomerName.value = "";
    setBorderStyle([textCustomerName]);
    customer = {};
    contactRegisterCheck.classList.add("d-none");
  }
};

//function for check errors
const checkErrors = () => {
  //need to check all required property fields
  let error = "";

  if (customer.fullName == null && customer.contact != null) {
    error += "Please Enter Customer Name...!\n";
    textCustomerName.style.border = "1px solid red";
  }
  if (customer.contact == null && customer.fullName != null) {
    error += "Please Enter Customer Contact No...!\n";
    textCustomerContact.style.border = "1px solid red";
  }

  if (invoice.invoiceStatusId.id == null) {
    error += "Please Select Valid Status...!\n";
    selectStatus.style.border = "1px solid red";
  }

  if (invoice.invoiceHasProducts.length == 0) {
    error += "Please Select Products...!\n";
  }

  return error;
};

//function for check updates
const checkUpdates = () => {
  let updates = "";

  // if (oldinvoice.customerId == null) {
  //   oldinvoice.customerId = { fullName: null, contact: null };
  // }

  // if (oldinvoice.customerId.fullName != customer.fullName) {
  //   updates +=
  //     "Name has changed " +
  //     oldinvoice.customerId.fullName +
  //     " into " +
  //     customer.fullName +
  //     " \n";
  // }
  // if (oldinvoice.customerId.contact != customer.contact) {
  //   updates +=
  //     "Contact No. has changed " +
  //     oldinvoice.customerId.contact +
  //     " into " +
  //     customer.contact +
  //     " \n";
  // }

  return updates;
};

//function for add record
const addRecord = () => {
  //check form errors -
  let formErrors = checkErrors();
  if (formErrors == "") {
    //get user confirmation
    let title = "Are you sure to add following record..?\n";
    msgCustomer =
      (customer.fullName != null
        ? "Customer Name : " + customer.fullName
        : "") +
      (customer.fullName != null ? "\nContact : " + customer.contact : "");
    let message =
      msgCustomer +
      "\nItem Count : " +
      invoice.itemCount +
      "\nTotal : Rs." +
      invoice.total;
    showConfirm(title, message).then((userConfirm) => {
      if (userConfirm) {
        invoice.customerId = customer;
        console.log(invoice);

        //pass data into back end
        let serverResponse = ajaxRequestBody("/invoice", "POST", invoice); // url,method,object

        //check back end response
        if (serverResponse == "OK") {
          showAlert("success", "Invoice Created successfully..!").then(() => {
            //need to refresh table and form
            refreshAll();
          });
        } else {
          showAlert(
            "error",
            "Invoice create not successfully..! have some errors \n" +
              serverResponse
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
          let serverResponse = ajaxRequestBody("/customer", "PUT", customer);
          if (serverResponse == "OK") {
            showAlert("success", "Customer Update successfully..!").then(() => {
              //need to refresh table and form
              refreshAll();
            });
          } else {
            showAlert(
              "error",
              "Customer update not successfully..! have some errors \n" +
                serverResponse
            );
          }
        }
      });
    } else {
      showAlert("warning", "Nothing to Update...!");
    }
  } else {
    showAlert("error", errors);
  }
};

//function for caluclate invoice total
const calculateInvTotal = () => {
  let invTotal = 0;
  invoice.invoiceHasProducts.forEach((ele) => {
    invTotal += parseFloat(ele.lineAmount);
  });

  //bind value to totalAmount
  invoice.total = invTotal.toFixed(2);

  textTotalAmount.value = invoice.total;
};

// ********* INNER FORM/TABLE OPERATIONS *********

//function for refresh inner product form/table area
const refreshInnerFormAndTable = () => {
  invProduct = {};

  //empty all elements
  resetProductSelection();

  const displayProperties = [
    { property: getProduct, datatype: "function" },
    { property: "sellPrice", datatype: "currency" },
    { property: "qty", datatype: "String" },
    { property: "lineAmount", datatype: "currency" },
  ];

  //call the function (tableID,dataList,display property list,refill function name, delete function name, button visibilitys)
  fillDataIntoInnerTable(
    invoiceProductsTable,
    invoice.invoiceHasProducts,
    displayProperties,
    refillProductDetail,
    deleteProductDetail
  );

  calculateInvTotal();
  getItemCount();
};

// ********* INNER FORM OPERATIONS *********

// function for get total item count
const getItemCount = () => {
  let count = invoice.invoiceHasProducts.length;
  textItemCount.value = count;
  invoice.itemCount = count;
};

// function for calculate line amount
const calLineAmount = () => {
  //calculate line amount
  invProduct.lineAmount =
    invProduct.sellPrice != null && invProduct.qty != null
      ? parseFloat(invProduct.sellPrice) * parseFloat(invProduct.qty)
      : 0;

  //display line amount
  textLineAmount.value =
    invProduct.lineAmount != 0
      ? parseFloat(invProduct.lineAmount).toFixed(2)
      : "";
};

// function for load product list on search
const getProductList = (fieldId) => {
  const fieldValue = fieldId.value;
  let regPattern = new RegExp("^[A-Za-z0-9 ]{3,}$");
  stocks = [];

  if (fieldValue !== "") {
    if (regPattern.test(fieldValue)) {
      stocks = ajaxGetRequest(
        "/stock/findstocksbyproductnamebarcode/" + fieldValue
      );
      // add new properties to access stock details
      stocks = stocks.map((stock) => ({
        originalStock: stock, // keep copy of original
        barcode: stock.productId.barcode,
        name: stock.productId.name,
        brand: stock.productId.brandId.name,
        productId: stock.productId.id,
        ...stock, // add all original data
      }));
    }
  }
  // fillMoreDataIntoDataList(dataListProducts, stocks, "barcode", "name");
  fillFullDataIntoDataList(
    dataListProducts,
    stocks,
    "id",
    "barcode",
    "brand",
    "name",
    "sellPrice",
    "availableQty"
  );
};

// function for get product values and bind them
const getProductValues = () => {
  if (invProduct.allData != null) {
    textQty.disabled = false;
    textQty.focus();

    textSellPrice.value = parseFloat(invProduct.allData.sellPrice).toFixed(2);
    textUnitType.innerText = invProduct.allData.productId.unitTypeId.name;
    textUnitType.classList.remove("d-none");
    textProduct.value =
      invProduct.allData.productId.barcode +
      " - " +
      invProduct.allData.productId.name;
    textProduct.disabled = true;

    //bind values
    invProduct.stockId = invProduct.allData.originalStock;
    invProduct.sellPrice = invProduct.allData.sellPrice;
    invProduct.productId = invProduct.allData.productId.id;

    // remove unwanted properties
    delete invProduct.allData;
  }
};

// function for reset selected product
const resetProductSelection = () => {
  invProduct = {};
  textProduct.value = "";
  textProduct.disabled = false;
  textQty.value = "";
  textQty.disabled = true;
  textSellPrice.value = "";
  textSellPrice.value = "";
  textLineAmount.value = "";
  textUnitType.classList.add("d-none");

  refillProductRowId = null;

  //set default border color
  setBorderStyle([textProduct, textSellPrice, textQty, textLineAmount]);
};

//function for check inner form errors
const checkInnerFormErrors = () => {
  let error = "";

  if (invProduct.productId == null) {
    error = error + "Please Select Product...!\n";
    textProduct.style.border = "1px solid red";
  }

  if (invProduct.qty == null) {
    error = error + "Please Enter Valid Qty...!\n";
    textQty.style.border = "1px solid red";
  }

  return error;
};

// function for check product in inner table
const isAlreayAdded = () => {
  return invoice.invoiceHasProducts.some(
    (invoiceHasProduct) =>
      // at least one element pass the condtion then immedialty retrun true
      invoiceHasProduct.stockId.id === invProduct.stockId.id
  );
};

// fucntion for add product to inner table
const addProduct = () => {
  // check errors
  let formErrors = checkInnerFormErrors();
  if (formErrors == "") {
    if (refillProductRowId != null) {
      updateProduct(invProduct);
    } else {
      if (!isAlreayAdded()) {
        // get user confirmation
        let title = "Are you sure to add following product..?";
        let message =
          "Product Name : " +
          invProduct.stockId.productId.name +
          "\nPurchase Price : " +
          parseFloat(invProduct.sellPrice).toFixed(2) +
          "\nQty : " +
          invProduct.qty;

        showConfirm(title, message).then((userConfirm) => {
          if (userConfirm) {
            //add object into array
            invoice.invoiceHasProducts.push(invProduct);
            refreshInnerFormAndTable();
          }
        });
      } else {
        showAlert("error", "This Product Already Added!");
      }
    }
  } else {
    showAlert("error", formErrors);
  }

  console.log(invoice);
};

// function for reset update refilled product
const updateProduct = (updatedProduct) => {
  invoice.invoiceHasProducts[refillProductRowId].qty = updatedProduct.qty;
  invoice.invoiceHasProducts[refillProductRowId].lineAmount =
    updatedProduct.lineAmount;

  refreshInnerFormAndTable();
};

// ********* INNER TABLE OPERATIONS *********

// function for get product
const getProduct = (rowObject, rowId) => {
  return (
    rowObject.stockId.productId.barcode + " " + rowObject.stockId.productId.name
  );
};

// function for refill product
const refillProductDetail = (rowObject, rowId) => {
  //remove refilled product from grn.grnHasProducts
  // invoice.invoiceHasProducts = invoice.invoiceHasProducts.filter(
  //   (product) => product.stockId.productId.id != rowObject.stockId.productId.id
  // );

  // refresh inner table
  // refreshInnerFormAndTable();

  refillProductRowId = rowId;

  //fill product data into relavent fields
  invProduct = JSON.parse(JSON.stringify(rowObject));
  textProduct.value =
    invProduct.stockId.productId.barcode +
    " - " +
    invProduct.stockId.productId.name;
  textSellPrice.value = parseFloat(invProduct.sellPrice).toFixed(2);
  textQty.value = parseFloat(invProduct.qty).toFixed(2);
  textLineAmount.value = parseFloat(invProduct.lineAmount).toFixed(2);

  textProduct.disabled = true;
  textQty.disabled = false;

  //set valid border color
  let elements = [textProduct, textQty];
  setBorderStyle(elements, "2px solid #00FF7F");
};

//function for delete selected product
const deleteProductDetail = (rowObject) => {
  // get user confirmation
  let title = "Are you sure you want to delete this product...?\n";
  let message =
    rowObject.stockId.productId.barcode +
    " - " +
    rowObject.stockId.productId.name;

  showConfirm(title, message).then((userConfirm) => {
    if (userConfirm) {
      //remove deleted product from invoice.invoiceHasProducts
      invoice.invoiceHasProducts = invoice.invoiceHasProducts.filter(
        (product) =>
          product.stockId.productId.id != rowObject.stockId.productId.id
      );

      // refresh inner table
      refreshInnerFormAndTable();
    }
  });
};

// ********* TABLE OPERATIONS *********

//function for refresh table records
const refreshTable = () => {
  //array for store data list
  invoices = ajaxGetRequest(
    "/invoice/findall/" + dateFromDate.value + "/" + dateToDate.value
  );

  //object count = table column count
  //String - number/string/date
  //function - object/array/boolean
  //currency - RS
  const displayProperties = [
    { property: "invoiceId", datatype: "String" },
    { property: getCustomer, datatype: "function" },
    { property: "itemCount", datatype: "String" },
    { property: getDate, datatype: "function" },
    { property: "grandTotal", datatype: "currency" },
    { property: "paidAmount", datatype: "currency" },
    { property: getStatus, datatype: "function" },
  ];

  let table = new DataTable("#invoiceTable");
  table.destroy();
  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys, user privileges)
  fillDataIntoTable(
    invoiceTable,
    invoices,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    true,
    userPrivilages
  );

  //hide delete button when status is 'deleted'
  invoices.forEach((invoice, index) => {
    if (userPrivilages.delete && invoice.invoiceStatusId.name == "Deleted") {
      //catch the button
      let targetElement =
        customerTable.children[1].children[index].children[8].children[
          userPrivilages.update && userPrivilages.insert ? 2 : 1
        ];
      //add changes
      targetElement.style.pointerEvents = "none";
      targetElement.style.visibility = "hidden";
    }
  });

  $("#invoiceTable").dataTable();
};

// fucntion for get customer
const getCustomer = (rowObject) => {
  return rowObject.customerId != null ? rowObject.customerId.fullName : "-";
};

// function for get added date
const getDate = (rowObject) => {
  return rowObject.addedDateTime.split("T")[0];
};

// function for get status
const getStatus = (rowObject) => {
  if (rowObject.invoiceStatusId.name == "Completed") {
    return (
      '<p class = "status status-active">' +
      rowObject.invoiceStatusId.name +
      "</p>"
    );
  } else if (rowObject.invoiceStatusId.name == "Pending") {
    return (
      '<p class = "status status-warning">' +
      rowObject.invoiceStatusId.name +
      "</p>"
    );
  } else if (rowObject.invoiceStatusId.name == "Deleted") {
    return (
      '<p class = "status status-error">' +
      rowObject.invoiceStatusId.name +
      "</p>"
    );
  } else if (rowObject.invoiceStatusId.name == "Incompelte") {
    return (
      '<p class = "status status-pending"}>' +
      rowObject.invoiceStatusId.name +
      "</p>"
    );
  }
};

//function for view record
const viewRecord = (rowObject, rowId) => {
  //need to get full object
  let printObj = rowObject;

  tdInvoiceId.innerText = printObj.invoiceId;
  tdCustomer.innerText =
    printObj.customerId != null ? printObj.customerId.fullName : "";
  tdItemCount.innerText = printObj.itemCount;
  tdInvoicedDate.innerText = printObj.addedDateTime.split("T")[0];
  tdInvoiceType.innerText = printObj.isCredit != true ? "Normal" : "Credit";
  tdTotal.innerText = "Rs." + parseFloat(printObj.total).toFixed(2);
  tdDiscount.innerText =
    "Rs." + parseFloat(printObj.discount ?? "0").toFixed(2);
  tdGrandTotal.innerText = "Rs." + parseFloat(printObj.grandTotal).toFixed(2);
  tdPaid.innerText = "Rs." + parseFloat(printObj.paidAmount).toFixed(2);
  tdBalance.innerText = "Rs." + parseFloat(printObj.balanceAmount).toFixed(2);
  tdStatus.innerText = printObj.invoiceStatusId.name;
  getINVProductsForPrint(printObj);
  //open model
  $("#modelDetailedView").modal("show");
};

// funtion for get invoice product list for print
const getINVProductsForPrint = (printObj) => {
  // remove the previously added dynamic rows
  document.querySelectorAll(".dynamic-row").forEach((row) => row.remove());

  printObj.invoiceHasProducts.forEach((ele) => {
    const tr = document.createElement("tr");
    tr.classList.add("dynamic-row");
    const tdProduct = document.createElement("td");
    const tdSellPrice = document.createElement("td");
    const tdQty = document.createElement("td");
    const tdLineAmount = document.createElement("td");

    tdProduct.innerText =
      ele.stockId.productId.barcode + " - " + ele.stockId.productId.name;
    tdSellPrice.innerText = "Rs." + parseFloat(ele.sellPrice).toFixed(2);
    tdQty.innerText =
      ele.qty + " (" + ele.stockId.productId.unitTypeId.name + ")";
    tdLineAmount.innerText = "Rs." + parseFloat(ele.lineAmount).toFixed(2);

    tr.appendChild(tdProduct);
    tr.appendChild(tdSellPrice);
    tr.appendChild(tdQty);
    tr.appendChild(tdLineAmount);
    printTable.appendChild(tr);
  });
};

//function for refill record
const refillRecord = (rowObject, rowId) => {
  refreshForm();
  $("#addNewButton").click();

  invoice = JSON.parse(JSON.stringify(rowObject)); //convert rowobject to json string and covert back it to js object
  oldinvoice = JSON.parse(JSON.stringify(rowObject)); // deep copy - create compeletely indipended two objects

  customer = invoice.customerId;

  textCustomerName.value =
    invoice.customerId != null ? invoice.customerId.fullName : "";
  textCustomerContact.value =
    invoice.customerId != null ? invoice.customerId.contact : "";
  dateInvoiceDate.value = invoice.addedDateTime.split("T")[0];

  // set status
  fillDataIntoSelect(
    selectStatus,
    "Select Status",
    statuses,
    "name",
    invoice.invoiceStatusId.name
  );
  statusDiv.classList.remove("d-none");

  //refresh inner form and table to get saved products from invoice.invoiceHasProducts
  refreshInnerFormAndTable();

  setBorderStyle([textCustomerName, textCustomerContact]);

  //manage buttons
  manageFormButtons("refill", userPrivilages);
};

// //function for delete record
const deleteRecord = (rowObject, rowId) => {
  //get user confirmation
  let title = "Are you sure!\nYou wants to delete following record? \n";
  let message =
    "Invoice ID : " +
    rowObject.invoiceId +
    "\nCustomer :" +
    rowObject.customerId.fullName +
    "\nItem Count :" +
    rowObject.itemCount +
    "\nGrand Total :" +
    rowObject.grandTotal;

  showConfirm(title, message).then((userConfirm) => {
    if (userConfirm) {
      //response from backend ...
      let serverResponse = ajaxRequestBody("/invoice", "DELETE", rowObject); // url,method,object
      //check back end response
      if (serverResponse == "OK") {
        showAlert("success", "Invoice Delete successfully..!").then(() => {
          // Need to refresh table and form
          refreshAll();
        });
      } else {
        showAlert(
          "error",
          "Invoice delete not successfully..! have some errors \n" +
            serverResponse
        );
      }
    }
  });
};

// function for get invoices by given date range
const checkDates = () => {
  dateToDate.min = dateFromDate.value;
  if (dateToDate.value < dateFromDate.value) {
    dateToDate.value = dateFromDate.value;
  }
};

// ********* PRINT OPERATIONS *********

//print function
const printViewRecord = () => {
  newTab = window.open();
  newTab.document.write(
    //  link bootstrap css
    "<head><title>Print Invoice</title>" +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
      "<h2 style = 'font-weight:bold'>Invoice Details</h2>" +
      printTable.outerHTML
  );

  //triger print() after 1000 milsec time out - time to load content to the printing tab
  setTimeout(function () {
    newTab.print();
  }, 1000);
};

//print all data table after 1000 milsec of new tab opening () - to refresh the new tab elements
const printFullTable = () => {
  const newTab = window.open();
  newTab.document.write(
    //  link bootstrap css
    "<head><title>Print Invoices</title>" +
      '<script src="resources/js/jquery.js"></script>' +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
      "<h2 style = 'font-weight:bold'>Invoices Details</h2>" +
      invoiceTable.outerHTML +
      '<script>$("#modifyButtons").css("display","none");$(".table-buttons").hide();</script>'
  );

  setTimeout(function () {
    newTab.print();
  }, 1000);
};
