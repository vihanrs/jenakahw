//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest("/privilege/byloggeduserandmodule/Privilege");

  console.log(userPrivilages);
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
  cmbRole.addEventListener("change", () => {
    selectDFieldValidator(cmbRole, "privilege", "role");
    generateModuleList();
  });

  cmbModule.addEventListener("change", () => {
    selectDFieldValidator(cmbModule, "privilege", "module");
  });

  checkBoxSelect.addEventListener("change", () => {
    checkBoxValidator(
      checkBoxSelect,
      "privilege",
      "sel",
      true,
      false,
      labelCBSelect,
      "Granted",
      "Not-Granted"
    );
  });

  checkBoxInsert.addEventListener("change", () => {
    checkBoxValidator(
      checkBoxInsert,
      "privilege",
      "inst",
      true,
      false,
      labelCBInsert,
      "Granted",
      "Not-Granted"
    );
  });

  checkBoxUpdate.addEventListener("change", () => {
    checkBoxValidator(
      checkBoxUpdate,
      "privilege",
      "upd",
      true,
      false,
      labelCBUpdate,
      "Granted",
      "Not-Granted"
    );
  });

  checkBoxDelete.addEventListener("change", () => {
    checkBoxValidator(
      checkBoxDelete,
      "privilege",
      "del",
      true,
      false,
      labelCBDelete,
      "Granted",
      "Not-Granted"
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

  //record print function call
  btnViewPrint.addEventListener("click", () => {
    printViewRecord();
  });

  //record print function call
  btnPrintFullTable.addEventListener("click", () => {
    printFullTable();
  });
};

//define function for filter module list by given role id
const generateModuleList = () => {
  modulesByRole = ajaxGetRequest(
    "/module/listbyrole?roleid=" + JSON.parse(cmbRole.value).id
  );
  fillDataIntoSelect(cmbModule, "Select Module", modulesByRole, "name");
  cmbModule.disabled = false;
  cmbModule.style.border = "1px solid #ced4da";
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
  privilege = {};
  privilege.sel = false;
  privilege.inst = false;
  privilege.upd = false;
  privilege.del = false;

  //get data list from select element
  roles = ajaxGetRequest("/role/findall");
  fillDataIntoSelect(cmbRole, "Select Role", roles, "name");

  modules = ajaxGetRequest("/module/findall");
  fillDataIntoSelect(cmbModule, "Select Module", modules, "name");

  //empty all elements
  checkBoxSelect.checked = false;
  labelCBSelect.innerText = "Not-Granted";
  checkBoxInsert.checked = false;
  labelCBInsert.innerText = "Not-Granted";
  checkBoxUpdate.checked = false;
  labelCBUpdate.innerText = "Not-Granted";
  checkBoxDelete.checked = false;
  labelCBDelete.innerText = "Not-Granted";

  cmbRole.style.border = "1px solid #ced4da";
  cmbModule.style.border = "1px solid #ced4da";

  cmbRole.disabled = false;
  cmbModule.disabled = true;

  //manage buttons
  btnUpdate.style.display = "none";
  if (!userPrivilages.insert) {
    btnAdd.style.display = "none";
  } else {
    btnAdd.style.display = "";
  }
};

//function for refresh table records
const refreshTable = () => {
  //array for store data list
  privileges = ajaxGetRequest("/privileges/findall");

  //object count = table column count
  //String - number/string/date
  //function - object/array/boolean
  const displayProperties = [
    { property: getRole, datatype: "function" },
    { property: getModule, datatype: "function" },
    { property: getSelect, datatype: "function" },
    { property: getInsert, datatype: "function" },
    { property: getUpdate, datatype: "function" },
    { property: getDelete, datatype: "function" },
  ];

  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys)
  fillDataIntoTable(
    tblPrivilege,
    privileges,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    true,
    userPrivilages
  );

  //hide delete button when all privileges not-granted
  privileges.forEach((privilege, index) => {
    if (
      userPrivilages.delete &&
      !privilege.sel &&
      !privilege.inst &&
      !privilege.upd &&
      !privilege.del
    ) {
      //catch the button
      let targetElement =
        tblPrivilege.children[1].children[index].children[7].children[
          userPrivilages.update && userPrivilages.insert ? 2 : 1
        ];
      //add changes
      targetElement.style.pointerEvents = "none";
      targetElement.style.visibility = "hidden";
    }
  });

  $("#tblPrivilege").dataTable();
};

// ********* TABLE OPERATIONS *********

//function for get role name
const getRole = (rowObject) => {
  return rowObject.role.name;
};

//function for get module name
const getModule = (rowObject) => {
  return rowObject.module.name;
};

//function for get select
const getSelect = (rowObject) => {
  return rowObject.sel
    ? "<p><i class='fa fa-check'style='color: #00e71f'></i> Granted</p>"
    : "<p><i class='fa fa-times'style='color: #ff2c28'></i> Not-Granted</p>";
};

//function for get insert
const getInsert = (rowObject) => {
  return rowObject.inst
    ? "<p><i class='fa fa-check'style='color: #00e71f'></i> Granted</p>"
    : "<p><i class='fa fa-times'style='color: #ff2c28'></i> Not-Granted</p>";
};

//function for get update
const getUpdate = (rowObject) => {
  return rowObject.upd
    ? "<p><i class='fa fa-check'style='color: #00e71f'></i> Granted</p>"
    : "<p><i class='fa fa-times'style='color: #ff2c28'></i> Not-Granted</p>";
};

//function for get delete
const getDelete = (rowObject) => {
  return rowObject.del
    ? "<p><i class='fa fa-check'style='color: #00e71f'></i> Granted</p>"
    : "<p><i class='fa fa-times'style='color: #ff2c28'></i> Not-Granted</p>";
};

//function for view record
const viewRecord = (rowObject, rowId) => {
  //need to get full object
  let printObj = rowObject;

  tdRole.innerText = printObj.role.name;
  tdModule.innerText = printObj.module.name;
  tdSelect.innerText = printObj.sel ? "Granted" : "Not-Granted";
  tdInsert.innerText = printObj.inst ? "Granted" : "Not-Granted";
  tdUpdate.innerText = printObj.upd ? "Granted" : "Not-Granted";
  tdDelete.innerText = printObj.del ? "Granted" : "Not-Granted";

  //open model
  $("#modelDetailedView").modal("show");
};

//function for refill record
const refillRecord = (rowObject, rowId) => {
  $("#addNewButton").click();

  // default = rowObject;
  privilege = JSON.parse(JSON.stringify(rowObject));
  oldPrivilege = JSON.parse(JSON.stringify(rowObject));

  //disable pw field
  cmbRole.disabled = true;
  cmbModule.disabled = true;

  checkBoxSelect.checked = false;
  checkBoxInsert.checked = false;
  checkBoxUpdate.checked = false;
  checkBoxDelete.checked = false;

  if (privilege.sel) {
    checkBoxSelect.checked = true;
  }
  if (privilege.inst) {
    checkBoxInsert.checked = true;
  }
  if (privilege.upd) {
    checkBoxUpdate.checked = true;
  }
  if (privilege.del) {
    checkBoxDelete.checked = true;
  }

  //if we have optional join column then need to check null
  // cmbDesignation
  fillDataIntoSelect(
    cmbRole,
    "Select Status",
    roles,
    "name",
    privilege.role.name
  );
  // cmbDesignation
  fillDataIntoSelect(
    cmbModule,
    "Select Status",
    modules,
    "name",
    privilege.module.name
  );

  //manage buttons
  btnAdd.style.display = "none";
  if (!userPrivilages.update) {
    btnUpdate.style.display = "none";
  } else {
    btnUpdate.style.display = "";
  }
};

//function for delete record
const deleteRecord = (rowObject, rowId) => {
  //get user confirmation
  let title = "Are you sure!\nYou wants to delete following record?\n";
  let message =
    "Role : " +
    rowObject.role.name +
    "\n" +
    "Module : " +
    rowObject.module.name;

  showConfirm(title, message).then((userConfirm) => {
    if (userConfirm) {
      //response from backend ...
      let serverResponse = ajaxRequestBody("/privileges", "DELETE", rowObject); // url,method,object
      //check back end response
      if (serverResponse == "OK") {
        showAlert("success", "Privilege Delete successfully..!").then(() => {
          //need to refresh table and form
          refreshAll();
        });
      } else {
        showAlert(
          "error",
          "Privilege delete not successfully..! have some errors \n" +
            serverResponse
        );
      }
    }
  });
};

// ********* FORM OPERATIONS *********

//function for check errors
const checkErrors = () => {
  //need to check all required prperty filds
  let error = "";

  if (privilege.role == null) {
    error = error + "Please Select Role...!\n";
    cmbRole.style.border = "1px solid red";
  }
  if (privilege.module == null) {
    error = error + "Please Select Module...!\n";
    cmbModule.style.border = "1px solid red";
  }

  if (
    privilege.sel == null &&
    privilege.inst == null &&
    privilege.upd == null &&
    privilege.del == null
  ) {
    error = error + "Please Select Granted Privileges...!\n";
  }

  return error;
};

//function for check updates
const checkUpdates = () => {
  let updates = "";

  if (oldPrivilege.sel != privilege.sel) {
    updates =
      updates +
      "Select Privilege has changed : \n" +
      (oldPrivilege.sel ? "Granted" : "Not-Granted") +
      " into " +
      (privilege.sel ? "Granted" : "Not-Granted") +
      " \n";
  }

  if (oldPrivilege.inst != privilege.inst) {
    updates =
      updates +
      "Insert Privilege has changed : \n" +
      (oldPrivilege.inst ? "Granted" : "Not-Granted") +
      " into " +
      (privilege.inst ? "Granted" : "Not-Granted") +
      " \n";
  }

  if (oldPrivilege.upd != privilege.upd) {
    updates =
      updates +
      "Update Privilege has changed : \n" +
      (oldPrivilege.upd ? "Granted" : "Not-Granted") +
      " into " +
      (privilege.upd ? "Granted" : "Not-Granted") +
      " \n";
  }

  if (oldPrivilege.del != privilege.del) {
    updates =
      updates +
      "Delete Privilege has changed : \n" +
      (oldPrivilege.del ? "Granted" : "Not-Granted") +
      " into " +
      (privilege.del ? "Granted" : "Not-Granted") +
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
      "\nRole : " +
      privilege.role.name +
      "\nModule : " +
      privilege.module.name +
      "\n\nSelect : " +
      (privilege.sel ? "Granted" : "Not-Granted") +
      " | Instert : " +
      (privilege.inst ? "Granted" : "Not-Granted") +
      "\nUpdate : " +
      (privilege.upd ? "Granted" : "Not-Granted") +
      " | Delete : " +
      (privilege.del ? "Granted" : "Not-Granted");
    showConfirm(title, message).then((userConfirm) => {
      if (userConfirm) {
        //pass data into back end
        let serverResponse = ajaxRequestBody("/privileges", "POST", privilege); // url,method,object

        //check back end response
        if (serverResponse == "OK") {
          showAlert("success", "Privilege Save successfully..!").then(() => {
            //need to refresh table and form
            refreshAll();
          });
        } else {
          showAlert(
            "error",
            "Privilege save not successfully..! have some errors \n" +
              serverResponse
          );
        }
      }
    });
  } else {
    showAlert("error", "Error\n" + formErrors);
  }
};

//function for update record
const updateRecord = () => {
  let errors = checkErrors();
  if (errors == "") {
    let updates = checkUpdates();
    if (updates != "") {
      let title = "Are you sure you want to update following changes...?\n";
      let message = updates;
      showConfirm(title, message).then((userConfirm) => {
        if (userConfirm) {
          let updateServiceResponse = ajaxRequestBody(
            "/privileges",
            "PUT",
            privilege
          );
          if (updateServiceResponse == "OK") {
            showAlert("success", "Privilege Update successfully..!").then(
              () => {
                //need to refresh table and form
                refreshAll();
              }
            );
          } else {
            showAlert(
              "error",
              "Privilege update not successfully..! have some errors \n" +
                updateServiceResponse
            );
          }
        }
      });
    } else {
      showAlert("warning", "Nothing to Update...!");
    }
  } else {
    showAlert(
      "error",
      "Cannot update!!!\nForm has following errors \n" + errors
    );
  }
};

// ********* PRINT OPERATIONS *********

//print function
const printViewRecord = () => {
  newTab = window.open();
  newTab.document.write(
    //  link bootstrap css
    "<head><title>Privilege Details</title>" +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
      "<h2 style = 'font-weight:bold'>Privilege Details</h2>" +
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
    "<head><title>Print Privileges</title>" +
      '<script src="resources/js/jquery.js"></script>' +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" />' +
      '<link rel="stylesheet" href="resources/fontawesome-6.4.2/css/all.css" /></head>' +
      "<h2 style = 'font-weight:bold'>Privilege Details</h2>" +
      tblPrivilege.outerHTML +
      '<script>$("#modifyButtons").css("display","none");$(".table-buttons").hide();</script>'
  );

  setTimeout(function () {
    newTab.print();
  }, 1000);
};
