//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest("/privilege/byloggeduserandmodule/Customer");

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
  let namePattern = "^[A-Z][A-Za-z ]{1,19}[A-Za-z]$";
  let contactPattern = "^[0][7][01245678][0-9]{7}$";
  let nicPattern = "^(([0-9]{9}[Vv])|([2][0][0-9]{2}[0-9]{8}))$";
  // textCustomerName;
  // textCustomerContact;
  // textCustomerNIC;
  // textCustomerAddress;
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
    );
  });

  textCustomerNIC.addEventListener("keyup", () => {
    textFieldValidator(textCustomerNIC, nicPattern, "customer", "nic");
  });

  textCustomerAddress.addEventListener("keyup", () => {
    textFieldValidator(textCustomerAddress, "", "customer", "address");
  });

  selectStatus.addEventListener("change", () => {
    selectDFieldValidator(selectStatus, "customer", "customerStatusId");
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

  // get status
  statuses = ajaxGetRequest("/customerstatus/findall");
  statuseswithoutdelete = statuses.filter((status) => status.name != "Deleted");

  fillDataIntoSelect(
    selectStatus,
    "Select Status",
    statuseswithoutdelete,
    "name"
  );

  //empty all elements
  textCustomerName.value = "";
  textCustomerContact.value = "";
  textCustomerNIC.value = "";
  textCustomerAddress.value = "";
  selectStatus.value = "";

  //set default border color
  let elements = [
    textCustomerName,
    textCustomerContact,
    textCustomerNIC,
    textCustomerAddress,
    selectStatus,
  ];
  setBorderStyle(elements);

  //manage form buttons
  manageFormButtons("insert", userPrivilages);
};

//function for check errors
const checkErrors = () => {
  //need to check all required property fields
  let error = "";

  if (customer.fullName == null) {
    error = error + "Please Enter Customer Name...!\n";
    textCustomerName.style.border = "1px solid red";
  }
  if (customer.contact == null) {
    error = error + "Please Enter Customer Contact No...!\n";
    textCustomerContact.style.border = "1px solid red";
  }

  if (customer.customerStatusId == null) {
    error = error + "Please Select Valid Status...!\n";
    selectStatus.style.border = "1px solid red";
  }

  return error;
};

//function for check updates
const checkUpdates = () => {
  let updates = "";

  if (oldcustomer.name != customer.name) {
    updates +=
      "Name has changed " + oldcustomer.name + " into " + customer.name + " \n";
  }
  if (oldcustomer.contact != customer.contact) {
    updates +=
      "Contact No. has changed " +
      oldcustomer.contact +
      " into " +
      customer.contact +
      " \n";
  }

  if (oldcustomer.nic != customer.nic) {
    updates +=
      "NIC has changed " +
      (oldcustomer.nic ?? "-") + //nullish coalescing operator --> return right side operand when left side is null or undefined
      " into " +
      (customer.nic ?? "-") +
      " \n";
  }

  if (oldcustomer.address != customer.address) {
    updates +=
      "Address has changed " +
      (oldcustomer.address ?? "-") + //nullish coalescing operator --> return right side operand when left side is null or undefined
      " into " +
      (customer.address ?? "-") +
      " \n";
  }

  if (oldcustomer.customerStatusId.id != customer.customerStatusId.id) {
    updates +=
      "Status has changed " +
      oldcustomer.customerStatusId.name +
      " into " +
      customer.customerStatusId.name +
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
      "Customer Name : " +
      customer.fullName +
      "\nContact : " +
      customer.contact +
      "\nStatus : " +
      customer.customerStatusId.name;
    showConfirm(title, message).then((userConfirm) => {
      if (userConfirm) {
        //pass data into back end
        let serverResponse = ajaxRequestBody("/customer", "POST", customer); // url,method,object

        //check back end response
        if (serverResponse == "OK") {
          showAlert("success", "Customer Save successfully..!").then(() => {
            //need to refresh table and form
            refreshAll();
          });
        } else {
          showAlert(
            "error",
            "Customer save not successfully..! have some errors \n" +
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

  // let table = new DataTable("#customerTable");
  // table.destroy();

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
      '<script>$("#modifyButtons").css("display","none");$(".table-buttons").hide();</script>'
  );

  setTimeout(function () {
    newTab.print();
  }, 1000);
};
