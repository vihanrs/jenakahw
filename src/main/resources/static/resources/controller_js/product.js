//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest("/privilege/byloggeduserandmodule/Product");
  //   userPrivilages.insert = false;
  // userPrivilages.update = false;
  // userPrivilages.delete = false;

  //refresh all
  //   refreshAll();

  //set default selected section
  if (userPrivilages.insert) {
    showDefaultSection("addNewButton", "addNewSection");
  } else {
    showDefaultSection("viewAllButton", "viewAllSection");
    addAccordion.style.display = "none";
  }
  //call all event listners
  //   addEventListeners();
});

selectBrand;
selectCategory;
selectSubCategory;
textProductName;
textProductDescription;
