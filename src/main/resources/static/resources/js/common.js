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

  if (message != "") {
    let optionMessage = document.createElement("option");
    optionMessage.value = "";
    optionMessage.selected = "selected";
    optionMessage.disabled = "disable";
    optionMessage.innerText = message;
    fieldId.appendChild(optionMessage);
  }

  for (const data of dataList) {
    let option = document.createElement("option");
    option.value = JSON.stringify(data); //convert into JSON string

    option.innerText = data[property];

    if (selectedValue == data[property]) {
      option.selected = "selected";
    }

    fieldId.appendChild(option);
  }
};

// define fuction for fill data into select dropdown with more than one value, ex- [barcode] product name
const fillMoreDataIntoSelect = (
  fieldId,
  message,
  dataList,
  property,
  property2,
  selectedValue
) => {
  fieldId.innerHTML = "";

  if (message != "") {
    let optionMessage = document.createElement("option");
    optionMessage.value = "";
    optionMessage.selected = "selected";
    optionMessage.disabled = "disable";
    optionMessage.innerText = message;
    fieldId.appendChild(optionMessage);
  }

  for (const data of dataList) {
    let option = document.createElement("option");
    option.value = JSON.stringify(data); //convert into JSON string

    option.innerText =
      data[property] + (data[property2] != null ? " - " + data[property2] : "");

    if (selectedValue == data[property]) {
      option.selected = "selected";
    }

    fieldId.appendChild(option);
  }
};

// define fuction for fill data into type and select dropdown with more than one value, ex- [barcode] product name
const fillMoreDataIntoDataList = (fieldId, dataList, property, property2) => {
  fieldId.innerHTML = "";

  for (const data of dataList) {
    let option = document.createElement("option");
    option.value =
      data[property] + (data[property2] != null ? " - " + data[property2] : "");

    fieldId.appendChild(option);
  }
};

//set default section - add show class to selected div by using id
function showDefaultSection(buttonId, sectionId) {
  document.getElementById(buttonId).classList.remove("collapsed");
  document.getElementById(sectionId).classList.add("show");
}

// function for logout user with user confirmation
const logoutUser = () => {
  let title = "<span style ='color:red; font-size: 25px'>LogOut!</span>";
  let message = "Are you sure you want to logout...?\n";

  showConfirm(title, message).then((userConfirm) => {
    if (userConfirm) {
      window.location.assign("/logout");
    }
  });
};

// function for set elements border color
const setBorderStyle = (elements, borderStyle = "1px solid #ced4da") => {
  elements.forEach((element) => {
    element.style.border = borderStyle;
  });
};

// function for manage form buttons
const manageFormButtons = (opetation, privilegeObj) => {
  if (opetation == "insert") {
    btnUpdate.style.display = "none";
    if (!privilegeObj.insert) {
      btnAdd.style.display = "none";
    } else {
      btnAdd.style.display = "";
    }
  } else if (opetation == "refill") {
    btnAdd.style.display = "none";
    if (!privilegeObj.update) {
      btnUpdate.style.display = "none";
    } else {
      btnUpdate.style.display = "";
    }
  }
};

// function for get month name by month number
const getMonthName = (monthNumber) => {
  const monthNames = [
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December",
  ];

  // Ensure the month number is between 1 and 12
  if (monthNumber < 1 || monthNumber > 12) {
    return "Invalid month number";
  }

  // Subtract 1 from the month number to get the correct index
  return monthNames[monthNumber - 1];
};
