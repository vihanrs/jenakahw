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
  }
  //  else {
  //   showDefaultSection("viewAllButton", "viewAllSection");
  //   addAccordion.style.display = "none";
  // }
  //call all event listners
  addEventListeners();
});

// ********* LISTENERS *********
const addEventListeners = () => {
  let numberWithdecimals = "^(([1-9]{1}[0-9]{0,7})|([0-9]{0,8}[.][0-9]{2}))$";

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

  textPayment.addEventListener("keyup", () => {
    textFieldValidator(
      textPayment,
      numberWithdecimals,
      "cusPayment",
      "paidAmount"
    ),
      calBalance();
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
  cusPayment = {};
  // invCusPayment = {};

  createViewPayMethodUI();
  refreshIncompelteInvoiceTable();

  //empty all elements
  textCustomer.value = "";
  textBalance.value = "";
  textPayment.value = "";

  setBorderStyle([textCustomer, textPayment]);

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
          textPayment.value =
            textTotalBalance.value != ""
              ? parseFloat(textTotalBalance.value).toFixed(2)
              : 0;
          textPayment.style.border = "1px solid #ced4da";
          textPayment.disabled = true;
          calBalance();
        } else {
          textPayment.disabled = false;
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
  if (cusPayment.customer != null) {
    customerid = cusPayment.customer.id;

    incompleteInvoicesByCustomer = ajaxGetRequest(
      "/invoice/findincompletebycustomer/" + customerid
    );

    if (incompleteInvoicesByCustomer.length != 0) {
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
    } else {
      showAlert("warning", "No due payments");
    }
  }
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
  let paidAmount = textPayment.value;
  let totalBalance = textTotalBalance.value;
  let balance = 0;

  if (paidAmount != "" && cusPayment.paidAmount != null) {
    // cal balance amount
    balance = parseFloat(paidAmount) - parseFloat(totalBalance);

    // if paid amount lower than total payable balance
    if (balance < 0) {
      lblBalance.innerText = "Due (Rs.):";
      cusPayment.paidAmount = parseFloat(paidAmount);
      cusPayment.balance = -balance;
    } else {
      lblBalance.innerText = "Balance (Rs.):";
      cusPayment.paidAmount = parseFloat(totalBalance);
      cusPayment.balance = 0;
    }
    textBalance.value = parseFloat(balance).toFixed(2);

    // bind value
  } else {
    textBalance.value = "";
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

//function for view record
const viewRecord = (rowObject, rowId) => {
  //need to get full object
  // let printObj = rowObject;
  // tdCustomerName.innerText = printObj.fullName;
  // tdCustomerContact.innerText = printObj.contact;
  // tdCustomerNIC.innerText = printObj.nic;
  // tdCustomerAddress.innerText = printObj.address;
  // tdStatus.innerText = printObj.customerStatusId.name;
  // //open model
  // $("#modelDetailedView").modal("show");
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
