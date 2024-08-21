//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest("/privilege/byloggeduserandmodule/Stock");

  //refresh all
  refreshAll();

  //set default selected section

  showDefaultSection("viewAllButton", "viewAllSection");
  //call all event listners
  addEventListeners();
});

// ********* LISTENERS *********
const addEventListeners = () => {
  //print full table function call
  btnPrintFullTable.addEventListener("click", () => {
    printFullTable();
  });
};

// ********* RESET *********
//function for refresh form and table
const refreshAll = () => {
  //Call form refresh function
  // refreshForm();
  //Call table refresh function
  refreshTable();
};

// ********* FORM OPERATIONS *********

//function for refresh form area
const refreshForm = () => {};

// ********* TABLE OPERATIONS *********

//function for refresh table records
const refreshTable = () => {
  //array for store data list
  stocks = ajaxGetRequest("/stock/findall");

  //object count = table column count
  //String - number/string/date
  //function - object/array/boolean
  //currency - RS
  const displayProperties = [
    { property: getProduct, datatype: "function" },
    { property: getBrand, datatype: "function" },
    { property: "totalQty", datatype: "String" },
    { property: "availableQty", datatype: "String" },
    { property: "sellPrice", datatype: "currency" },
    { property: getStatus, datatype: "function" },
  ];

  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys, user privileges)
  fillDataIntoTable(
    stockTable,
    stocks,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    false,
    userPrivilages
  );

  $("#stockTable").dataTable();
};

// function for get brand
const getBrand = (rowObject) => {
  return rowObject.productId.brandId.name;
};

// function for get category
const getProduct = (rowObject) => {
  return rowObject.productId.name;
};

// function for get status
const getStatus = (rowObject) => {
  if (rowObject.stockStatus.name == "In Stock") {
    return (
      '<p class = "status status-active">' + rowObject.stockStatus.name + "</p>"
    );
  } else if (rowObject.stockStatus.name == "Low Stock") {
    return (
      '<p class = "status status-warning">' +
      rowObject.stockStatus.name +
      "</p>"
    );
  } else if (rowObject.stockStatus.name == "Out of Stock") {
    return (
      '<p class = "status status-error">' + rowObject.stockStatus.name + "</p>"
    );
  }
};

//function for view record
const viewRecord = (rowObject, rowId) => {};

//function for refill record
const refillRecord = (rowObject, rowId) => {};

// //function for delete record
const deleteRecord = (rowObject, rowId) => {};

// ********* PRINT OPERATIONS *********

//print all data table after 1000 milsec of new tab opening () - to refresh the new tab elements
const printFullTable = () => {
  const newTab = window.open();
  newTab.document.write(
    //  link bootstrap css
    "<head><title>Print Stock</title>" +
      '<script src="resources/js/jquery.js"></script>' +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" />' +
      '<link rel="stylesheet" href="resources/css/common.css" /></head>' +
      "<h2 style = 'font-weight:bold'>Stock Details</h2>" +
      stockTable.outerHTML
  );

  setTimeout(function () {
    newTab.print();
  }, 1000);
};
