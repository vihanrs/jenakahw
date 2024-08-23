//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest("/privilege/byloggeduserandmodule/Dashboard");

  //refresh all
  manageNavBar();
  refreshAll();
  getAge();
  getGenderDOBByNIC();
});

const refreshAll = () => {
  refreshCards();
  refreshNotifications();
  refreshTopSellingProducts();
};

const refreshCards = () => {
  customerCount.innerText = ajaxGetRequest(
    "/dashboard/findcustomercountsincelastmonth"
  );

  supplierCount.innerText = ajaxGetRequest("/dashboard/activesuppliercount");

  pendingPOCount.innerText = ajaxGetRequest("/dashboard/pendingpocount");

  completeInvoiceCount.innerText = ajaxGetRequest(
    "/dashboard/completeinvcountsincelastmonth"
  );

  pendingInvoiceCount.innerText = ajaxGetRequest(
    "/dashboard/pendinginvcounttoday"
  );

  invoicesTotal = ajaxGetRequest("/dashboard/invoicetotalsincelastmonth");

  completeInvoiceTotal.innerText = "Rs." + parseFloat(invoicesTotal).toFixed(2);
};

const refreshNotifications = () => {
  if (userRole == "Admin" || userRole == "Manager") {
    divTopSellingProducts.classList.remove("d-none");
    cardCustomer.classList.remove("d-none");
    cardSales.classList.remove("d-none");
    cardCompleteInvoices.classList.remove("d-none");
    cardPendingInvoices.classList.remove("d-none");
  }

  if (
    userRole == "Admin" ||
    userRole == "Manager" ||
    userRole == "Store-Keeper"
  ) {
    divNotification.classList.remove("d-none");
    cardSupPO.classList.remove("d-none");
    divTitle.classList.remove("d-none");
  }

  if (userRole == "Cashier" || userRole == "Helper") {
    divWelcomeContent.classList.remove("d-none");
  }
  notifications.innerHTML = "";
  lowStockProducts = ajaxGetRequest("/dashboard/findlowstockproducts");

  lowStockProducts.forEach((stock) => {
    if (stock.stockStatus.name != "Out of Stock") {
      const p = document.createElement("p");
      p.className = "card-text";

      const span = document.createElement("span");
      span.style.color = "red";
      span.style.fontWeight = "bold";

      if (stock.stockStatus.name == "Out of Stock") {
        span.textContent = "out of Stock : ";
      } else {
        span.textContent = "low stock : ";
      }

      p.appendChild(span);

      p.innerHTML +=
        stock.productId.barcode +
        " " +
        stock.productId.brandId.name +
        " - " +
        stock.productId.name +
        " (Rs." +
        parseFloat(stock.sellPrice).toFixed(2) +
        ")";

      notifications.appendChild(p);
    }
  });
};

const refreshTopSellingProducts = () => {
  topSellingProducts = ajaxGetRequest("/dashboard/topsellingproducts");
  //object count = table column count
  //String - number/string/date
  //function - object/array/boolean
  //currency - RS
  const displayProperties = [
    { property: "name", datatype: "String" },
    { property: "brand", datatype: "String" },
    { property: "category", datatype: "String" },
    { property: "subCategory", datatype: "String" },
    { property: "sellQty", datatype: "String" },
    { property: "totalAmount", datatype: "currency" },
  ];

  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys, user privileges)
  fillDataIntoTable(
    topSellingTable,
    topSellingProducts,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    false,
    userPrivilages
  );
};

const viewRecord = () => {};
const refillRecord = () => {};
const deleteRecord = () => {};
