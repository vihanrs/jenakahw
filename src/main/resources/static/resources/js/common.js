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
  let userConfirmation = confirm("Are you sure to logout...?");
  if (userConfirmation) {
    window.location.assign("/logout");
  }
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

// alert box functions
// Load alertbox.html content and append it to the body
// function loadAlertBox() {
//   fetch("../../../../templates/alertbox.html") // Adjust the path as necessary
//     .then((response) => response.text())
//     .then((data) => {
//       document.body.insertAdjacentHTML("beforeend", data);
//     })
//     .catch((error) => console.error("Error loading alert box:", error));
// }

// // Add event listener to load the alert box on DOMContentLoaded
// document.addEventListener("DOMContentLoaded", loadAlertBox);

// // Function to show the custom alert box
// function showAlert(imageSrc, title, message) {
//   var alertBox = document.getElementById("customAlertBox");
//   // var alertImage = document.getElementById("alertImage");
//   console.log(document.getElementById("alertTitle"));
//   console.log(document.getElementById("customAlertBox"));
//   var alertTitle = document.getElementById("alertTitle");
//   var alertMessage = document.getElementById("alertMessage");

//   // alertImage.src = imageSrc;
//   alertTitle.textContent = title;
//   alertMessage.textContent = message;

//   alertBox.classList.remove("hidden");
//   document.body.classList.add("no-scroll");
// }

// // Function to hide the custom alert box
// function hideAlert() {
//   var alertBox = document.getElementById("customAlertBox");
//   alertBox.classList.add("hidden");
//   document.body.classList.remove("no-scroll");
// }
