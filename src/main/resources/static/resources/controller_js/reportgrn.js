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
  selectSupplier.addEventListener("change", () => {});

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
  grns = ajaxGetRequest("/grn/findall");
  // grnSummaryByMonthly = ajaxGetRequest(
  //   "/report/reportgrn/findgrnsummarybymonthly"
  // );

  grnSummaryByMonthly = [
    {
      grnGrandTotal: "7250.00",
      grnCount: null,
      addedMonth: "1",
    },
    {
      grnGrandTotal: "8000.00",
      grnCount: null,
      addedMonth: "2",
    },
    {
      grnGrandTotal: "9000.00",
      grnCount: null,
      addedMonth: "3",
    },
    {
      grnGrandTotal: "8500.00",
      grnCount: null,
      addedMonth: "4",
    },
    {
      grnGrandTotal: "9200.00",
      grnCount: null,
      addedMonth: "5",
    },
    {
      grnGrandTotal: "9250.00",
      grnCount: null,
      addedMonth: "6",
    },
    {
      grnGrandTotal: "7250.00",
      grnCount: null,
      addedMonth: "7",
    },
  ];

  // call refresh filters function
  refreshFilters();
  //Call table refresh function
  refreshTable();

  // call Summary table refresh fucntion
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
};

const resetFilters = () => {
  fillMoreDataIntoSelect(
    selectSupplier,
    "Select Supplier",
    suppliers,
    "firstName",
    "company"
  );

  search();
};

const search = () => {
  let selectedSupplier =
    selectSupplier.value != "" ? JSON.parse(selectSupplier.value) : "";

  if (selectedSupplier != "") {
    //array for store data list
    grns = ajaxGetRequest(
      "report/reportgrn/findbysupplier/" + selectedSupplier.id
    );
  } else {
    grns = ajaxGetRequest("/grn/findall");
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
    { property: "grnCode", datatype: "String" },
    { property: getAddedDate, datatype: "function" },
    { property: getSupplier, datatype: "function" },
    { property: getPOID, datatype: "function" },
    { property: "itemCount", datatype: "String" },
    { property: "grandTotal", datatype: "currency" },
    { property: getStatus, datatype: "function" },
  ];

  let table = new DataTable("#grnReportTable");
  table.destroy();
  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys, user privileges)
  fillDataIntoTable(
    grnReportTable,
    grns,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    false,
    userPrivilages
  );
  getTotalamount(grns);
  $("#grnReportTable").dataTable();
};

const viewRecord = () => {};
const refillRecord = () => {};
const deleteRecord = () => {};

const refreshSummaryTable = () => {
  const displayProperties = [
    { property: getMonth, datatype: "function" },
    { property: "grnGrandTotal", datatype: "currency" },
  ];

  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys, user privileges)
  fillDataIntoTable(
    grnSummaryTable,
    grnSummaryByMonthly,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    false,
    userPrivilages
  );

  refreshChart(grnSummaryByMonthly);
};

const refreshChart = (grns) => {
  labelArray = new Array();
  dataArray = new Array();

  grns.forEach((grn) => {
    labelArray.push(getMonthName(grn.addedMonth));
    dataArray.push(grn.grnGrandTotal);
  });

  const ctx = document.getElementById("myChart");

  myChartView = new Chart(ctx, {
    type: "bar",
    data: {
      labels: labelArray,
      datasets: [
        {
          label: "Monthly Grand Total",
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

const getMonth = (rowObject) => {
  return getMonthName(rowObject.addedMonth);
};

// function for get POID
const getPOID = (rowObject) => {
  return rowObject.purchaseOrderId.poCode;
};

//function for get Supplier
const getSupplier = (rowObject) => {
  return (
    rowObject.purchaseOrderId.supplierId.firstName +
    (rowObject.purchaseOrderId.supplierId.company != null
      ? " - " + rowObject.purchaseOrderId.supplierId.company
      : "")
  );
};

// function for get added date
const getAddedDate = (rowObject) => {
  return rowObject.addedDateTime.split("T")[0];
};

// function for get total amount
const getTotalamount = (grns) => {
  let total = 0;
  grns.forEach((grn) => {
    total += grn.grandTotal;
  });

  textTotalAmount.value = "Rs." + parseFloat(total).toFixed(2);
};

// function for get Status
const getStatus = (rowObject) => {
  if (rowObject.grnStatusId.name == "Received") {
    return (
      '<p class = "status status-warning">' +
      rowObject.grnStatusId.name +
      "</p>"
    );
  } else if (rowObject.grnStatusId.name == "Deleted") {
    return (
      '<p class = "status status-error">' + rowObject.grnStatusId.name + "</p>"
    );
  } else if (rowObject.grnStatusId.name == "Completed") {
    return (
      '<p class = "status status-active">' + rowObject.grnStatusId.name + "</p>"
    );
  }
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
    "<head><title>Print GRN</title>" +
      '<script src="resources/js/jquery.js"></script>' +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
      "<h2>GRN Details</h2>" +
      grnReportTable.outerHTML
  );

  setTimeout(function () {
    newTab.print();
  }, 1000);
};
