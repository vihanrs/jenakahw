//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest(
    "/privilege/byloggeduserandmodule/Invoice Payment"
  );

  //refresh all
  refreshAll();

  //set default selected section
  if (userPrivilages.insert) {
    showDefaultSection("addNewButton", "addNewSection");
  }
  // else {
  //   showDefaultSection("viewAllButton", "viewAllSection");
  //   addAccordion.style.display = "none";
  // }
  //call all event listners
  addEventListeners();
});

// ********* LISTENERS *********
const addEventListeners = () => {
  let numberWithdecimals = "^(([1-9]{1}[0-9]{0,7})|([0-9]{0,8}[.][0-9]{2}))$";

  textInvoiceId.addEventListener("keyup", () => {
    getInvoiceList(textInvoiceId);
  });

  textInvoiceId.addEventListener("input", () => {
    dataListValidator(
      textInvoiceId,
      "pendingInvoices",
      "invPayment",
      "invoiceId",
      "invoiceId"
    ),
      getInvoiceValues();
  });

  textDiscount.addEventListener("keyup", () => {
    discountValidator(
      textDiscount,
      textTotalAmount,
      discountPrecentageCheck,
      "invPayment",
      "discount"
    ),
      calDisount(),
      calPayment();
  });

  discountPrecentageCheck.addEventListener("change", () => {
    discountValidator(
      textDiscount,
      textTotalAmount,
      discountPrecentageCheck,
      "invPayment",
      "discount"
    ),
      calDisount(),
      calPayment();
  });

  creditSellCheck.addEventListener("change", () => {
    calPayment();
  });

  textPayment.addEventListener("keyup", () => {
    calPayment();
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
  // btnViewPrint.addEventListener("click", () => {
  //   printViewRecord();
  // });

  //print full table function call
  // btnPrintFullTable.addEventListener("click", () => {
  //   printFullTable();
  // });
};

// ********* RESET *********
//function for refresh form and table
const refreshAll = () => {
  //Call form refresh function
  refreshForm();
  //Call table refresh function
  // refreshTable();
};

// ********* FORM OPERATIONS *********

//function for refresh form area
const refreshForm = () => {
  //create empty object
  invPayment = {};

  createViewPayMethodUI();
  resetInvoiceDetails();

  //empty all elements
  textInvoiceId.value = "";

  setBorderStyle([textInvoiceId]);

  //manage form buttons
  manageFormButtons("insert", userPrivilages);
};

//function for create select Roles UI part
const createViewPayMethodUI = () => {
  divPaymethods.innerHTML = "";
  paymethods = ajaxGetRequest("/paymethod/findall");
  paymethods.forEach((paymethod) => {
    const div = document.createElement("div");
    div.className = "form-check form-check-inline";
    const inputCHK = document.createElement("input");
    inputCHK.type = "radio";
    inputCHK.className = "form-check-input";
    inputCHK.name = "flexRadioDefault";
    inputCHK.id = "chk" + paymethod.name;

    inputCHK.onchange = function () {
      if (this.checked) {
        //if not exist add new role
        invPayment.paymethodId = paymethod;
        if (
          invPayment.paymethodId.name == "Card" ||
          invPayment.paymethodId.name == "Cheque"
        ) {
          textPayment.value = parseFloat(
            invPayment.invoiceId.grandTotal
          ).toFixed(2);
          textPayment.style.border = "1px solid #ced4da";
          calPayment();
        }
      }
    };

    const label = document.createElement("label");
    label.className = "form-check-label fw-bold";
    label.for = inputCHK.id;
    label.innerText = paymethod.name;

    div.appendChild(inputCHK);
    div.appendChild(label);

    divPaymethods.appendChild(div);
  });
};

// function for get pending invoice list
const getInvoiceList = (fieldId) => {
  const fieldValue = fieldId.value;

  pendingInvoices = [];
  if (new RegExp("^[0-9]{3}$").test(fieldValue)) {
    // make full invoice number relavent to the current date
    let date = new Date();
    let year = date.getFullYear().toString().slice(-2); // Get last 2 digits of the year
    let month = (date.getMonth() + 1).toString().padStart(2, "0"); // Get month and add 0 if needed
    let day = date.getDate().toString().padStart(2, "0"); // Get day and add 0 if needed
    let invoiceNo = "INV" + year + month + day + fieldValue;
    invPayment.invoiceId = ajaxGetRequest("/invoice/findbyid/" + invoiceNo);
    getInvoiceValues();
  } else if (new RegExp("^[I][N][V][0-9]{9}$").test(fieldValue)) {
    // get full invoice number from user
    invPayment.invoiceId = ajaxGetRequest("/invoice/findbyid/" + fieldValue);
    getInvoiceValues();
  } else if (new RegExp("^[I][N][V][0-9]{0,9}$").test(fieldValue)) {
    // get pending list of invoices
    pendingInvoices = ajaxGetRequest("/invoice/findbystatus/pending");
  }
  fillMoreDataIntoDataList(dataListInvoices, pendingInvoices, "invoiceId");
};

// function for load invoice values
const getInvoiceValues = () => {
  let inv = invPayment.invoiceId;
  if (inv != null && inv.length != 0) {
    if (inv.invoiceStatusId.name == "Pending") {
      textInvoiceId.value = inv.invoiceId;
      textInvoiceId.style.border = "2px solid #00FF7F";

      if (inv.customerId != null) {
        textCustomer.value =
          inv.customerId.fullName + " - " + inv.customerId.contact;
        divCustomer.classList.remove("d-none");
      } else {
        divCustomer.classList.add("d-none");
      }

      textTotalAmount.value = parseFloat(inv.total).toFixed(2);
      textGrandTotal.value = parseFloat(inv.grandTotal).toFixed(2);

      if (
        inv.customerId != null &&
        inv.customerId.customerStatusId.name == "Loyalty"
      ) {
        divCreditSell.classList.remove("d-none");
      }

      textDiscount.disabled = false;
      discountPrecentageCheck.disabled = false;
      textPayment.disabled = false;

      let paymethods = divPaymethods.querySelectorAll("input");
      paymethods.forEach((input) => {
        input.disabled = false;
      });
    } else {
      showAlert(
        "warning",
        "Invoice ID " + inv.invoiceId + " is not a pending invoice!"
      );
    }
  } else {
    resetInvoiceDetails();
  }
};

// function for reset invoice values
const resetInvoiceDetails = () => {
  textCustomer.value = "";
  textTotalAmount.value = "";
  textGrandTotal.value = "";
  creditSellCheck.checked = false;
  divCreditSell.classList.add("d-none");
  textDiscount.disabled = true;
  discountPrecentageCheck.disabled = true;
  textPayment.disabled = true;

  textDiscount.value = "";
  textPayment.value = "";
  textBalance.value = "";

  setBorderStyle([textDiscount, textPayment, textBalance]);

  let paymethods = divPaymethods.querySelectorAll("input");
  paymethods.forEach((input) => {
    input.disabled = true;
  });
};

// function for cal discount
const calDisount = () => {
  let discount = 0;
  let total = invPayment.invoiceId.total;
  if (invPayment.discount != null) {
    discount = invPayment.discount;
    textGrandTotal.value = parseFloat(total - discount).toFixed(2);
  } else {
    textGrandTotal.value = parseFloat(total).toFixed(2);
  }

  //bind values
  invPayment.invoiceId.discount = discount;
  invPayment.invoiceId.grandTotal = parseFloat(textGrandTotal.value);
};

// fucntion for cal payment and balance
const calPayment = () => {
  let numberWithdecimals = "^(([1-9]{1}[0-9]{0,7})|([0-9]{0,8}[.][0-9]{2}))$";
  let balance = "";
  let fieldValue = textPayment.value;

  if (fieldValue != "" && new RegExp(numberWithdecimals).test(fieldValue)) {
    textPayment.style.border = "2px solid #00FF7F";
    balance = parseFloat(fieldValue) - parseFloat(textGrandTotal.value);
  } else {
    textPayment.value = "0";
    balance = -parseFloat(textGrandTotal.value).toFixed(2);
  }

  if (balance >= 0) {
    textBalance.style.border = "1px solid #ced4da";
    invPayment.paidAmount = parseFloat(textGrandTotal.value);
    creditSellCheck.checked = false;
    invPayment.invoiceId.isCredit = false;
  } else if (balance < 0) {
    if (creditSellCheck.checked) {
      invPayment.invoiceId.isCredit = true;
      textBalance.style.border = "1px solid #ced4da";
      invPayment.paidAmount = parseFloat(textPayment.value);
    } else {
      invPayment.invoiceId.isCredit = false;
      textBalance.style.border = "1px solid red";
      invPayment.paidAmount = null;
    }
  }
  textBalance.value = parseFloat(balance).toFixed(2);
  console.log(invPayment);
};

//function for check errors
const checkErrors = () => {
  //need to check all required property fields
  let error = "";

  if (invPayment.invoiceId == null) {
    error = error + "Please Enter Invoice to Make Payment...!\n";
    textInvoiceId.style.border = "1px solid red";
  }

  if (!invPayment.invoiceId.isCredit && invPayment.paymethodId == null) {
    error = error + "Please Select Payment Method...!\n";
  }

  if (
    invPayment.invoiceId.isCredit &&
    invPayment.paidAmount != 0 &&
    invPayment.paymethodId == null
  ) {
    error = error + "Please Select Payment Method...!\n";
  }
  if (invPayment.paidAmount == null) {
    error = error + "Please Enter Valid Payment Amount...!\n";
    textPayment.style.border = "1px solid red";
  }

  return error;
};

//function for add record
const addRecord = () => {
  //check form errors -
  let formErrors = checkErrors();
  if (formErrors == "") {
    //get user confirmation
    let title = "Are you sure to add following payment..?\n";
    let message =
      "Payment Amount : Rs." + parseFloat(invPayment.paidAmount).toFixed(2);
    showConfirm(title, message).then((userConfirm) => {
      if (userConfirm) {
        //pass data into back end
        let serverResponse = ajaxRequestBody(
          "/invoicepayment",
          "POST",
          invPayment
        ); // url,method,object

        //check back end response
        if (serverResponse == "OK") {
          showAlert("success", "Payment Save successfully..!").then(() => {
            //need to refresh table and form
            refreshAll();
          });
        } else {
          showAlert(
            "error",
            "Payment save not successfully..! \n" + serverResponse
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
    showAlert("error", "Cannot update!!!\n" + errors);
  }
};

// ********* TABLE OPERATIONS *********

//function for refresh table records
const refreshTable = () => {
  //array for store data list
  customers = ajaxGetRequest("/customer/findall");

  //object count = table column count
  //String - number/string/date
  //function - object/array/boolean
  //currency - RS
  const displayProperties = [
    { property: "fullName", datatype: "String" },
    { property: "contact", datatype: "String" },
    { property: "nic", datatype: "String" },
    { property: "address", datatype: "String" },
    { property: getStatus, datatype: "function" },
  ];

  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys, user privileges)
  fillDataIntoTable(
    customerTable,
    customers,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    true,
    userPrivilages
  );

  //hide delete button when status is 'deleted'
  customers.forEach((customer, index) => {
    if (userPrivilages.delete && customer.customerStatusId.name == "Deleted") {
      //catch the button
      let targetElement =
        customerTable.children[1].children[index].children[6].children[
          userPrivilages.update && userPrivilages.insert ? 2 : 1
        ];
      //add changes
      targetElement.style.pointerEvents = "none";
      targetElement.style.visibility = "hidden";
    }
  });

  $("#customerTable").dataTable();
};

// function for get status
const getStatus = (rowObject) => {
  if (rowObject.customerStatusId.name == "Loyalty") {
    return (
      '<p class = "status status-active">' +
      rowObject.customerStatusId.name +
      "</p>"
    );
  } else if (rowObject.customerStatusId.name == "Normal") {
    return (
      '<p class = "status status-warning">' +
      rowObject.customerStatusId.name +
      "</p>"
    );
  } else if (rowObject.customerStatusId.name == "Deleted") {
    return (
      '<p class = "status status-error">' +
      rowObject.customerStatusId.name +
      "</p>"
    );
  }
};

//function for view record
const viewRecord = (rowObject, rowId) => {
  //need to get full object
  let printObj = rowObject;

  tdCustomerName.innerText = printObj.fullName;
  tdCustomerContact.innerText = printObj.contact;
  tdCustomerNIC.innerText = printObj.nic;
  tdCustomerAddress.innerText = printObj.address;
  tdStatus.innerText = printObj.customerStatusId.name;

  //open model
  $("#modelDetailedView").modal("show");
};

//function for refill record
const refillRecord = (rowObject, rowId) => {
  $("#addNewButton").click();

  customer = JSON.parse(JSON.stringify(rowObject)); //convert rowobject to json string and covert back it to js object
  oldcustomer = JSON.parse(JSON.stringify(rowObject)); // deep copy - create compeletely indipended two objects

  textCustomerName.value = customer.fullName;
  textCustomerContact.value = customer.contact;

  //set optional fields
  textCustomerNIC.value = customer.nic ?? "";
  textCustomerAddress.value = customer.address ?? "";

  // set status
  fillDataIntoSelect(
    selectStatus,
    "Select Status",
    statuses,
    "name",
    customer.customerStatusId.name
  );

  setBorderStyle([
    textCustomerName,
    textCustomerContact,
    textCustomerNIC,
    textCustomerAddress,
    selectStatus,
  ]);

  //manage buttons
  manageFormButtons("refill", userPrivilages);
};

// //function for delete record
const deleteRecord = (rowObject, rowId) => {
  //get user confirmation
  let title = "Are you sure!\nYou wants to delete following record? \n";
  let message =
    "Customer Name : " +
    rowObject.fullName +
    "\nContact No. :" +
    rowObject.contact;

  showConfirm(title, message).then((userConfirm) => {
    if (userConfirm) {
      //response from backend ...
      let serverResponse = ajaxRequestBody("/customer", "DELETE", rowObject); // url,method,object
      //check back end response
      if (serverResponse == "OK") {
        showAlert("success", "Customer Delete successfully..!").then(() => {
          // Need to refresh table and form
          refreshAll();
        });
      } else {
        showAlert(
          "error",
          "Customer delete not successfully..! have some errors \n" +
            serverResponse
        );
      }
    }
  });
};

// ********* PRINT OPERATIONS *********

//print function
const printViewRecord = () => {
  newTab = window.open();
  newTab.document.write(
    //  link bootstrap css
    "<head><title>Print Customer</title>" +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
      "<h2 style = 'font-weight:bold'>Customer Details</h2>" +
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
    "<head><title>Print Customers</title>" +
      '<script src="resources/js/jquery.js"></script>' +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
      "<h2 style = 'font-weight:bold'>Customers Details</h2>" +
      customerTable.outerHTML +
      '<script>$(".modify-button").css("display","none")</script>'
  );

  setTimeout(function () {
    newTab.print();
  }, 1000);
};
