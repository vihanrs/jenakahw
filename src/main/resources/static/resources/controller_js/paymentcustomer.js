//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest(
    "/privilege/byloggeduserandmodule/Customer Payment"
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
  textCustomer.addEventListener("keyup", () => {
    getCustomerList();
  });

  textCustomer.addEventListener("input", () => {
    dataListValidator(
      textCustomer,
      "loyaltyCustomers",
      "cusPayment",
      "customer",
      "contact",
      true
    ),
      refreshIncompelteInvoiceTable();
  });

  selectUser.addEventListener("change", () => {
    filterPaymentsByUser();
  });

  textPayment.addEventListener("keyup", () => {
    calBalance();
  });

  //form reset button function call
  btnUserReset.addEventListener("click", () => {
    selectUser.value = "";
    filterPaymentsByUser();
  });

  //form reset button function call
  btnReset.addEventListener("click", () => {
    refreshForm();
  });

  //record update function call
  btnUpdate.addEventListener("click", () => {
    // updateRecord();
  });

  //record save function call
  btnAdd.addEventListener("click", () => {
    addRecord();
  });

  // record print function call
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
  filterPaymentsByUser();
  refreshTable();
};

// ********* FORM OPERATIONS *********

//function for refresh form area
const refreshForm = () => {
  //create empty object
  cusPayment = {};
  // invCusPayment = {};

  createViewPayMethodUI();
  refreshIncompelteInvoiceTable();

  //empty all elements
  textCustomer.value = "";
  textBalance.value = "";
  textPayment.value = "";
  textTotalBalance.value = "";

  setBorderStyle([textCustomer, textPayment, textBalance]);

  //get all users
  if (userRole == "Admin" || userRole == "Manager") {
    divUserFilter.classList.remove("d-none");
    users = ajaxGetRequest("/user/findallusers");
    fillDataIntoSelect(selectUser, "Select User", users, "username");
  } else {
    divUserFilter.classList.add("d-none");
  }
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
        cusPayment.paymethodId = paymethod;
        if (
          cusPayment.paymethodId.name == "Card" ||
          cusPayment.paymethodId.name == "Cheque"
        ) {
          textPayment.value = parseFloat(textTotalBalance.value).toFixed(2);
          calBalance();
        } else {
          calBalance();
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
const getCustomerList = () => {
  loyaltyCustomers = ajaxGetRequest("/customer/findbystatus/loyalty");

  fillMoreDataIntoDataList(
    dataListCustomers,
    loyaltyCustomers,
    "contact",
    "fullName"
  );
};

// function for refresh invoice table
const refreshIncompelteInvoiceTable = () => {
  let customerid = 0;
  incompleteInvoicesByCustomer = [];
  if (cusPayment.customer != null) {
    customerid = cusPayment.customer.id;

    incompleteInvoicesByCustomer = ajaxGetRequest(
      "/invoice/findincompletebycustomer/" + customerid
    );

    if (incompleteInvoicesByCustomer.length == 0) {
      showAlert("warning", "No due payments");
    }
  }

  const displayProperties = [
    { property: "invoiceId", datatype: "String" },
    { property: "grandTotal", datatype: "currency" },
    { property: "paidAmount", datatype: "currency" },
    { property: "balanceAmount", datatype: "currency" },
  ];

  //call the function (tableID,dataList,display property list,refill function name, delete function name, button visibilitys)
  fillDataIntoInnerTable(
    incompleteInvoicesTable,
    incompleteInvoicesByCustomer,
    displayProperties,
    refillInvoice,
    deleteInvoice,
    false
  );

  getTotalBalance();
};

const refillInvoice = () => {};
const deleteInvoice = () => {};

// function for get total balance of loaded invoices
const getTotalBalance = () => {
  let totBalance = 0;
  incompleteInvoicesByCustomer.forEach((inv) => {
    totBalance += parseFloat(inv.balanceAmount);
  });
  textTotalBalance.value = parseFloat(totBalance).toFixed(2);
};

// fucntion for cal payment and balance
const calBalance = () => {
  let numberWithdecimals = "^(([1-9]{1}[0-9]{0,7})|([0-9]{0,8}[.][0-9]{2}))$";
  let balance = "";
  let fieldValue = textPayment.value;

  if (fieldValue != "" && new RegExp(numberWithdecimals).test(fieldValue)) {
    if (
      typeof cusPayment.paymethodId !== "undefined" &&
      (cusPayment.paymethodId.name == "Card" ||
        cusPayment.paymethodId.name == "Cheque") &&
      parseFloat(fieldValue) > parseFloat(textTotalBalance.value)
    ) {
      textPayment.style.border = "1px solid red";
      cusPayment.paidAmount = null;
      cusPayment.balance = null;
      textBalance.value = parseFloat(textTotalBalance.value).toFixed(2);
    } else {
      textPayment.style.border = "2px solid #00FF7F";
      balance = parseFloat(fieldValue) - parseFloat(textTotalBalance.value);
      textBalance.value = Math.abs(parseFloat(balance)).toFixed(2);

      if (balance >= 0) {
        lblBalance.innerText = "Balance (Rs.):";
        cusPayment.paidAmount = parseFloat(textTotalBalance.value);
        cusPayment.balance = 0;
      } else {
        lblBalance.innerText = "Due (Rs.):";
        cusPayment.balance = balance;
        cusPayment.paidAmount = parseFloat(textPayment.value);
      }
    }
  } else {
    cusPayment.paidAmount = null;
    cusPayment.balance = null;
    textPayment.style.border = "1px solid red";
    lblBalance.innerText = "Due (Rs.):";
    textBalance.value = parseFloat(textTotalBalance.value).toFixed(2);
  }
  console.log(cusPayment);
};

//function for check errors
const checkErrors = () => {
  //need to check all required property fields
  let error = "";

  if (cusPayment.customer == null) {
    error = error + "Please Select Customer...!\n";
    textCustomer.style.border = "1px solid red";
  }
  if (cusPayment.paymethodId == null) {
    error = error + "Please Select Payment Method...!\n";
  }
  if (cusPayment.paidAmount == null) {
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
      "Payment Amount : Rs." +
      parseFloat(cusPayment.paidAmount).toFixed(2) +
      "\nPayment Method : " +
      cusPayment.paymethodId.name;
    showConfirm(title, message).then((userConfirm) => {
      if (userConfirm) {
        //pass data into back end
        let serverResponse = ajaxRequestBody(
          "/customerpayment",
          "POST",
          cusPayment
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
const updateRecord = () => {};

// ********* TABLE OPERATIONS *********

//function for refresh table records
const refreshTable = () => {
  //object count = table column count
  //String - number/string/date
  //function - object/array/boolean
  //currency - RS
  const displayProperties = [
    { property: "paymentInvoiceId", datatype: "String" },
    { property: getCustomer, datatype: "function" },
    { property: getAddedDate, datatype: "function" },
    { property: getPaymethod, datatype: "function" },
    { property: "paidAmount", datatype: "currency" },
  ];

  let table = new DataTable("#customerPaymentsTable");
  table.destroy();

  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys, user privileges)
  fillDataIntoTable(
    customerPaymentsTable,
    customerPayments,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    true,
    userPrivilages
  );

  // hide the refill button
  customerPayments.forEach((obj, index) => {
    if (userPrivilages.update) {
      let targetElement =
        customerPaymentsTable.children[1].children[index].children[6]
          .children[1];
      //add changes
      targetElement.style.pointerEvents = "none";
      targetElement.style.visibility = "hidden";
      targetElement.style.display = "none";
    }

    if (userPrivilages.delete) {
      let targetElement =
        customerPaymentsTable.children[1].children[index].children[6]
          .children[2];
      //add changes
      targetElement.style.pointerEvents = "none";
      targetElement.style.visibility = "hidden";
      targetElement.style.display = "none";
    }
  });

  $("#customerPaymentsTable").dataTable();
};

// function for get customer
const getCustomer = (rowObject) => {
  return rowObject.customer.fullName;
};

// function for get paymethod
const getPaymethod = (rowObject) => {
  if (rowObject.paymethodId.name == "Cash") {
    return (
      '<p class = "status status-active">' + rowObject.paymethodId.name + "</p>"
    );
  } else if (rowObject.paymethodId.name == "Card") {
    return (
      '<p class = "status btn-plus">' + rowObject.paymethodId.name + "</p>"
    );
  } else if (rowObject.paymethodId.name == "Cheque") {
    return (
      '<p class = "status status-warning">' +
      rowObject.paymethodId.name +
      "</p>"
    );
  }
};

// function for get paid date
const getAddedDate = (rowObject) => {
  return rowObject.addedDateTime.split("T")[0];
};

//function for filter table by user
const filterPaymentsByUser = () => {
  //array for store data list
  customerPayments = ajaxGetRequest("/customerpayment/findall");

  if (selectUser.value != "") {
    const userId = JSON.parse(selectUser.value).id;

    customerPayments = ajaxGetRequest(
      "/customerpayment/findallbyuser/" + userId
    );
  }

  refreshTable();
};

//function for refill record
const refillRecord = (rowObject, rowId) => {
  //manage buttons
  // manageFormButtons("refill", userPrivilages);
};

//function for delete record
const deleteRecord = (rowObject, rowId) => {
  //get user confirmation
  // let title = "Are you sure!\nYou wants to delete following record? \n";
  // let message =
  //   "Customer Name : " +
  //   rowObject.fullName +
  //   "\nContact No. :" +
  //   rowObject.contact;
  // showConfirm(title, message).then((userConfirm) => {
  //   if (userConfirm) {
  //     //response from backend ...
  //     let serverResponse = ajaxRequestBody("/customer", "DELETE", rowObject); // url,method,object
  //     //check back end response
  //     if (serverResponse == "OK") {
  //       showAlert("success", "Customer Delete successfully..!").then(() => {
  //         // Need to refresh table and form
  //         refreshAll();
  //       });
  //     } else {
  //       showAlert(
  //         "error",
  //         "Customer delete not successfully..! have some errors \n" +
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

  tdInvoiceId.innerText = printObj.paymentInvoiceId;
  tdCustomer.innerText = printObj.customer.fullName;
  tdInvoicedDate.innerText = printObj.addedDateTime.split("T")[0];
  tdPayMethod.innerText = printObj.paymethodId.name;
  tdPaid.innerText = "Rs." + parseFloat(printObj.paidAmount).toFixed(2);
  getGRNPaymentBySupPaymentForPrint(printObj.id);
  //open model
  $("#modelDetailedView").modal("show");
};

//function for get customer payment related invoice payment details
const getGRNPaymentBySupPaymentForPrint = (payId) => {
  // remove the previously added dynamic rows
  document.querySelectorAll(".dynamic-row").forEach((row) => row.remove());

  //get invoice payment details
  customerPayments = ajaxGetRequest(
    "/customerpayment/findinvpaymentsbycustomerpayment/" + payId
  );

  customerPayments.forEach((ele) => {
    const tr = document.createElement("tr");
    tr.classList.add("dynamic-row");
    const tdInvoiceId = document.createElement("td");
    const tdAmount = document.createElement("td");

    tdInvoiceId.innerText = ele.invoiceId.invoiceId;
    tdAmount.innerText = "Rs." + parseFloat(ele.paidAmount).toFixed(2);

    tr.appendChild(tdInvoiceId);
    tr.appendChild(tdAmount);
    printTable.appendChild(tr);
  });
};

// ********* PRINT OPERATIONS *********

//print function
const printViewRecord = () => {
  newTab = window.open();
  newTab.document.write(
    //  link bootstrap css
    "<head><title>Print Customer Payment</title>" +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
      "<h2 style = 'font-weight:bold'>Customer Payment Details</h2>" +
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
    "<head><title>Print Customer Payments</title>" +
      '<script src="resources/js/jquery.js"></script>' +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
      "<h2 style = 'font-weight:bold'>Customer Payments</h2>" +
      customerPaymentsTable.outerHTML +
      '<script>$("#modifyButtons").css("display","none");$(".table-buttons").hide();</script>'
  );

  setTimeout(function () {
    newTab.print();
  }, 1000);
};
