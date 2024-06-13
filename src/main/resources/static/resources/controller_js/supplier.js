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
    textFieldValidator(textCompany, companyNamePattern, "supplier", "email");
  });

  textAddress.addEventListener("keyup", () => {
    textFieldValidator(textAddress, "^.*$", "supplier", "address");
  });

  selectStatus.addEventListener("change", () => {
    selectDFieldValidator(selectStatus, "supplier", "supplierStatusId");
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
  supplier.supplierStatus = JSON.parse(selectStatus.value);
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

  //set default colors
  textFirstName.style.border = "1px solid #ced4da";
  textLastName.style.border = "1px solid #ced4da";
  textContact.style.border = "1px solid #ced4da";
  textEmail.style.border = "1px solid #ced4da";
  textCompany.style.border = "1px solid #ced4da";
};

//function for refresh table records
const refreshTable = () => {
  /* EXAMPLES
    //array for store data list
    defaults = ajaxGetRequest("/default/findall");
  
    //object count = table column count
    //String - number/string/date
    //function - object/array/boolean
    const displayProperties = [
      { property: "fullName", datatype: "String" },
      { property: "email", datatype: "String" },
      { property: getStatus, datatype: "function" },
    ];
  
     //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys)
    fillDataIntoTable(
      tableId,
      defaults,
      displayProperties,
      viewRecord,
      refillRecord,
      deleteRecord,
      true
    );
  
    $("#tableId").dataTable();
    */
};

// ********* TABLE OPERATIONS *********

//function for set stats column
const getStatus = (rowOb) => {
  /* EXAMPLES
    if (rowOb.statusId.name == "Working") {
      return '<p class = "working-status">' + rowOb.statusId.name + "</p>";
    } else if (rowOb.employeeStatusId.name == "Resign") {
      return '<p class = "resign-status">' + rowOb.statusId.name + "</p>";
    } else {
      return '<p class = "delete-status">' + rowOb.statusId.name + "</p>";
    }*/
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
  /* EXAMPLES
    $("#modalAddFormId").modal("show");
  
    // default = rowObject;
    defaultObj = JSON.parse(JSON.stringify(rowObject));
    olddefaultObj = JSON.parse(JSON.stringify(rowObject));
  
    //set normal fields
    txtFullName.value = employee.fullName;
    cmbCivilstatus.value = employee.civilStatus;
  
    //set conditional value fields
    if (defaultObj.gender == "male") {
      radioGenderMale.checked = true;
    } else {
      radioGenderFemale.checked = true;
    }
  
    //set optional fields
    if (defaultObj.landNo != null) txtLandNo.value = defaultObj.landNo;
    else txtLandNo.value = "";
    if (defaultObj.note != null) txtNote.value = defaultObj.note;
    else txtNote.value = "";
  
    //if we have optional join column then need to check null
    // cmbDesignation
    fillDataIntoSelect(
      cmbDesignation,
      "Select Designation",
      designations,
      "name",
      employee.designationId.name
    );
  
    // cmbEmployeeStatus
    fillDataIntoSelect(
      cmbEmployeeStatus,
      "Select status",
      employeestatuses,
      "name",
      employee.employeeStatusId.name
    );
    */
};

//function for delete record
const deleteRecord = (rowObject, rowId) => {
  /* EXAMPLES
    //get user confirmation
    const userConfirm = confirm(
      "Are you sure to delete following record \n" + rowObject.fullName
    );
  
    if (userConfirm) {
      //response from backend ...
      let serverResponse = ajaxRequestBody("/default", "DELETE", rowObject); // url,method,object
      //check back end response
      if (serverResponse == "OK") {
        alert("Delete sucessfully..! \n" + serverResponse);
        //need to refresh table and form
        refreshAll();
      } else {
        alert("Delete not sucessfully..! have some errors \n" + serverResponse);
      }
    }
    */
};

// ********* FORM OPERATIONS *********

//function for check errors
const checkError = () => {
  //need to check all required prperty filds
  let error = "";

  /* EXAMPLES
    if (employee.fullName == null) {
      error = error + "Please Enter Valid Full Name...!\n";
      txtFullName.style.border = "2px solid red";
    }
    */

  return error;
};

//function for check updates
const checkUpdate = () => {
  let updates = "";

  /* EXAMPLES
    if (oldemployee.mobile != employee.mobile) {
      updates =
        updates +
        "NIC has changed " +
        oldemployee.mobile +
        " into " +
        employee.mobile +
        " \n";
    }
    */

  return updates;
};

//function for add record
const addRecord = () => {
  /* EXAMPLES
    //check form errors -
    let formErrors = checkError();
    if (formErrors == "") {
      //get user confirmation
      let userConfirm = window.confirm(
        "Are you sure to add following record..?\n" +
          "\nFull Name : " +
          employee.fullName +
          "\nDesgnation : " +
          employee.designationId.name
      );
  
      if (userConfirm) {
        //pass data into back end
        let serverResponse = ajaxRequestBody("/default", "POST", defaultObj); // url,method,object
  
        //check back end response
        if (new RegExp("^[0-9]{8}$").test(serverResponse)) {
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
    */
};

//function for update record
const updateRecord = () => {
  /* EXAMPLES
    let errors = checkError();
    if (errors == "") {
      let updates = checkUpdate();
      if (updates != "") {
        let userConfirm = confirm(
          "Are you sure you want to update following changes...?\n" + updates
        );
        if (userConfirm) {
          let updateServiceResponse = ajaxRequestBody(
            "/default",
            "PUT",
            defaultObj
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
      alert("Cannot update!!!\n form has following errors \n" + errors);
    }
    */
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

//function for add selected product
const addOneProduct = () => {
  // get selected product from list
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
  let extProductIndex = availableProductList
    .map((product) => product.name)
    .indexOf(selectedProduct.name);

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
};

const removeOneProduct = () => {
  // get selected product from list
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
  let extProductIndex = supplier.products
    .map((product) => product.name)
    .indexOf(selectedProduct.name);

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
};

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
