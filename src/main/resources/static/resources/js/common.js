//define function for ajax request (GET)
const ajaxGetRequest = (url, params) => {
  let serverResponse;

  // Append query parameters to the URL
  if (params) {
    url += "?" + $.param(params);
  }

  $.ajax(url, {
    async: false,
    type: "GET",
    dataType: "json",
    success: function (data, status, ahr) {
      console.log(data);
      console.log("success " + url + " " + status + " " + ahr);
      serverResponse = data;
    },
    error: function (errormsg, status, ahr) {
      console.log("failed " + url + " " + errormsg + " " + status + " " + ahr);

      serverResponse = [];
    },
  });
  return serverResponse;
};
// define function for ajax request (POST,PUT,DELETE)
const ajaxRequestBody = (url, method, object) => {
  let serverResponse;
  $.ajax(url, {
    async: false,
    type: method,
    data: JSON.stringify(object),
    contentType: "application/json",
    success: function (data, status, ahr) {
      console.log(data);
      console.log(url + "\n" + "success " + status + " " + ahr);
      serverResponse = data;
    },
    error: function (errormsg, status, ahr) {
      console.log(
        url + "\n" + "failed  " + errormsg + " " + status + " " + ahr
      );
      serverResponse = errormsg;
    },
  });

  return serverResponse;
};

// define fuction for fill data into select dropdown
const fillDataIntoSelect = (
  fieldId,
  message,
  dataList,
  property,
  selectedValue
) => {
  fieldId.innerHTML = "";
  let optionMessage = document.createElement("option");
  optionMessage.value = "";
  optionMessage.selected = "selected";
  optionMessage.disabled = "disable";
  optionMessage.innerText = message;
  fieldId.appendChild(optionMessage);

  for (const data of dataList) {
    let option = document.createElement("option");
    option.value = JSON.stringify(data); //convert into JSON string

    let value = "";
    if (property.includes(".")) {
      if (property.includes(",")) {
        value = getPropertiesConcatenated(data, property);
      } else {
        value = getPropertyNested(data, property);
      }
    } else {
      value = data[property];
    }

    option.innerText = value;
    if (selectedValue == value) {
      option.selected = "selected";
    }
    fieldId.appendChild(option);
  }
};

// Function to access nested properties
const getPropertyNested = (object, property) => {
  const properties = property.split(".");
  return properties.reduce((obj, prop) => obj && obj[prop], object);
};

// Function to access and concatenate multiple nested properties
const getPropertiesConcatenated = (object, properties) => {
  const propertyArray = properties.split(",");
  const propertyValues = propertyArray.map((property) =>
    getPropertyNested(object, property)
  );
  return propertyValues.join(" - "); // Adjust the separator as needed
};

//set default section - add show class to selected div by using id
function showDefaultSection(buttonId, sectionId) {
  document.getElementById(buttonId).classList.remove("collapsed");
  document.getElementById(sectionId).classList.add("show");
}
