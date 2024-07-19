// selectBrand;
// selectCategory;
// selectSubCategory;
// textProductName;
// selectUnitType;
// textProductDescription;
// textROL;
// textLocation;
// textProfitRate;
// selectStatus;
//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest("/privilege/byloggeduserandmodule/Product");

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
  selectBrand.addEventListener("change", () => {
    selectDFieldValidator(selectBrand, "product", "brandId");
  });

  selectCategory.addEventListener("change", () => {
    selectDFieldValidator(selectCategory, "product", "categoryId"),
      getSubCategoriesByCategory(product.categoryId.id);
  });

  selectSubCategory.addEventListener("change", () => {
    selectDFieldValidator(selectSubCategory, "product", "subCategoryId");
  });

  textProductName.addEventListener("keyup", () => {
    textFieldValidator(textProductName, "^.*$", "product", "name");
  });

  selectUnitType.addEventListener("change", () => {
    selectDFieldValidator(selectUnitType, "product", "unitTypeId");
  });

  textProductDescription.addEventListener("keyup", () => {
    textFieldValidator(textProductDescription, "", "product", "description");
  });

  textROL.addEventListener("keyup", () => {
    textFieldValidator(
      textROL,
      "^(([1-9]{1}[0-9]{0,5})|([0-9]{0,5}[.][0-9]{1,2}))$",
      "product",
      "rol"
    );
  });

  textLocation.addEventListener("keyup", () => {
    textFieldValidator(textLocation, "", "product", "location");
  });

  textProfitRate.addEventListener("keyup", () => {
    textFieldValidator(
      textProfitRate,
      "^(([1-9]{1}[0-9]{0,2})|([1-9]{1}[0-9]{0,2}[.][0-9]{1,3})|([0-9]{1}[.][0-9]{1,3}))$",
      "product",
      "profitRate"
    );
  });

  selectStatus.addEventListener("change", () => {
    selectDFieldValidator(selectStatus, "product", "productStatusId");
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
    // printViewRecord();
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
  product = {};

  //get brands
  brands = ajaxGetRequest("/brand/findall");
  fillDataIntoSelect(selectBrand, "Select Brand", brands, "name");

  // get categories
  categories = ajaxGetRequest("/category/findall");
  fillDataIntoSelect(selectCategory, "Select Category", categories, "name");

  selectSubCategory.disabled = true;

  // get unit-type
  unittypes = ajaxGetRequest("/unittype/findall");
  fillDataIntoSelect(selectUnitType, "Select Unit Type", unittypes, "name");

  // get status
  statuses = ajaxGetRequest("/productstatus/findall");
  fillDataIntoSelect(
    selectStatus,
    "Select Status",
    statuses,
    "name",
    "Available"
  );

  //bind default selected status in to supplier object and set valid color
  product.productStatusId = JSON.parse(selectStatus.value);
  selectStatus.style.border = "2px solid #00FF7F";

  //empty all elements
  textProductName.value = "";
  textProductDescription.value = "";
  textROL.value = "";
  textLocation.value = "";
  textProfitRate.value = "";
  selectSubCategory.value = "";

  //set default border color
  let elements = [
    selectBrand,
    selectCategory,
    selectSubCategory,
    selectUnitType,
    textProductName,
    textProductDescription,
    textROL,
    textLocation,
    textProfitRate,
  ];
  setBorderStyle(elements);

  //manage form buttons
  manageFormButtons("insert", userPrivilages);
};

const getSubCategoriesByCategory = (categoryId, selectedValue = null) => {
  selectSubCategory.disabled = false;

  // if function call in category event listner - need to reset product.subCategory id
  if (selectedValue == null) {
    product.subCategoryId = null;
  }

  subCategories = ajaxGetRequest(
    "/subcategory/findbycategory?categoryid=" + categoryId
  );
  fillDataIntoSelect(
    selectSubCategory,
    "Select Sub-Category",
    subCategories,
    "name",
    selectedValue != null ? selectedValue : ""
  );
};

//function for check errors
const checkErrors = () => {
  //need to check all required prperty filds
  let error = "";

  if (product.brandId == null) {
    error = error + "Please Select Valid Brand...!\n";
    selectBrand.style.border = "1px solid red";
  }
  if (product.categoryId == null) {
    error = error + "Please Select Valid Category...!\n";
    selectCategory.style.border = "1px solid red";
  }
  if (product.subCategoryId == null) {
    error = error + "Please Select Valid Sub-Category...!\n";
    selectSubCategory.style.border = "1px solid red";
  }

  if (product.name == null) {
    error = error + "Please Enter Product Name...!\n";
    textProductName.style.border = "1px solid red";
  }

  if (product.profitRate == null) {
    error = error + "Please Enter Valid Profit Rate...!\n";
    textProfitRate.style.border = "1px solid red";
  }

  if (product.productStatusId == null) {
    error = error + "Please Select Valid Status...!\n";
    selectStatus.style.border = "1px solid red";
  }

  return error;
};

//function for check updates
const checkUpdates = () => {
  let updates = "";

  if (oldproduct.brandId.id != product.brandId.id) {
    updates +=
      "Brand has changed " +
      oldproduct.brandId.name +
      " into " +
      product.brandId.name +
      " \n";
  }
  if (oldproduct.categoryId.id != product.categoryId.id) {
    updates +=
      "Category has changed " +
      oldproduct.categoryId.name +
      " into " +
      product.categoryId.name +
      " \n";
  }

  if (oldproduct.subCategoryId.id != product.subCategoryId.id) {
    updates +=
      "Sub-Category has changed " +
      oldproduct.subCategoryId.name +
      " into " +
      product.subCategoryId.name +
      " \n";
  }

  if (oldproduct.name != product.name) {
    updates +=
      "Name has changed " + oldproduct.name + " into " + product.name + " \n";
  }

  if (oldproduct.unitTypeId.id != product.unitTypeId.id) {
    updates +=
      "Unit Type has changed " +
      oldproduct.unitTypeId.name +
      " into " +
      product.unitTypeId.name +
      " \n";
  }

  if (oldproduct.description != product.description) {
    updates +=
      "Description has changed " +
      (oldproduct.description ?? "-") +
      " into " +
      (product.description ?? "-") +
      " \n";
  }

  if (oldproduct.rol != product.rol) {
    updates +=
      "Re-Order Level has changed " +
      (oldproduct.rol ?? "-") +
      " into " +
      (product.rol ?? "-") +
      " \n";
  }

  if (oldproduct.location != product.location) {
    updates +=
      "Location Level has changed " +
      (oldproduct.location ?? "-") +
      " into " +
      (product.location ?? "-") +
      " \n";
  }

  if (oldproduct.profitRate != product.profitRate) {
    updates +=
      "Profit Rate has changed " +
      oldproduct.profitRate +
      "% into Rs." +
      product.profitRate +
      "% \n";
  }

  if (oldproduct.productStatusId.id != product.productStatusId.id) {
    updates +=
      "Status has changed " +
      oldproduct.productStatusId.name +
      " into " +
      product.productStatusId.name +
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
      "Product Name : " +
      product.name +
      "\nBrand : " +
      product.brandId.name +
      "\nCategory : " +
      product.categoryId.name +
      "\nSub-Category : " +
      product.subCategoryId.name +
      "\nUnit Type : " +
      product.unitTypeId.name +
      "\nStatus : " +
      product.productStatusId.name;
    showConfirm(title, message).then((userConfirm) => {
      if (userConfirm) {
        //pass data into back end
        let serverResponse = ajaxRequestBody("/product", "POST", product); // url,method,object

        //check back end response
        if (serverResponse == "OK") {
          showAlert("success", "Save sucessfully..! " + serverResponse);
          //need to refresh table and form
          refreshAll();
        } else {
          showAlert(
            "error",
            "Save not sucessfully..! have some errors \n" + serverResponse
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
          let serverResponse = ajaxRequestBody("/product", "PUT", product);
          if (serverResponse == "OK") {
            showAlert("success", "Update sucessfully..!").then(() => {
              //need to refresh table and form
              refreshAll();
            });
          } else {
            showAlert(
              "error",
              "Update not sucessfully..! have some errors \n" + serverResponse
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
  products = ajaxGetRequest("/product/findall");

  //object count = table column count
  //String - number/string/date
  //function - object/array/boolean
  //currency - RS
  const displayProperties = [
    { property: "barcode", datatype: "String" },
    { property: "name", datatype: "String" },
    { property: getBrand, datatype: "function" },
    { property: getCategory, datatype: "function" },
    { property: getSubCategory, datatype: "function" },
    { property: "location", datatype: "String" },
    { property: getStatus, datatype: "function" },
  ];

  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys, user privileges)
  fillDataIntoTable(
    productTable,
    products,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    true,
    userPrivilages
  );

  //hide delete button when status is 'deleted'
  products.forEach((product, index) => {
    if (userPrivilages.delete && product.productStatusId.name == "Deleted") {
      //catch the button
      let targetElement =
        productTable.children[1].children[index].children[8].children[
          userPrivilages.update && userPrivilages.insert ? 2 : 1
        ];
      //add changes
      targetElement.style.pointerEvents = "none";
      targetElement.style.visibility = "hidden";
    }
  });

  $("#productTable").dataTable();
};

// function for get brand
const getBrand = (rowObject) => {
  return rowObject.brandId.name;
};

// function for get category
const getCategory = (rowObject) => {
  return rowObject.subCategoryId.categoryId.name;
};

// function for get sub category
const getSubCategory = (rowObject) => {
  return rowObject.subCategoryId.name;
};

// function for get status
const getStatus = (rowObject) => {
  if (rowObject.productStatusId.name == "Available") {
    return (
      '<p class = "status status-active">' +
      rowObject.productStatusId.name +
      "</p>"
    );
  } else if (rowObject.productStatusId.name == "Un-Available") {
    return (
      '<p class = "status status-warning">' +
      rowObject.productStatusId.name +
      "</p>"
    );
  } else if (rowObject.productStatusId.name == "Deleted") {
    return (
      '<p class = "status status-error">' +
      rowObject.productStatusId.name +
      "</p>"
    );
  }
};

//function for view record
const viewRecord = () => {};

//function for refill record
const refillRecord = (rowObject, rowId) => {
  $("#addNewButton").click();

  product = JSON.parse(JSON.stringify(rowObject)); //convert rowobject to json string and covert back it to js object
  oldproduct = JSON.parse(JSON.stringify(rowObject)); // deep copy - create compeletely indipended two objects

  product.categoryId = product.subCategoryId.categoryId;
  oldproduct.categoryId = oldproduct.subCategoryId.categoryId;

  textProductName.value = product.name;
  textProfitRate.value = product.profitRate;

  //set optional fields
  textProductDescription.value =
    product.description != null ? product.description : "";

  textROL.value = product.rol != null ? product.rol : "";

  textLocation.value = product.location != null ? product.location : "";

  // ***if we have optional join column then need to check null

  // set brand
  fillDataIntoSelect(
    selectBrand,
    "Select Brand",
    brands,
    "name",
    product.brandId.name
  );

  // set category
  fillDataIntoSelect(
    selectCategory,
    "Select Category",
    categories,
    "name",
    product.subCategoryId.categoryId.name
  );

  // set sub category
  getSubCategoriesByCategory(
    product.subCategoryId.categoryId.id,
    product.subCategoryId.name
  );
  // set unit type
  fillDataIntoSelect(
    selectUnitType,
    "Select Unit Type",
    unittypes,
    "name",
    product.unitTypeId.name
  );

  // set status
  fillDataIntoSelect(
    selectStatus,
    "Select Status",
    statuses,
    "name",
    product.productStatusId.name
  );

  setBorderStyle([
    selectBrand,
    selectCategory,
    selectSubCategory,
    textProductName,
    selectUnitType,
    textProductDescription,
    textROL,
    textLocation,
    textProfitRate,
    selectStatus,
  ]);

  //manage buttons
  manageFormButtons("refill", userPrivilages);
};

// //function for delete record
const deleteRecord = (rowObject, rowId) => {
  //get user confirmation
  let title = "Are you sure!\nYou wants to delete following record? \n";
  let message = "Product Name : " + rowObject.name;

  showConfirm(title, message).then((userConfirm) => {
    if (userConfirm) {
      //response from backend ...
      let serverResponse = ajaxRequestBody("/product", "DELETE", rowObject); // url,method,object
      //check back end response
      if (serverResponse == "OK") {
        showAlert("success", "Delete successfully..! \n" + serverResponse).then(
          () => {
            // Need to refresh table and form
            refreshAll();
          }
        );
      } else {
        showAlert(
          "error",
          "Delete not successfully..! have some errors \n" + serverResponse
        );
      }
    }
  });
};
