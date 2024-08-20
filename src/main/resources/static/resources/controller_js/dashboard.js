//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest("/privilege/byloggeduserandmodule/Dashboard");

  //refresh all
  refreshAll();
  getAge();
  getGenderDOBByNIC();
});

const refreshAll = () => {
  refreshCards();
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
