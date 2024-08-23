//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest(
    "/privilege/byloggeduserandmodule/Invoice Payment"
  );

  manageNavBar();
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
    // updateRecord();
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

  if (invPayment.paymethodId != null && invPayment.paymethodId.name != "Cash") {
    textPayment.value = parseFloat(textGrandTotal.value).toFixed(2);
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

  if (fieldValue != "") {
    if (new RegExp(numberWithdecimals).test(fieldValue)) {
      textPayment.style.border = "2px solid #00FF7F";
      balance = parseFloat(fieldValue) - parseFloat(textGrandTotal.value);
      if (
        typeof invPayment.paymethodId !== "undefined" &&
        (invPayment.paymethodId.name == "Card" ||
          invPayment.paymethodId.name == "Cheque") &&
        parseFloat(fieldValue) > parseFloat(textGrandTotal.value)
      ) {
        textPayment.style.border = "1px solid red";
        balance = -parseFloat(textGrandTotal.value);
      }
    } else {
      balance = -parseFloat(textGrandTotal.value);
      textPayment.style.border = "1px solid red";
    }
  } else {
    balance = -parseFloat(textGrandTotal.value);
    textPayment.style.border = "1px solid #ced4da";
  }
  if (balance >= 0) {
    textBalance.style.border = "1px solid #ced4da";
    invPayment.paidAmount = parseFloat(textGrandTotal.value);
    creditSellCheck.checked = false;
    invPayment.invoiceId.isCredit = false;
    lblBalance.innerText = "Balance (Rs.) :";
  } else if (balance < 0) {
    lblBalance.innerText = "Due (Rs.) :";
    if (creditSellCheck.checked) {
      invPayment.invoiceId.isCredit = true;
      textBalance.style.border = "1px solid #ced4da";
      invPayment.paidAmount =
        textPayment.value != "" ? parseFloat(textPayment.value) : 0;
    } else {
      invPayment.invoiceId.isCredit = false;
      textBalance.style.border = "1px solid red";
      invPayment.paidAmount = null;
    }
  }
  textBalance.value = parseFloat(Math.abs(balance)).toFixed(2);
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
        if (new RegExp("^[A-Z]{3}[0-9]{9}$").test(serverResponse)) {
          showAlert("success", "Payment Save successfully..!").then(() => {
            //need to refresh table and form
            printInvoice(serverResponse);
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

// function for print invoice
const printInvoice = (invoiceId) => {
  invoice = ajaxGetRequest("/invoice/findbyid/" + invoiceId);

  let printObj = invoice;

  tdInvoiceId.innerText = printObj.invoiceId;
  tdCustomer.innerText =
    printObj.customerId != null ? printObj.customerId.fullName : "";
  tdItemCount.innerText = printObj.itemCount;
  tdInvoicedDate.innerText = printObj.addedDateTime.split("T")[0];
  tdInvoiceType.innerText =
    printObj.isCredit != true ? "Normal Invoice" : "Credit Invoice";
  tdTotal.innerText = "Rs." + parseFloat(printObj.total).toFixed(2);
  tdDiscount.innerText =
    "Rs." + parseFloat(printObj.discount ?? "0").toFixed(2);
  tdGrandTotal.innerText = "Rs." + parseFloat(printObj.grandTotal).toFixed(2);
  tdPaid.innerText = "Rs." + parseFloat(printObj.paidAmount).toFixed(2);
  tdBalance.innerText = "Rs." + parseFloat(printObj.balanceAmount).toFixed(2);
  tdStatus.innerText = printObj.invoiceStatusId.name;
  getINVProductsForPrint(printObj);

  newTab = window.open();
  newTab.document.write(
    //  link bootstrap css
    "<head><title>Print Invoice</title>" +
      "<h2 style = 'font-weight:bold'>Jenaka Hardware Avissawella</h2>" +
      printTable.outerHTML
  );

  //triger print() after 1000 milsec time out - time to load content to the printing tab
  setTimeout(function () {
    newTab.print();
    newTab.close(); // Close the tab after printing
  }, 1000);
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
