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
const fillMoreDataIntoDataList = (
  fieldId,
  dataList,
  property,
  property2 = null
) => {
  fieldId.innerHTML = "";

  for (const data of dataList) {
    let option = document.createElement("option");
    option.value =
      data[property] + (data[property2] != null ? " " + data[property2] : "");

    fieldId.appendChild(option);
  }
};

// define fuction for fill data into type and select dropdown with more than one value, ex- [barcode] product name
const fillFullDataIntoDataList = (
  fieldId,
  dataList,
  id,
  property,
  property2,
  property3,
  property4,
  property5
) => {
  fieldId.innerHTML = "";

  for (const data of dataList) {
    let option = document.createElement("option");
    option.value =
      data[id] +
      ") " +
      data[property] +
      " - " +
      data[property2] +
      " " +
      data[property3] +
      " | Rs." +
      parseFloat(data[property4]).toFixed(2) +
      " (" +
      data[property5] +
      ")";

    fieldId.appendChild(option);
  }
};

//set default section - add show class to selected div by using id
function showDefaultSection(buttonId, sectionId) {
  document.getElementById(buttonId).classList.remove("collapsed");
  document.getElementById(sectionId).classList.add("show");
}

// ************************* logged user related functions start*************************

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

//function for update user profile
const submitUserSettings = () => {
  if (isUserProfileUpdated()) {
    let serverResponse = ajaxRequestBody(
      "/user/updateprofile",
      "PUT",
      loggedUser
    ); // url,method,object
    //check back end response
    if (serverResponse == "OK") {
      showAlert("success", "User Profile Update successfully..!").then(() => {
        // Need to refresh table and form
        window.location.assign("/logout");
      });
    } else {
      showAlert(
        "error",
        "User profile update not successfully..!\n" + serverResponse
      );
    }
  } else {
    showAlert("warning", "Nothing to update");
  }
};

// function for check user profile updates
const isUserProfileUpdated = () => {
  if (
    oldLoggedUser.email != loggedUser.email ||
    oldLoggedUser.username != loggedUser.username ||
    loggedUser.password != null ||
    oldLoggedUser.userPhoto != loggedUser.userPhoto
  ) {
    return true;
  } else {
    return false;
  }
};

// function for refresh user profile model
const refreshUserProfile = () => {
  loggedUser = ajaxGetRequest("/user/loggeduser");
  oldLoggedUser = JSON.parse(JSON.stringify(loggedUser));

  // set user image
  if (loggedUser.userPhoto == null) {
    imgUserPhoto.src = "resources/images/default-user-img.jpg";
    textUserPhoto.value = "";
  } else {
    imgUserPhoto.src = atob(loggedUser.userPhoto);
    textUserPhoto.value = loggedUser.photoName;
  }
  textEmail.value = loggedUser.email;
  textUsername.value = loggedUser.username;
  textPassword.value = "";
  textRPassword.value = "";

  setBorderStyle([textEmail, textUsername, textPassword, textRPassword]);
};

// function for clear user photo and set default
const clearProfileUserPhoto = () => {
  loggedUser.userPhoto = null;
  loggedUser.photoName = null;
  imgUserPhoto.src = "resources/images/default-user-img.jpg";
  textUserPhoto.value = "";
  fileUserPhoto.files = null;
};

//function for check user password
const profilePasswordRTValidator = () => {
  if (textPassword.value != "") {
    if (textPassword.value == textRPassword.value) {
      textPassword.style.border = "2px solid #00FF7F";
      textRPassword.style.border = "2px solid #00FF7F";
      loggedUser.password = textPassword.value;
    } else {
      textPassword.style.border = "1px solid red";
      textRPassword.style.border = "1px solid red";
      loggedUser.password = null;
    }
  } else {
    // showAlert("warning", "Please fill the password field first...!");
    textPassword.style.border = "1px solid red";
    textRPassword.style.border = "1px solid red";
    // textRPassword.value = "";
    // textPassword.focus();
  }
};

// ************************* logged user related functions end*************************

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

// function for get age from date
const getAge = (filedId) => {
  // create date objects
  let dateDOB = new Date("1998-10-17"); //new Date(filedId.value)

  let dateDifference = new Date().getTime() - dateDOB.getTime();
  let difDate = new Date(dateDifference);

  let age = Math.abs(difDate.getFullYear() - 1970);

  console.log(age);
};

// function for get gender and dob by nic
const getGenderDOBByNIC = (filedId) => {
  let nicValue = "980480526V";
  let year, days;
  let month, date;

  if (
    new RegExp("^(([0-9]{9}[Vv])|([2][0][0-9]{2}[0-9]{8}))$").test(nicValue)
  ) {
    if (nicValue.length == 10) {
      year = "19" + nicValue.substring(0, 2);
      days = nicValue.substring(2, 5);
    } else if (nicValue.length == 12) {
      year = nicValue.substring(0, 4);
      days = nicValue.substring(4, 7);
    }

    //get gender
    if (days < 500) {
      console.log("Male");
    } else {
      console.log("Female");
      days -= 500;
    }

    let dateDOB = new Date(year); // get birthday year

    if (year % 4 != 0) {
      dateDOB.setDate(parseInt(days) - 1);
    } else {
      dateDOB.setDate(parent(days));
    }

    month = dateDOB.getMonth() + 1;
    if (month < 10) month = "0" + month;

    date = dateDOB.getDate();
    if (date < 10) date = "0" + date;

    dob = year + "-" + month + "-" + date;

    console.log(dob);
  }
};

const manageNavBar = () => {
  loggedUserModules = ajaxGetRequest("/module/listbyloggeduser");

  console.log(loggedUserModules);

  for (let module of loggedUserModules) {
    let element = document.getElementById(module);
    element.classList.remove("d-none");
  }

  // manage payments in side bar
  let hasPayments = false;
  paymentModules = [
    "Supplier Payment",
    "Invoice Payment",
    "Customer Payment",
    "Daily Extra Income",
    "Daily Expenses",
  ];
  paymentModules.forEach((module) => {
    if (loggedUserModules.includes(module)) {
      hasPayments = true;
      return;
    }
  });

  if (hasPayments) {
    document.getElementById("Payment").classList.remove("d-none");
  }

  //special cases
  if (userRole == "Admin" || userRole == "Manager") {
    document.getElementById("Customer").classList.remove("d-none");
  }
};
