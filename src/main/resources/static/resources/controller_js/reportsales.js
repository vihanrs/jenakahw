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
  printSummary.addEventListener("click", () => {
    printChart();
  });

  selectType.addEventListener("change", () => {
    refreshSummaryTable();
  });
};

// ********* RESET *********
//function for refresh form and table
const refreshAll = () => {
  // call Summary table refresh fucntion
  refreshSummaryTable();
};

// ********* TABLE OPERATIONS *********

const refreshSummaryTable = () => {
  financeSummary = [];
  if (selectType.value == "daily") {
    financeSummary = ajaxGetRequest("/report/reportsales/dailysummery");
  }

  const displayProperties = [
    { property: "day", datatype: "String" },
    { property: "income", datatype: "currency" },
    { property: "expense", datatype: "currency" },
    { property: getProfit, datatype: "function" },
  ];

  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys, user privileges)
  fillDataIntoTable(
    SalesSummaryTable,
    financeSummary,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    false,
    userPrivilages
  );

  refreshChart(financeSummary);
};

const viewRecord = () => {};
const refillRecord = () => {};
const deleteRecord = () => {};

// function for calculate profit
const getProfit = (rowObject) => {
  profit = parseFloat(rowObject.income) - parseFloat(rowObject.expense);
  if (profit > 0) {
    return (
      '<div style = "background-color:rgba(75, 192, 192, 0.2);padding-left:5px; font-weight:bold">' +
      "Rs." +
      parseFloat(profit).toFixed(2) +
      "</div>"
    );
  } else if (profit < 0) {
    return (
      '<div style = "background-color:rgba(255, 99, 132, 0.2);padding-left:5px; font-weight:bold">' +
      "Rs." +
      parseFloat(profit).toFixed(2) +
      "</div>"
    );
  } else {
    return (
      '<div style = "padding-left:5px; font-weight:bold">' +
      "Rs." +
      parseFloat(profit).toFixed(2) +
      "</div>"
    );
  }
};

const refreshChart = (financeSummary) => {
  const ctx = document.getElementById("myChart");

  myChartView = new Chart(ctx, {
    type: "line",
    data: {
      datasets: [
        {
          label: "Income",
          data: financeSummary,
          backgroundColor: "rgba(75, 192, 192, 0.2)",
          borderColor: "rgba(75, 192, 192, 1)",
          tension: 0.4,
          parsing: {
            xAxisKey: "day",
            yAxisKey: "income",
          },
        },
        {
          label: "Expense",
          data: financeSummary,
          backgroundColor: "rgba(255, 99, 132, 0.2)",
          borderColor: "rgba(255, 99, 132, 1)",
          tension: 0.4,
          parsing: {
            xAxisKey: "day",
            yAxisKey: "expense",
          },
        },
      ],
    },
    options: {
      scales: {
        x: {
          title: {
            display: true,
            text: "Day of Week",
          },
        },
        y: {
          beginAtZero: true,
          title: {
            display: true,
            text: "Amount",
          },
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

  // triger print() after 1000 milsec time out
  setTimeout(function () {
    newWindow.print();
  }, 1000);
};
