//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest("/privilege/byloggeduserandmodule/Supplier");

  //set default selected section
  if (userPrivilages.insert) {
    showDefaultSection("addNewButton", "addNewSection");
  } else {
    showDefaultSection("viewAllButton", "viewAllSection");
    addAccordion.style.display = "none";
  }
  //refresh all
  refreshAll();

  //call all event listners
  addEventListeners();
});

// ********* LISTENERS *********
const addEventListeners = () => {
  let namePattern = "^[a-zA-Z][a-z]{1,29}$"; //first letter can be capital or simple and other letters need to be simple
  let contactPattern = "^[0][7][01245678][0-9]{7}$";
  let emailPattern = "^[A-Za-z0-9]{4,20}[@][a-z]{3,10}[.][a-z]{2,3}$";
  let companyNamePattern = "^[a-zA-Z](?:[a-zA-Z() ]{1,28}[a-zA-Z)])?$"; //first/last letters can be capital or simple and middle can be accept spaces

  textFirstName.addEventListener("keyup", () => {
    textFieldValidator(textFirstName, namePattern, "supplier", "firstName");
  });

  textLastName.addEventListener("keyup", () => {
    textFieldValidator(textLastName, namePattern, "supplier", "lastName");
  });

  textContact.addEventListener("keyup", () => {
    textFieldValidator(textContact, contactPattern, "supplier", "contact");
  });

  textEmail.addEventListener("keyup", () => {
    textFieldValidator(textEmail, emailPattern, "supplier", "email");
  });

  textCompany.addEventListener("keyup", () => {
    textFieldValidator(textCompany, companyNamePattern, "supplier", "company");
  });

  textAddress.addEventListener("keyup", () => {
    textFieldValidator(textAddress, "^.*$", "supplier", "address");
  });

  selectStatus.addEventListener("change", () => {
    selectDFieldValidator(selectStatus, "supplier", "supplierStatusId");
  });

  //bank details event listners
  textBankName.addEventListener("keyup", () => {
    textFieldValidator(
      textBankName,
      "^[a-zA-Z' ]{2,}[a-zA-Z]$",
      "bankDetail",
      "bankName"
    );
  });

  textBranchName.addEventListener("keyup", () => {
    textFieldValidator(
      textBranchName,
      "^[a-zA-Z ]{2,}[a-zA-Z]$",
      "bankDetail",
      "branchName"
    );
  });

  textAccNo.addEventListener("keyup", () => {
    textFieldValidator(textAccNo, "^[0-9]{5,}$", "bankDetail", "accNo");
  });

  textAccHolderName.addEventListener("keyup", () => {
    textFieldValidator(
      textAccHolderName,
      "^[a-zA-Z ]{2,}[a-zA-Z]$",
      "bankDetail",
      "accHolderName"
    );
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

  // list trasform buttons
  btnSingleAdd.addEventListener("click", () => {
    addOneProduct();
  });

  btnAddAll.addEventListener("click", () => {
    addAllProducts();
  });

  btnSingleRemove.addEventListener("click", () => {
    removeOneProduct();
  });

  btnRemoveAll.addEventListener("click", () => {
    removeAllProducts();
  });

  btnAddBankDetail.addEventListener("click", () => {
    addBankDetail();
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
  supplier = {};
  supplier.products = new Array();
  supplier.bankDetails = new Array();

  //get data list for select element
  supplierStatus = ajaxGetRequest("/supplierstatus/findall");

  fillDataIntoSelect(
    selectStatus,
    "Select Status",
    supplierStatus,
    "name",
    "Active"
  );

  //bind default selected status in to supplier object and set valid color
  supplier.supplierStatusId = JSON.parse(selectStatus.value);
  selectStatus.style.border = "2px solid #00FF7F";

  //get data list for select element (left side list)
  availableProductList = ajaxGetRequest("/product/availablelist");
  fillMoreDataIntoSelect(
    selectAllProducts,
    "",
    availableProductList,
    "barcode",
    "name"
  );

  //set selected product list empty (right side list)
  fillMoreDataIntoSelect(
    selectedProducts,
    "",
    supplier.products,
    "barcode",
    "name"
  );

  //empty all elements
  textFirstName.value = "";
  textLastName.value = "";
  textContact.value = "";
  textEmail.value = "";
  textCompany.value = "";
  textAddress.value = "";

  //set default border color
  let elements = [
    textFirstName,
    textLastName,
    textContact,
    textEmail,
    textCompany,
    textAddress,
  ];

  setBorderStyle(elements);

  refreshInnerFormAndTable();

  //manage buttons
  manageFormButtons("insert", userPrivilages);
};

//function for refresh inner product form/table area
const refreshInnerFormAndTable = () => {
  bankDetail = {};

  //empty all elements
  textBankName.value = "";
  textBranchName.value = "";
  textAccNo.value = "";
  textAccHolderName.value = "";

  //set default border color
  let elements = [textBankName, textBranchName, textAccNo, textAccHolderName];
  setBorderStyle(elements);

  const displayProperties = [
    { property: "bankName", datatype: "String" },
    { property: "branchName", datatype: "String" },
    { property: "accNo", datatype: "String" },
    { property: "accHolderName", datatype: "String" },
  ];

  //call the function (tableID,dataList,display property list,refill function name, delete function name, button visibilitys)
  fillDataIntoInnerTable(
    bankDetailsTable,
    supplier.bankDetails,
    displayProperties,
    refillBankDetail,
    deleteBankDetail
  );
};

// ********* INNER FORM/TABLE OPERATIONS *********

//function for refill selected bank detail
const refillBankDetail = (rowObject) => {
  //remove refilled bank detail from supplier.bankDetails
  supplier.bankDetails = supplier.bankDetails.filter(
    (bankdetail) => bankdetail.id !== rowObject.id
  );

  // refresh inner table
  refreshInnerFormAndTable();

  //fill product data into relavent fields
  bankDetail = JSON.parse(JSON.stringify(rowObject));
  textBankName.value = bankDetail.bankName;
  textBranchName.value = bankDetail.branchName;
  textAccNo.value = bankDetail.accNo;
  textAccHolderName.value = bankDetail.accHolderName;

  //set valid border color
  let elements = [textBankName, textBranchName, textAccNo, textAccHolderName];
  setBorderStyle(elements, "2px solid #00FF7F");
};

//function for delete selected bank detail
const deleteBankDetail = (rowObject) => {
  // get user confirmation
  let userConfirm = window.confirm(
    "Are you sure you want to delete this bank detail...?\n" +
      rowObject.bankName +
      " - " +
      rowObject.accNo
  );

  if (userConfirm) {
    //remove deleted bank detail from supplier.bankDetails
    supplier.bankDetails = supplier.bankDetails.filter(
      (bankdetail) => bankdetail.id !== rowObject.id
    );
  }
  // refresh inner table
  refreshInnerFormAndTable();
};
//function for refresh table records
const refreshTable = () => {
  //array for store data list
  suppliers = ajaxGetRequest("/supplier/findall");

  //object count = table column count
  //String - number/string/date
  //function - object/array/boolean
  const displayProperties = [
    { property: getSupplierFullName, datatype: "function" },
    { property: "company", datatype: "String" },
    { property: "contact", datatype: "String" },
    { property: "email", datatype: "String" },
    { property: getSupplierStatus, datatype: "function" },
  ];

  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys,user privileges)
  fillDataIntoTable(
    supplierTable,
    suppliers,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    true,
    userPrivilages
  );

  //hide delete button when status is 'resigned'
  suppliers.forEach((supplier, index) => {
    if (userPrivilages.delete && supplier.supplierStatusId.name == "Deleted") {
      //catch the button
      let targetElement =
        supplierTable.children[1].children[index].children[6].children[
          userPrivilages.update && userPrivilages.insert ? 2 : 1
        ];
      //add changes
      targetElement.style.pointerEvents = "none";
      targetElement.style.visibility = "hidden";
    }
  });

  $("#supplierTable").dataTable();
};

//function for check inner form errors
const checkInnerFormErrors = () => {
  let error = "";

  if (bankDetail.bankName == null) {
    error = error + "Please Enter Valid Bank Name...!\n";
    textBankName.style.border = "1px solid red";
  }

  if (bankDetail.branchName == null) {
    error = error + "Please Enter Valid Branch Name...!\n";
    textBranchName.style.border = "1px solid red";
  }

  if (bankDetail.accNo == null) {
    error = error + "Please Enter Valid Acc No...!\n";
    textAccNo.style.border = "1px solid red";
  }

  if (bankDetail.accHolderName == null) {
    error = error + "Please Enter Valid Acc Holder Name...!\n";
    textAccHolderName.style.border = "1px solid red";
  }

  return error;
};

// fucntion for add product to inner table
const addBankDetail = () => {
  // check errors
  let formErrors = checkInnerFormErrors();
  if (formErrors == "") {
    // get user confirmation
    let userConfirm = window.confirm(
      "Are you sure to add following bank detail..?\n" +
        "\nBank : " +
        bankDetail.bankName +
        " - " +
        bankDetail.branchName +
        "\nAcc No. : " +
        bankDetail.accNo +
        "\nAcc Holder Name : " +
        bankDetail.accHolderName
    );

    if (userConfirm) {
      //add object into array
      supplier.bankDetails.push(bankDetail);
      refreshInnerFormAndTable();
    }
  } else {
    alert("Error\n" + formErrors);
  }
};

// ********* TABLE OPERATIONS *********

//function for set stats column
const getSupplierStatus = (rowOb) => {
  if (rowOb.supplierStatusId.name == "Active") {
    return (
      '<p class = "status status-active">' +
      rowOb.supplierStatusId.name +
      "</p>"
    );
  } else if (rowOb.supplierStatusId.name == "Inactive") {
    return (
      '<p class = "status status-warning">' +
      rowOb.supplierStatusId.name +
      "</p>"
    );
  } else {
    return (
      '<p class = "status status-error">' + rowOb.supplierStatusId.name + "</p>"
    );
  }
};

// function for get supplier fullname
const getSupplierFullName = (rowOb) => {
  return rowOb.firstName + " " + (rowOb.lastName != null ? rowOb.lastName : "");
};

//function for view record
const viewRecord = (ob, rowId) => {
  //need to get full object

  const printObj = ob;
  /* EXAMPLES
    tdFullName.innerText = printObj.fullName;
    tdStatus.innerText = printObj.employeeStatusId.name;
    */
  //open model
  $("#modelDetailedView").modal("show");
};

//function for refill record
const refillRecord = (rowObject, rowId) => {
  $("#addNewButton").click();

  supplier = JSON.parse(JSON.stringify(rowObject));
  oldSupplier = JSON.parse(JSON.stringify(rowObject));

  //set data to fields
  textFirstName.value = supplier.firstName;
  textContact.value = supplier.contact;
  textEmail.value = supplier.email;
  textCompany.value = supplier.company;
  textAddress.value = supplier.address;

  //set optional fields
  if (supplier.lastName != null) textLastName.value = supplier.lastName;
  else textLastName.value = "";

  if (supplier.email != null) textEmail.value = supplier.email;
  else textEmail.value = "";

  if (supplier.company != null) textCompany.value = supplier.company;
  else textCompany.value = "";

  if (supplier.address != null) textAddress.value = supplier.address;
  else textAddress.value = "";

  // set status
  fillDataIntoSelect(
    selectStatus,
    "Select Status",
    supplierStatus,
    "name",
    supplier.supplierStatusId.name
  );

  // set supplier product list
  fillMoreDataIntoSelect(
    selectedProducts,
    "",
    supplier.products,
    "barcode",
    "name"
  );

  availableProductList = ajaxGetRequest(
    "/product/availablelistWithoutSupplier/" + supplier.id
  );
  fillMoreDataIntoSelect(
    selectAllProducts,
    "",
    availableProductList,
    "barcode",
    "name"
  );

  //change status border color to default
  setBorderStyle([selectStatus]);

  refreshInnerFormAndTable();
  //manage buttons
  manageFormButtons("refill", userPrivilages);
};

//function for delete record
const deleteRecord = (rowObject, rowId) => {
  //get user confirmation
  const userConfirm = confirm(
    "Are you sure to delete following record? \n" +
      rowObject.firstName +
      " " +
      rowObject.lastName
  );

  if (userConfirm) {
    //response from backend ...
    let serverResponse = ajaxRequestBody("/supplier", "DELETE", rowObject); // url,method,object
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

// ********* FORM OPERATIONS *********

//function for check errors
const checkError = () => {
  //need to check all required property fields
  let error = "";

  if (supplier.firstName == null) {
    error += "Please Enter Valid First Name...!\n";
    textFirstName.style.border = "2px solid red";
  }

  if (supplier.contact == null) {
    error += "Please Enter Valid Contact...!\n";
    textContact.style.border = "2px solid red";
  }

  if (supplier.supplierStatusId == null) {
    error += "Please Select Supplier Status...!\n";
    selectStatus.style.border = "1px solid red";
  }

  if (supplier.products == "") {
    error += "Please Select Supplier Products...!\n";
  }

  return error;
};

//function for check updates
const checkUpdate = () => {
  let updates = "";

  if (oldSupplier.firstName != supplier.firstName) {
    updates +=
      "First Name has changed " +
      oldSupplier.firstName +
      " into " +
      supplier.firstName +
      " \n";
  }

  if (oldSupplier.lastName != supplier.lastName) {
    updates +=
      "Last Name has changed " +
      (oldSupplier.lastName ?? "-") +
      " into " +
      (supplier.lastName ?? "-") +
      " \n";
  }

  if (oldSupplier.contact != supplier.contact) {
    updates +=
      "Contact has changed " +
      oldSupplier.contact +
      " into " +
      supplier.contact +
      " \n";
  }

  if (oldSupplier.email != supplier.email) {
    updates +=
      "Email has changed " +
      (oldSupplier.email ?? "-") +
      " into " +
      (supplier.email ?? "-") +
      " \n";
  }

  if (oldSupplier.company != supplier.company) {
    updates +=
      "Company has changed " +
      (oldSupplier.company ?? "-") +
      " into " +
      (supplier.company ?? "-") +
      " \n";
  }

  if (oldSupplier.address != supplier.address) {
    updates +=
      "Address has changed " +
      (oldSupplier.address ?? "-") +
      " into " +
      (supplier.address ?? "-") +
      " \n";
  }

  if (oldSupplier.supplierStatusId.id != supplier.supplierStatusId.id) {
    updates +=
      "Company has changed " +
      (oldSupplier.supplierStatusId.name ?? "-") +
      " into " +
      (supplier.supplierStatusId.name ?? "-") +
      " \n";
  }
  return updates;
};

//function for add record
const addRecord = () => {
  //check form errors -
  let formErrors = checkError();
  if (formErrors == "") {
    //get user confirmation
    let userConfirm = window.confirm(
      "Are you sure to add following record..?\n" +
        "\nSupplier Name : " +
        supplier.firstName +
        " " +
        (supplier.lastName != null ? supplier.lastName : " ") +
        "\nContact : " +
        supplier.contact
    );

    if (userConfirm) {
      //pass data into back end
      let serverResponse = ajaxRequestBody("/supplier", "POST", supplier); // url,method,object

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
  let errors = checkError();
  if (errors == "") {
    let updates = checkUpdate();
    if (updates != "") {
      let userConfirm = confirm(
        "Are you sure you want to update following changes...?\n" + updates
      );
      if (userConfirm) {
        let updateServiceResponse = ajaxRequestBody(
          "/supplier",
          "PUT",
          supplier
        );
        if (updateServiceResponse == "OK") {
          alert("Update sucessfully..! ");
          //need to refresh table and form
          refreshAll();
        } else {
          alert(
            "Update not sucessfully..! have some errors \n" +
              updateServiceResponse
          );
        }
      }
    } else {
      alert("Nothing to Update...!");
    }
  } else {
    alert("Cannot update!!!\n form has following errors \n" + errors);
  }
};

// ********* PRINT OPERATIONS *********

//print function
const printViewRecord = () => {
  newTab = window.open();
  newTab.document.write(
    //  link bootstrap css
    "<head><title>Print #</title>" +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
      "<h2># Details</h2>" +
      printTable.outerHTML
  );

  //triger print() after 1000 milsec time out
  setTimeout(function () {
    newTab.print();
  }, 1000);
};

//print all data table after 1000 milsec of new tab opening () - to refresh the new tab elements
const printFullTable = () => {
  const newTab = window.open();
  newTab.document.write(
    //  link bootstrap css
    "<head><title>Print Employee</title>" +
      '<script src="resources/js/jquery.js"></script>' +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
      "<h2>Employee Details</h2>" +
      tableId.outerHTML +
      '<script>$(".modify-button").css("display","none")</script>'
  );

  setTimeout(function () {
    newTab.print();
  }, 1000);
};

// ********* OTHER OPERATIONS *********

// function for add selected product
const addOneProduct = () => {
  // get selected product from list
  if (selectAllProducts.value != "") {
    let selectedProduct = JSON.parse(selectAllProducts.value);

    //push selected product into new array - supplier.products
    supplier.products.push(selectedProduct);

    //set that array to selected selectedProducts dropdown
    fillMoreDataIntoSelect(
      selectedProducts,
      "",
      supplier.products,
      "barcode",
      "name"
    );

    // get index of selected product
    let extProductIndex = getProductIndexByName(
      availableProductList,
      selectedProduct.name
    );

    // remove selected product from all available product list
    if (extProductIndex != -1) {
      availableProductList.splice(extProductIndex, 1);
    }

    // refill available product list
    fillMoreDataIntoSelect(
      selectAllProducts,
      "",
      availableProductList,
      "barcode",
      "name"
    );
  } else {
    alert("Please select value before add...!");
  }
};

// function for remove selected product
const removeOneProduct = () => {
  // get selected product from list
  if (selectedProducts.value != "") {
    let selectedProduct = JSON.parse(selectedProducts.value);

    //push selected product back into available product list
    availableProductList.push(selectedProduct);

    // refill available product list
    fillMoreDataIntoSelect(
      selectAllProducts,
      "",
      availableProductList,
      "barcode",
      "name"
    );

    // get index of selected product
    let extProductIndex = getProductIndexByName(
      supplier.products,
      selectedProduct.name
    );

    // remove selected product from all available product list
    if (extProductIndex != -1) {
      supplier.products.splice(extProductIndex, 1);
    }
    //set that array to selected selectedProducts dropdown
    fillMoreDataIntoSelect(
      selectedProducts,
      "",
      supplier.products,
      "barcode",
      "name"
    );
  } else {
    alert("Please select value before remove...!");
  }
};

// function for add all products
const addAllProducts = () => {
  // get all product to selectd product list
  availableProductList.forEach((product) => {
    supplier.products.push(product);
  });

  //set that array to selected selectedProducts dropdown
  fillMoreDataIntoSelect(
    selectedProducts,
    "",
    supplier.products,
    "barcode",
    "name"
  );

  // remove all products from avalilable product list
  availableProductList = [];
  fillMoreDataIntoSelect(
    selectAllProducts,
    "",
    availableProductList,
    "barcode",
    "name"
  );
};

// fucntion for remove all products
const removeAllProducts = () => {
  // get all product to available product list
  supplier.products.forEach((product) => {
    availableProductList.push(product);
  });

  // set all products to available product list
  fillMoreDataIntoSelect(
    selectAllProducts,
    "",
    availableProductList,
    "barcode",
    "name"
  );
  // remove all products from selected product list
  supplier.products = [];
  //set that array to selected selectedProducts dropdown
  fillMoreDataIntoSelect(
    selectedProducts,
    "",
    supplier.products,
    "barcode",
    "name"
  );
};

// function for get index of product by name
const getProductIndexByName = (products, productName) => {
  return products.map((product) => product.name).indexOf(productName);
};
