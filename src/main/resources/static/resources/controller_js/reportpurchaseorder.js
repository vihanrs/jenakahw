//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest("/privilege/byloggeduserandmodule/Report");
  manageNavBar();
  //refresh all
  refreshAll();

  //call all event listners
  addEventListeners();
});

// ********* LISTENERS *********

const addEventListeners = () => {
  btnSearch.addEventListener("click", () => {
    search();
  });
  btnReset.addEventListener("click", () => {
    resetFilters();
  });

  printSummary.addEventListener("click", () => {
    printChart();
  });

  printAllData.addEventListener("click", () => {
    printFullTable();
  });
};

// ********* RESET *********
//function for refresh form and table
const refreshAll = () => {
  //array for store data list
  purchaseOrders = ajaxGetRequest("/purchaseorder/findall");

  poSummaryBySupplier = ajaxGetRequest(
    "/report/reportpurchaseorder/findposupplierwisesummary"
  );
  // call refresh filters function
  refreshFilters();
  //Call table refresh function
  refreshTable();
  //Call summary table refresh function
  refreshSummaryTable();
};

//function for refresh form area
const refreshFilters = () => {
  suppliers = ajaxGetRequest("/supplier/findactivesuppliers");
  fillMoreDataIntoSelect(
    selectSupplier,
    "Select Supplier",
    suppliers,
    "firstName",
    "company"
  );
  selectSupplier.value = "";
  // get purchase order status
  poStatuses = ajaxGetRequest("/purchaseorderstatus/findall");
  fillDataIntoSelect(selectPOStatus, "Select Status", poStatuses, "name");
};

// function for reset filters
const resetFilters = () => {
  selectSupplier.value = "";
  selectPOStatus.value = "";
  search();
};

// function for get table data from backend
const search = () => {
  //set values to variables
  let selectedSupplier =
    selectSupplier.value != "" ? JSON.parse(selectSupplier.value) : "";
  let selectedStatus =
    selectPOStatus.value != "" ? JSON.parse(selectPOStatus.value) : "";

  if (selectedSupplier != "" && selectedStatus != "") {
    //array for store data list
    purchaseOrders = ajaxGetRequest(
      "report/reportpurchaseorder/findbystatusandsupplier/" +
        selectedStatus.id +
        "/" +
        selectedSupplier.id
    );
  } else if (selectedSupplier != "") {
    //array for store data list
    purchaseOrders = ajaxGetRequest(
      "report/reportpurchaseorder/findbysupplier/" + selectedSupplier.id
    );
  } else if (selectedStatus != "") {
    //array for store data list
    purchaseOrders = ajaxGetRequest(
      "report/reportpurchaseorder/findbystatus/" + selectedStatus.id
    );
  } else {
    purchaseOrders = ajaxGetRequest("/purchaseorder/findall");
  }
  refreshTable();
};

// ********* TABLE OPERATIONS *********

//function for refresh table records
const refreshTable = () => {
  //object count = table column count
  //String - number/string/date
  //function - object/array/boolean
  //currency - RS
  const displayProperties = [
    { property: "poCode", datatype: "String" },
    { property: getAddedDate, datatype: "function" },
    { property: getSupplier, datatype: "function" },
    { property: "requiredDate", datatype: "String" },
    { property: getItemCount, datatype: "function" },
    { property: "totalAmount", datatype: "currency" },
    { property: getStatus, datatype: "function" },
  ];

  let table = new DataTable("#purchaseOrdersReportTable");
  table.destroy();

  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys, user privileges)
  fillDataIntoTable(
    purchaseOrdersReportTable,
    purchaseOrders,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    false,
    userPrivilages
  );

  getTotalamount(purchaseOrders);
  getPOCount(purchaseOrders);
  $("#purchaseOrdersReportTable").dataTable();
};

const getPOCount = (polist) => {
  textPOCount.value = polist.length;

  let requestedCount = 0;
  let recCount = 0;
  let delCount = 0;

  polist.forEach((po) => {
    if (po.purchaseOrderStatusId.name == "Requested") {
      requestedCount = requestedCount + 1;
      console.log(po.purchaseOrderStatusId.name);
    } else if (po.purchaseOrderStatusId.name == "Received") {
      recCount = recCount + 1;
    } else if (po.purchaseOrderStatusId.name == "Deleted") {
      delCount = delCount + 1;
    }
  });

  textRequestedPOCount.value = requestedCount;
  textRecivedPOCount.value = recCount;
  textDelPOCount.value = delCount;
};

const viewRecord = () => {};
const refillRecord = () => {};
const deleteRecord = () => {};

//function for get Supplier
const getSupplier = (rowObject) => {
  return (
    rowObject.supplierId.firstName +
    (rowObject.supplierId.company != null
      ? " - " + rowObject.supplierId.company
      : "")
  );
};

// function for get added date
const getAddedDate = (rowObject) => {
  return rowObject.addedDateTime.split("T")[0];
};
// function  for get po item count
const getItemCount = (rowObject) => {
  return rowObject.poHasProducts.length;
};

// function for get total amount
const getTotalamount = (purchaseOrders) => {
  let total = 0;
  purchaseOrders.forEach((po) => {
    total += po.totalAmount;
  });

  textTotalAmount.value = "Rs." + parseFloat(total).toFixed(2);
};

// function for get Status
const getStatus = (rowObject) => {
  if (rowObject.purchaseOrderStatusId.name == "Requested") {
    return (
      '<p class = "status btn-info">' +
      rowObject.purchaseOrderStatusId.name +
      "</p>"
    );
  } else if (rowObject.purchaseOrderStatusId.name == "Received") {
    return (
      '<p class = "status status-active">' +
      rowObject.purchaseOrderStatusId.name +
      "</p>"
    );
  } else if (rowObject.purchaseOrderStatusId.name == "Canceled") {
    return (
      '<p class = "status status-warning">' +
      rowObject.purchaseOrderStatusId.name +
      "</p>"
    );
  } else if (rowObject.purchaseOrderStatusId.name == "Deleted") {
    return (
      '<p class = "status status-error">' +
      rowObject.purchaseOrderStatusId.name +
      "</p>"
    );
  }
};

// function for refresh summary table
const refreshSummaryTable = () => {
  const displayProperties = [
    { property: getSummarySupplier, datatype: "function" },
    { property: "count", datatype: "String" },
    { property: "total", datatype: "currency" },
  ];

  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys, user privileges)
  fillDataIntoTable(
    poSummaryTable,
    poSummaryBySupplier,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    false,
    userPrivilages
  );

  refreshChart(poSummaryBySupplier);
};

// function for get supplier
const getSummarySupplier = (rowObject) => {
  return (
    rowObject.supplierFirstName +
    (rowObject.company != null ? " - " + rowObject.company : "")
  );
};

// function for refresh chart
const refreshChart = (po) => {
  labelArray = new Array();
  dataArray = new Array();

  po.forEach((po) => {
    labelArray.push(getSummarySupplier(po));
    dataArray.push(po.total);
  });

  const ctx = document.getElementById("myChart");

  myChartView = new Chart(ctx, {
    type: "bar",
    data: {
      labels: labelArray,
      datasets: [
        {
          label: "Supplier Wise Pending Purchase Order Total",
          data: dataArray,
          borderWidth: 1,
        },
      ],
    },
    options: {
      scales: {
        y: {
          beginAtZero: true,
        },
      },
    },
  });
};

// ********* PRINT OPERATIONS *********

// function for print chart
const printChart = () => {
  viewChart.src = myChartView.toBase64Image();

  let newWindow = window.open();

  newWindow.document.write(viewChart.outerHTML);

  // triger print() after 1000 milsec time out
  setTimeout(function () {
    newWindow.print();
  }, 1000);
};

//print all data table after 1000 milsec of new tab opening () - to refresh the new tab elements
const printFullTable = () => {
  const newTab = window.open();
  newTab.document.write(
    //  link bootstrap css
    "<head><title>Print Purchase Order</title>" +
      '<script src="resources/js/jquery.js"></script>' +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
      "<h2>Purchase Order Details</h2>" +
      purchaseOrdersReportTable.outerHTML
  );

  setTimeout(function () {
    newTab.print();
  }, 1000);
};
