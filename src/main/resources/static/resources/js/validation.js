//text field validation function
const textFieldValidator = (fieldId, pattern, object, property) => {
  const fieldValue = fieldId.value;
  const regPattern = new RegExp(pattern);

  if (fieldValue !== "") {
    if (regPattern.test(fieldValue)) {
      fieldId.style.border = "2px solid #00FF7F";

      //bind value into object property
      console.log(window[object]);
      window[object][property] = fieldValue;
    } else {
      fieldId.style.border = "1px solid red";
      //need to bind null
      window[object][property] = null;
    }
  } else {
    //need to bind null
    window[object][property] = null;
    if (fieldId.required) {
      fieldId.style.border = "1px solid red";
    } else {
      fieldId.style.border = "1px solid #ced4da";
    }
  }
};

//qty validation function
const qtyFieldValidator = (
  fieldId,
  pattern,
  object,
  property,
  maxQty,
  minQty = 0
) => {
  const fieldValue = fieldId.value;
  const regPattern = new RegExp(pattern);

  if (fieldValue !== "") {
    console.log(maxQty + "max");

    if (regPattern.test(fieldValue) && maxQty >= fieldValue) {
      fieldId.style.border = "2px solid #00FF7F";

      //bind value into object property
      console.log(window[object]);
      window[object][property] = fieldValue;
    } else {
      fieldId.style.border = "1px solid red";
      //need to bind null
      window[object][property] = null;
    }
  } else {
    //need to bind null
    window[object][property] = null;
    if (fieldId.required) {
      fieldId.style.border = "1px solid red";
    } else {
      fieldId.style.border = "1px solid #ced4da";
    }
  }
};

// discount validation function
const discountValidator = (
  fieldId,
  totalFieldId,
  precentageFieldId,
  object,
  property
) => {
  let fieldValue = fieldId.value;
  let discount = 0;
  let enterValue = parseFloat(fieldValue);
  let total = parseFloat(totalFieldId.value);

  if (fieldValue != "") {
    if (precentageFieldId.checked) {
      if (
        new RegExp(
          "^(([0-9]{1,2})|([0-9]{1,2}[.][0-9]{1,2})|([1][0][0]))$"
        ).test(fieldValue)
      ) {
        fieldId.style.border = "2px solid #00FF7F";
        discount = (total * enterValue) / 100;
        window[object][property] = discount;
        console.log(window[object]);
      } else {
        fieldId.style.border = "1px solid red";
        window[object][property] = null;
      }
    } else if (
      // validate and calculate fixed discount
      new RegExp("^(([0-9]{0,7})|([0-9]{0,7}[.][0-9]{1,2}))$").test(
        fieldValue
      ) &&
      enterValue <= total
    ) {
      fieldId.style.border = "2px solid #00FF7F";
      discount = enterValue;
      window[object][property] = discount;
      console.log(window[object]);
    } else {
      textDiscount.style.border = "1px solid red";
      window[object][property] = null;
    }
  } else {
    fieldId.style.border = "1px solid #ced4da";
    window[object][property] = null;
  }
};

//dynamic select field validation function
const selectDFieldValidator = (fieldId, object, property) => {
  const fieldValue = fieldId.value;

  if (fieldValue !== "") {
    fieldId.style.border = "2px solid #00FF7F";
    window[object][property] = JSON.parse(fieldValue); //convert to JS object
  } else {
    window[object][property] = null;
    if (fieldId.required) {
      fieldId.style.border = "1px solid red";
    } else {
      fieldId.style.border = "1px solid #ced4da";
    }
  }
};

//dynamic data list validation function
const dataListValidator = (
  fieldId,
  dataListName,
  object,
  property,
  displayProperty,
  isMultiValue = false
) => {
  const fieldValue = fieldId.value;

  if (fieldValue !== "") {
    let dataList = window[dataListName];
    //check null, undefined, 0, false, NaN, ""
    if (Boolean(dataList)) {
      let extIndex = dataList
        .map((data) => data[displayProperty])
        .indexOf(isMultiValue ? fieldValue.split(" ")[0] : fieldValue);

      if (extIndex != -1) {
        fieldId.style.border = "2px solid #00FF7F";
        window[object][property] = dataList[extIndex];
      } else {
        window[object][property] = null;
        if (fieldId.required) {
          fieldId.style.border = "1px solid red";
        } else {
          fieldId.style.border = "1px solid #ced4da";
        }
      }
    } else {
      window[object][property] = null;
      if (fieldId.required) {
        fieldId.style.border = "1px solid red";
      } else {
        fieldId.style.border = "1px solid #ced4da";
      }
    }
  } else {
    window[object][property] = null;
    if (fieldId.required) {
      fieldId.style.border = "1px solid red";
    } else {
      fieldId.style.border = "1px solid #ced4da";
    }
  }
};

//invoice list validation function
const invoiceDataListValidator = (fieldId, dataListName, object, property) => {
  const fieldValue = fieldId.value;

  if (fieldValue !== "") {
    let dataList = window[dataListName];
    //check null, undefined, 0, false, NaN, ""
    if (Boolean(dataList)) {
      let stockId = fieldValue.split(")")[0];

      let extIndex = dataList
        .map((data) => Number(data["id"]))
        .indexOf(Number(stockId));
      // covert values to number to ensure no type mismatches

      // let extIndex = -1;

      // for (const index in dataList) {
      //   if (
      //     fieldValue ==
      //     dataList[index]["barcode"] +
      //       " - " +
      //       dataList[index]["brand"] +
      //       " " +
      //       dataList[index]["name"] +
      //       " | Rs." +
      //       dataList[index]["sellPrice"] +
      //       " | (" +
      //       dataList[index]["availableQty"] +
      //       ")"
      //   ) {
      //     extIndex = index;
      //     break;
      //   } else {
      //     console.log("T2");
      //   }
      // }

      if (extIndex != -1) {
        fieldId.style.border = "2px solid #00FF7F";
        window[object][property] = dataList[extIndex];
      } else {
        window[object][property] = null;
        if (fieldId.required) {
          fieldId.style.border = "1px solid red";
        } else {
          fieldId.style.border = "1px solid #ced4da";
        }
      }
    } else {
      window[object][property] = null;
      if (fieldId.required) {
        fieldId.style.border = "1px solid red";
      } else {
        fieldId.style.border = "1px solid #ced4da";
      }
    }
  } else {
    window[object][property] = null;
    if (fieldId.required) {
      fieldId.style.border = "1px solid red";
    } else {
      fieldId.style.border = "1px solid #ced4da";
    }
  }
};

//select field validation function
const selectFieldValidator = (fieldId, object, property) => {
  const fieldValue = fieldId.value;

  if (fieldValue !== "") {
    fieldId.style.border = "2px solid #00FF7F";
    window[object][property] = fieldValue;
  } else {
    fieldId.style.border = "1px solid #ced4da";
    window[object][property] = null;
  }
};

//generate calling name values
const generateCallingNameValues = (fullNameField, DataListId) => {
  const callingNames = document.getElementById(DataListId);
  callingNames.innerHTML = "";

  callingNamePartList = fullNameField.value.split(" ");
  callingNamePartList.forEach((item) => {
    const option = document.createElement("option");
    option.value = item;
    callingNames.appendChild(option);
  });
};

// date field validation function
const dateFieldValidator = (fieldId, object, property) => {
  const fieldValue = fieldId.value;
  const regPattern = new RegExp("^[0-9]{4}[-][0-9]{2}[-][0-9]{2}$");

  if (fieldValue !== "") {
    if (regPattern.test(fieldValue)) {
      fieldId.style.border = "2px solid #00FF7F";

      //bind value into object property
      console.log(window[object]);
      window[object][property] = fieldValue;
    } else {
      fieldId.style.border = "1px solid red";
      //need to bind null
      window[object][property] = null;
    }
  } else {
    //need to bind null
    window[object][property] = null;
    if (fieldId.required) {
      fieldId.style.border = "1px solid red";
    } else {
      fieldId.style.border = "1px solid #ced4da";
    }
  }
};

// radio field validation function
const radioFieldValidator = (fieldId, object, property) => {
  const fieldValue = fieldId.value;

  if (fieldValue.checked) {
    //bind value into object property
    window[object][property] = fieldValue;
  } else {
    //need to bind null
    //window[object][property] = null;
    window[object][property] = fieldValue;
  }
};

// checkbox validation function
const checkBoxValidator = (
  fieldId,
  object,
  property,
  trueValue,
  falseValue,
  lableId,
  labelTrueValue,
  labelFalseValue
) => {
  if (fieldId.checked) {
    //bind value into object property
    window[object][property] = trueValue;
    lableId.innerText = labelTrueValue;
  } else {
    //need to bind null
    window[object][property] = falseValue;
    lableId.innerText = labelFalseValue;
  }
};

// image field validation function
const fileFieldValidator = (
  fieldId,
  object,
  propertyOne,
  propertyTwo,
  previewId,
  nameFieldId
) => {
  if (fieldId.value != "") {
    let file = fieldId.files[0]; // file details object (name)
    nameFieldId.value = file["name"];
    window[object][propertyOne] = file["name"]; // bind img name

    let fileReader = new FileReader();

    fileReader.onload = function (e) {
      //set selected image
      previewId.src = e.target.result;
      window[object][propertyTwo] = btoa(e.target.result); //bind image
    };

    fileReader.readAsDataURL(file);
    return;
  }
};
