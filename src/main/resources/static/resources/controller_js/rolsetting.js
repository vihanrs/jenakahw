//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest("/privilege/byloggeduserandmodule/Product");

  manageNavBar();
  //refresh all
  refreshAll();

  //set default selected section
  // if (userPrivilages.insert) {
  //   showDefaultSection("addNewButton", "addNewSection");
  // } else {
  showDefaultSection("viewAllButton", "viewAllSection");
  // addAccordion.style.display = "none";
  // }
  //call all event listners
  // addEventListeners();
});

// ********* RESET *********
//function for refresh form and table
const refreshAll = () => {
  //Call form refresh function
  // refreshForm();
  //Call table refresh function
  refreshTable();
};

//function for update ROL
const updateRol = () => {
  //get user confirmation
  let title = "Are you sure to update re-order level..?\n";

  let message = "";
  showConfirm(title, message).then((userConfirm) => {
    if (userConfirm) {
      //pass data into back end
      let serverResponse = ajaxRequestBody(
        "/rolsetting",
        "PUT",
        productwithnewrol
      ); // url,method,object

      //check back end response
      if (serverResponse == "OK") {
        showAlert("success", "ROL update successfully..!").then(() => {
          //need to refresh table and form
          refreshAll();
          $("#modelDetailedView").modal("hide");
        });
      } else {
        showAlert(
          "error",
          "ROL update not successfully..! have some errors \n" + serverResponse
        );
      }
    }
  });
};

// ********* TABLE OPERATIONS *********

//function for refresh table records
const refreshTable = () => {
  productwithnewrol = "";
  //array for store data list
  productswithrol = ajaxGetRequest("dashboard/allsellingproductswithnrol");

  //object count = table column count
  //String - number/string/date
  //function - object/array/boolean
  //currency - RS
  const displayProperties = [
    { property: "name", datatype: "String" },
    { property: "brand", datatype: "String" },
    { property: "category", datatype: "String" },
    { property: "subCategory", datatype: "String" },
    { property: "rol", datatype: "String" },
    { property: getNewROL, datatype: "function" },
  ];

  let table = new DataTable("#invoiceTable");
  table.destroy();
  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys, user privileges)
  fillDataIntoTable(
    productwithrolTable,
    productswithrol,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    true,
    userPrivilages
  );

  let modifyElement = productwithrolTable.children[0].children[0].children[7];
  modifyElement.id = "modifyButtons";
  modifyButtons.style.width = "75px";
  //hide delete button when status is 'deleted'
  productswithrol.forEach((invoice, index) => {
    let targetElement =
      productwithrolTable.children[1].children[index].children[7].children[1];
    targetElement.style.pointerEvents = "none";
    targetElement.style.visibility = "hidden";
    targetElement.style.display = "none";

    //catch the button
    let targetElement1 =
      productwithrolTable.children[1].children[index].children[7]
        .lastElementChild;
    //add changes
    targetElement1.style.pointerEvents = "none";
    targetElement1.style.visibility = "hidden";
  });

  $("#productwithrolTable").dataTable();
};

// function for get status
const getNewROL = (rowObject) => {
  newRol = Math.ceil(parseFloat(rowObject.sellQty) / 3);

  // bind new value to the object
  rowObject.newRol = newRol;
  return newRol;
};
// function for get status
const refillRecord = () => {};
// function for get status
const deleteRecord = () => {};

//function for view record
const viewRecord = (rowObject, rowId) => {
  //need to get full object
  let printObj = rowObject;

  tdProductName.innerText = printObj.name;
  tdBrand.innerText = printObj.brand;
  tdCurrentROL.innerText = printObj.rol;
  tdNewROL.innerText = printObj.newRol;

  // get values to parse to the backend
  productwithnewrol = printObj.productId + "," + printObj.newRol;
  //open model
  $("#modelDetailedView").modal("show");
};
