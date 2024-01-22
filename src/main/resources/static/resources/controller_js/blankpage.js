/** 
  change table id


 
*/

//Access Browser onload event
window.addEventListener("load", () => {
  //set default selected section
  showDefaultSection("addNewButton", "addNewSection");

  //refresh all
  refreshAll();

  //call all event listners
  addEventListeners();
});

// ********* LISTENERS *********
const addEventListeners = () => {
  // let namePattern = "^[A-Z][a-z]{2,20}$";
  // let contactPattern = "^[0][7][01245678][0-9]{7}$";
  // textFirstName.addEventListener("keyup", () => {
  //   textFieldValidator(textFirstName, namePattern, "user", "firstName");
  // });
  // radioGenderMale.addEventListener("change", () => {
  //   radioFieldValidator(radioGenderMale, "user", "gender");
  // });
  // //form reset button function call
  // btnReset.addEventListener("click", () => {
  //   refreshForm();
  // });
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
  /* EXAMPLES
  //create empty object
  test = {};

  //get data list from select element
  designations = ajaxGetRequest("/designation/findall");
  fillDataIntoSelect(
    cmbDesignation,
    "Select Designation",
    designations,
    "name"
  );

  employeestatuses = ajaxGetRequest("/employeestatus/findall");
  fillDataIntoSelect(
    cmbEmployeeStatus,
    "Select status",
    employeestatuses,
    "name"
  );

  //empty all elements
  txtFullName.value = "";
  radioGenderFemale.checked = false;
  txtMobileNo.value = "";
  checkBoxActive.checked = true;
  labelCBActive.innerText = "Active";

  //set default colors
  txtNIC.style.border = "1px solid #ced4da";
  txtMobileNo.style.border = "1px solid #ced4da";
  */
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
