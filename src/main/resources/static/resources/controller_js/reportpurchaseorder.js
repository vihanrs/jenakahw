//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest("/privilege/byloggeduserandmodule/Report");

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

const getSummarySupplier = (rowObject) => {
  return (
    rowObject.supplierFirstName +
    (rowObject.company != null ? " - " + rowObject.company : "")
  );
};

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
const printChart = () => {
  viewChart.src = myChartView.toBase64Image();

  let newWindow = window.open();

  newWindow.document.write(
    viewChart.outerHTML +
      "<script>viewChart.style.removeProperty('display');<//script>"
  );
};
