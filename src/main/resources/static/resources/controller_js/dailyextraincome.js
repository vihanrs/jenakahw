//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest(
    "/privilege/byloggeduserandmodule/Daily Extra Income"
  );
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
  let numberWithdecimals = "^(([1-9]{1}[0-9]{0,7})|([0-9]{0,8}[.][0-9]{2}))$";

  textAmount.addEventListener("keyup", () => {
    textFieldValidator(
      textAmount,
      numberWithdecimals,
      "dailyexincome",
      "total"
    );
  });

  textReason.addEventListener("keyup", () => {
    textFieldValidator(textReason, "", "dailyexincome", "reason");
  });

  selectStatus.addEventListener("change", () => {
    selectDFieldValidator(
      selectStatus,
      "dailyexincome",
      "dailyIncomeExpensesStatusId"
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
  dailyexincome = {};

  // get status
  statuses = ajaxGetRequest("/dailyincomeexpensesstatus/findall");
  fillDataIntoSelect(selectStatus, "Select Status", statuses, "name", "Saved");
  statusDiv.classList.add("d-none");

  //bind default selected status in to supplier object and set valid color
  dailyexincome.dailyIncomeExpensesStatusId = JSON.parse(selectStatus.value);
  selectStatus.style.border = "2px solid #00FF7F";

  //empty all elements
  textAmount.value = "";
  textReason.value = "";

  //set default border color
  let elements = [textAmount, textReason];
  setBorderStyle(elements);

  //manage form buttons
  manageFormButtons("insert", userPrivilages);
};

//function for check errors
const checkErrors = () => {
  //need to check all required property fields
  let error = "";

  if (dailyexincome.total == null) {
    error = error + "Please Enter Amount...!\n";
    textAmount.style.border = "1px solid red";
  }
  if (dailyexincome.reason == null) {
    error = error + "Please Enter Reason...!\n";
    textReason.style.border = "1px solid red";
  }

  if (dailyexincome.dailyIncomeExpensesStatusId == null) {
    error = error + "Please Select Valid Status...!\n";
    selectStatus.style.border = "1px solid red";
  }

  return error;
};

//function for check updates
const checkUpdates = () => {
  let updates = "";

  if (olddailyexincome.total != dailyexincome.total) {
    updates +=
      "Amount has changed " +
      olddailyexincome.total +
      " into " +
      dailyexincome.total +
      " \n";
  }
  if (olddailyexincome.reason != dailyexincome.reason) {
    updates +=
      "Reason No. has changed " +
      olddailyexincome.reason +
      " into " +
      dailyexincome.reason +
      " \n";
  }

  if (
    olddailyexincome.dailyIncomeExpensesStatusId.id !=
    dailyexincome.dailyIncomeExpensesStatusId.id
  ) {
    updates +=
      "Status has changed " +
      olddailyexincome.dailyIncomeExpensesStatusId.name +
      " into " +
      dailyexincome.dailyIncomeExpensesStatusId.name +
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
      "Amount (Rs.): " +
      dailyexincome.total +
      "\nReason : " +
      dailyexincome.reason;
    showConfirm(title, message).then((userConfirm) => {
      if (userConfirm) {
        //pass data into back end
        let serverResponse = ajaxRequestBody(
          "/dailyextraincome",
          "POST",
          dailyexincome
        ); // url,method,object

        //check back end response
        if (serverResponse == "OK") {
          showAlert("success", "Daily Extra Income Save successfully..!").then(
            () => {
              //need to refresh table and form
              refreshAll();
            }
          );
        } else {
          showAlert(
            "error",
            "Daily Extra Income save not successfully..! have some errors \n" +
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
          let serverResponse = ajaxRequestBody(
            "/dailyextraincome",
            "PUT",
            dailyexincome
          );
          if (serverResponse == "OK") {
            showAlert(
              "success",
              "Daily Extra Income Update successfully..!"
            ).then(() => {
              //need to refresh table and form
              refreshAll();
            });
          } else {
            showAlert(
              "error",
              "Daily Extra Income update not successfully..! have some errors \n" +
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
  dailyextraincomes = ajaxGetRequest("/dailyextraincome/findall");

  //object count = table column count
  //String - number/string/date
  //function - object/array/boolean
  //currency - RS
  const displayProperties = [
    { property: "total", datatype: "currency" },
    { property: "reason", datatype: "String" },
    { property: getAddedDate, datatype: "function" },
    { property: getStatus, datatype: "function" },
  ];

  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys, user privileges)
  fillDataIntoTable(
    dailyExtraIncomeTable,
    dailyextraincomes,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    false,
    userPrivilages
  );

  //hide delete button when status is 'deleted'
  // dailyextraincomes.forEach((dailyExtraIncome, index) => {
  //   if (userPrivilages.update) {
  //     let targetElement =
  //       dailyExtraIncomeTable.children[1].children[index].children[5]
  //         .children[1];
  //     //add changes
  //     targetElement.style.pointerEvents = "none";
  //     targetElement.style.visibility = "hidden";
  //     targetElement.style.display = "none";
  //   }

  //   if (userPrivilages.delete) {
  //     let targetElement =
  //       dailyExtraIncomeTable.children[1].children[index].children[5]
  //         .children[2];
  //     //add changes
  //     targetElement.style.pointerEvents = "none";
  //     targetElement.style.visibility = "hidden";
  //     targetElement.style.display = "none";
  //   }
  // });

  $("#dailyExtraIncomeTable").dataTable();
};

// function for get status
const getStatus = (rowObject) => {
  if (rowObject.dailyIncomeExpensesStatusId.name == "Saved") {
    return (
      '<p class = "status status-active">' +
      rowObject.dailyIncomeExpensesStatusId.name +
      "</p>"
    );
  } else if (rowObject.dailyIncomeExpensesStatusId.name == "Deleted") {
    return (
      '<p class = "status status-error">' +
      rowObject.dailyIncomeExpensesStatusId.name +
      "</p>"
    );
  }
};

const getAddedDate = (rowObject) => {
  return rowObject.addedDateTime.split("T")[0];
};

//function for view record
const viewRecord = (rowObject, rowId) => {
  //need to get full object
  let printObj = rowObject;

  tdAmount.innerText = "Rs." + parseFloat(printObj.total).toFixed(2);
  tdReason.innerText = printObj.reason;
  tdAddedDate.innerText = printObj.addedDateTime.split("T")[0];
  tdStatus.innerText = printObj.dailyIncomeExpensesStatusId.name;

  //open model
  $("#modelDetailedView").modal("show");
};

//function for refill record
const refillRecord = (rowObject, rowId) => {
  $("#addNewButton").click();

  dailyexincome = JSON.parse(JSON.stringify(rowObject)); //convert rowobject to json string and covert back it to js object
  olddailyexincome = JSON.parse(JSON.stringify(rowObject)); // deep copy - create compeletely indipended two objects

  textReason.value = dailyexincome.reason;
  textAmount.value = dailyexincome.total;

  // set status
  fillDataIntoSelect(
    selectStatus,
    "Select Status",
    statuses,
    "name",
    dailyexincome.dailyIncomeExpensesStatusId.name
  );
  // statusDiv.classList.remove("d-none");
  setBorderStyle([selectStatus, textAmount, textReason]);

  //manage buttons
  manageFormButtons("refill", userPrivilages);
};

// //function for delete record
const deleteRecord = (rowObject, rowId) => {
  //get user confirmation
  let title = "Are you sure!\nYou wants to delete following record? \n";
  let message =
    "Amount (Rs.): " + rowObject.total + "\nReason :" + rowObject.reason;

  showConfirm(title, message).then((userConfirm) => {
    if (userConfirm) {
      //response from backend ...
      let serverResponse = ajaxRequestBody(
        "/dailyextraincome",
        "DELETE",
        rowObject
      ); // url,method,object
      //check back end response
      if (serverResponse == "OK") {
        showAlert("success", "Daily Extra Income Delete successfully..!").then(
          () => {
            // Need to refresh table and form
            refreshAll();
          }
        );
      } else {
        showAlert(
          "error",
          "Daily Extra Income delete not successfully..! have some errors \n" +
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
    "<head><title>Print Daily Extra Income</title>" +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
      "<h2 style = 'font-weight:bold'>Daily Extra Income Details</h2>" +
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
    "<head><title>Print Daily Extra Incomes</title>" +
      '<script src="resources/js/jquery.js"></script>' +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
      "<h2 style = 'font-weight:bold'>Daily Extra Incomes Details</h2>" +
      dailyExtraIncomeTable.outerHTML +
      '<script>$("#modifyButtons").css("display","none");$(".table-buttons").hide();</script>'
  );

  setTimeout(function () {
    newTab.print();
  }, 1000);
};
