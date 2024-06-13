//Access Browser onload event
window.addEventListener("load", () => {
  //get logged user privileges
  userPrivilages = ajaxGetRequest("/privilege/byloggeduserandmodule/User");
  // userPrivilages.insert = false;
  // userPrivilages.update = false;
  // userPrivilages.delete = true;

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
  let namePattern = "^[A-Z][a-z]{2,20}$";
  let contactPattern = "^[0][7][01245678][0-9]{7}$";
  let nicPattern = "^(([0-9]{9}[VvXxSs])|([2][0][0-9]{2}[0-9]{8}))$";
  let emailPattern = "^[A-Za-z0-9]{6,20}[@][a-z]{3,10}[.][a-z]{2,3}$";
  let unameandpwPattern = "^^[a-zA-Z0-9]{5,16}$";

  textFirstName.addEventListener("keyup", () => {
    textFieldValidator(textFirstName, namePattern, "user", "firstName");
  });

  textLastName.addEventListener("keyup", () => {
    textFieldValidator(textLastName, namePattern, "user", "lastName");
  });

  textContact.addEventListener("keyup", () => {
    textFieldValidator(textContact, contactPattern, "user", "contact");
  });

  textNIC.addEventListener("keyup", () => {
    textFieldValidator(textNIC, nicPattern, "user", "nic");
  });

  radioGenderMale.addEventListener("change", () => {
    radioFieldValidator(radioGenderMale, "user", "gender");
  });

  radioGenderFemale.addEventListener("change", () => {
    radioFieldValidator(radioGenderFemale, "user", "gender");
  });

  textEmail.addEventListener("keyup", () => {
    textFieldValidator(textEmail, emailPattern, "user", "email");
  });

  textUsername.addEventListener("keyup", () => {
    textFieldValidator(textUsername, unameandpwPattern, "user", "username");
  });

  textPassword.addEventListener("keyup", () => {
    textFieldValidator(textPassword, unameandpwPattern, "user", "password");
  });

  textRPassword.addEventListener("keyup", () => {
    passwordRTValidator();
  });

  selectStatus.addEventListener("change", () => {
    selectDFieldValidator(selectStatus, "user", "userStatusId");
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
    printViewRecord();
  });
};

//function for check the password
const passwordRTValidator = () => {
  if (textPassword.value != "") {
    if (textPassword.value == textRPassword.value) {
      textPassword.style.border = "2px solid #00FF7F";
      textRPassword.style.border = "2px solid #00FF7F";
      user.password = textPassword.value;
    } else {
      textPassword.style.border = "1px solid red";
      textRPassword.style.border = "1px solid red";
      user.password = null;
    }
  } else {
    alert("Please fill the password field first...!");
    textPassword.style.border = "1px solid red";
    textRPassword.style.border = "1px solid red";
    textRPassword.value = "";
    textPassword.focus();
  }
};

// ********* RESET *********
//function for refresh form and table
const refreshAll = () => {
  //Call form refresh function
  refreshForm();
  //Call table refresh function
  refreshTable();
};

//function for refresh form area
const refreshForm = () => {
  //create empty object
  user = {};
  user.roles = new Array();

  //get data list from select element
  userstatus = ajaxGetRequest("/userstatus/findall");
  fillDataIntoSelect(selectStatus, "Select Status", userstatus, "name");

  roles = ajaxGetRequest("/role/findall");
  createViewRolesUI();

  //empty all elements
  textFirstName.value = "";
  textLastName.value = "";
  textContact.value = "";
  textNIC.value = "";
  radioGenderMale.checked = false;
  radioGenderFemale.checked = false;
  textEmail.value = "";
  textUsername.value = "";
  textPassword.value = "";
  textRPassword.value = "";

  //enable pw field
  textPassword.disabled = false;
  textRPassword.disabled = false;

  //set default colors
  textFirstName.style.border = "1px solid #ced4da";
  textLastName.style.border = "1px solid #ced4da";
  textContact.style.border = "1px solid #ced4da";
  textNIC.style.border = "1px solid #ced4da";
  textEmail.style.border = "1px solid #ced4da";
  textUsername.style.border = "1px solid #ced4da";
  textPassword.style.border = "1px solid #ced4da";
  textRPassword.style.border = "1px solid #ced4da";
  selectStatus.style.border = "1px solid #ced4da";

  //manage buttons
  btnUpdate.style.display = "none";
  if (!userPrivilages.insert) {
    btnAdd.style.display = "none";
  } else {
    btnAdd.style.display = "";
  }
};

//function for refresh table records
const refreshTable = () => {
  //array for store data list
  users = ajaxGetRequest("/user/findall");

  //object count = table column count
  //String - number/string/date
  //function - object/array/boolean
  const displayProperties = [
    { property: "empId", datatype: "String" },
    { property: "firstName", datatype: "String" },
    { property: "lastName", datatype: "String" },
    { property: "username", datatype: "String" },
    { property: getRoles, datatype: "function" },
    { property: getStatus, datatype: "function" },
  ];

  //call the function (tableID,dataList,display property list, view function name, refill function name, delete function name, button visibilitys)
  fillDataIntoTable(
    tblUser,
    users,
    displayProperties,
    viewRecord,
    refillRecord,
    deleteRecord,
    true,
    userPrivilages
  );

  //disable delete button when status is 'resigned'
  users.forEach((user, index) => {
    if (userPrivilages.delete && user.userStatusId.name == "Resigned") {
      //catch the button
      let targetElement =
        tblUser.children[1].children[index].children[7].children[
          userPrivilages.update && userPrivilages.insert ? 2 : 1
        ];
      //add changes
      targetElement.style.pointerEvents = "none";
      targetElement.style.visibility = "hidden";
    }
  });

  $("#tblUser").dataTable();
};

// ********* TABLE OPERATIONS *********

//function for set status column
const getStatus = (rowObject) => {
  let styles =
    "padding:1px;text-align: center;font-weight: bold;border-radius: 10px;";
  if (rowObject.userStatusId.name == "Working") {
    return (
      '<p style="' +
      styles +
      'background-color: rgb(49, 255, 49);">' +
      rowObject.userStatusId.name +
      "</p>"
    );
  } else if (rowObject.userStatusId.name == "Inative") {
    return (
      '<p style = "' +
      styles +
      'background-color:  rgb(255, 231, 49);">' +
      rowObject.userStatusId.name +
      "</p>"
    );
  } else {
    return (
      '<p style = "' +
      styles +
      'background-color: rgb(255, 20, 00);">' +
      rowObject.userStatusId.name +
      "</p>"
    );
  }
};

//function for get roles as a text
const getRoles = (rowObject) => {
  let rolesList = rowObject.roles.map((role) => role.name).join(", ");
  return "<p>" + rolesList + "</p>";
};

//function for view record
const viewRecord = (rowObject, rowId) => {
  //need to get full object
  let printObj = rowObject;

  tdFirstName.innerText = printObj.firstName;
  tdLastName.innerText = printObj.lastName;
  tdContact.innerText = printObj.contact;
  tdNIC.innerText = printObj.nic;
  tdGender.innerText = printObj.gender;
  tdEmail.innerText = printObj.email;
  tdUsername.innerText = printObj.username;

  tdRole.innerText = printObj.roles.map((role) => role.name).join(", ");

  tdStatus.innerText = printObj.userStatusId.name;
  //open model
  $("#modelDetailedView").modal("show");
};

//function for refill record
const refillRecord = (rowObject, rowId) => {
  $("#addNewButton").click();

  // default = rowObject;
  user = JSON.parse(JSON.stringify(rowObject));
  oldUserObj = JSON.parse(JSON.stringify(rowObject));

  //set normal fields
  textFirstName.value = user.firstName;
  textContact.value = user.contact;
  textNIC.value = user.nic;
  textEmail.value = user.email;
  textUsername.value = user.username;
  textPassword.value = "";
  textRPassword.value = "";

  //disable pw field
  textPassword.disabled = true;
  textRPassword.disabled = true;

  radioGenderMale.checked = false;
  radioGenderFemale.checked = false;
  //set conditional value fields
  if (user.gender == "Male") {
    radioGenderMale.checked = true;
  } else {
    radioGenderFemale.checked = true;
  }

  //set optional fields
  if (user.lastName != null) textLastName.value = user.lastName;
  else textLastName.value = "";

  //if we have optional join column then need to check null
  // cmbDesignation
  fillDataIntoSelect(
    selectStatus,
    "Select Status",
    userstatus,
    "name",
    user.userStatusId.name
  );

  //define roles
  createViewRolesUI();
  setSelectedRoles();

  //manage buttons
  btnAdd.style.display = "none";
  if (!userPrivilages.update) {
    btnUpdate.style.display = "none";
  } else {
    btnUpdate.style.display = "";
  }
};

//function for delete record
const deleteRecord = (rowObject, rowId) => {
  //get user confirmation
  const userConfirm = confirm(
    "Are you sure!\nYou wants to delete following record? \n" +
      "First Name : " +
      rowObject.firstName +
      "\n" +
      "Username : " +
      rowObject.username +
      "\n" +
      "Email : " +
      rowObject.email
  );

  if (userConfirm) {
    //response from backend ...
    let serverResponse = ajaxRequestBody("/user", "DELETE", rowObject); // url,method,object
    //check back end response
    if (serverResponse == "OK") {
      alert("Delete sucessfully..! \n" + serverResponse);
      //need to refresh table and form
      refreshAll();
    } else {
      alert("Delete not sucessfully..! have some errors \n" + serverResponse);
    }
  }
};

// ********* FORM OPERATIONS *********

//function for check errors
const checkErrors = () => {
  //need to check all required prperty filds
  let error = "";

  if (user.firstName == null) {
    error = error + "Please Enter Valid First Name...!\n";
    textFirstName.style.border = "1px solid red";
  }
  if (user.contact == null) {
    error = error + "Please Enter Valid Contact No...!\n";
    textContact.style.border = "1px solid red";
  }
  if (user.nic == null) {
    error = error + "Please Enter Valid NIC No...!\n";
    textNIC.style.border = "1px solid red";
  }
  if (user.gender == null) {
    error = error + "Please Select Gender...!\n";
  }
  if (user.email == null) {
    error = error + "Please Enter Valid Email...!\n";
    textEmail.style.border = "1px solid red";
  }
  if (user.username == null) {
    error = error + "Please Enter Valid Username...!\n";
    textUsername.style.border = "1px solid red";
  }
  if (user.password == null) {
    error = error + "Please Enter Valid Password...!\n";
    textPassword.style.border = "1px solid red";
    textRPassword.style.border = "1px solid red";
  }
  if (user.userStatusId == null) {
    error = error + "Please Select Status...!\n";
    selectStatus.style.border = "1px solid red";
  }
  if (user.roles.length == 0) {
    error = error + "Please Select Role...!\n";
  }

  return error;
};

//function for check updates
const checkUpdates = () => {
  let updates = ""; //when using 'let' this object only usable is this functional area

  // use nullish coalescing operator for handle null and undefined values
  if (oldUserObj.firstName != user.firstName) {
    updates +=
      "First Name has changed " +
      oldUserObj.firstName +
      " into " +
      user.firstName +
      " \n";
  }
  if (oldUserObj.lastName != user.lastName) {
    updates +=
      "Last Name has changed " +
      (oldUserObj.lastName ?? "-") +
      " into " +
      (user.lastName ?? "-") +
      " \n";
  }
  if (oldUserObj.contact != user.contact) {
    updates +=
      "Contact has changed " +
      oldUserObj.contact +
      " into " +
      user.contact +
      " \n";
  }
  if (oldUserObj.nic != user.nic) {
    updates +=
      "NIC has changed " + oldUserObj.nic + " into " + user.nic + " \n";
  }
  if (oldUserObj.gender != user.gender) {
    updates +=
      "Gender has changed " +
      oldUserObj.gender +
      " into " +
      user.gender +
      " \n";
  }
  if (oldUserObj.email != user.email) {
    updates +=
      "Email has changed " + oldUserObj.email + " into " + user.email + " \n";
  }
  if (oldUserObj.username != user.username) {
    updates +=
      "Username has changed " +
      oldUserObj.username +
      " into " +
      user.username +
      " \n";
  }
  if (oldUserObj.userStatusId.id != user.userStatusId.id) {
    updates +=
      "Status has changed " +
      oldUserObj.userStatusId.name +
      " into " +
      user.userStatusId.name +
      " \n";
  }

  if (isRolesChanged(oldUserObj, user)) {
    updates += "User Roles has changed " + " \n";
  }

  return updates;
};

//function for check role changed or not
const isRolesChanged = (oldUserObj, user) => {
  let rolesChanged = false;

  //check the array length
  if (oldUserObj.roles.length !== user.roles.length) {
    rolesChanged = true;
    console.log("T1 " + rolesChanged);
    return rolesChanged;
  } else {
    //sort the role names and iterate for find changes
    const oldRoleNames = oldUserObj.roles.map((role) => role.name).sort();
    const newRoleNames = user.roles.map((role) => role.name).sort();

    for (let i = 0; i < oldRoleNames.length; i++) {
      if (oldRoleNames[i] !== newRoleNames[i]) {
        rolesChanged = true;
        console.log("T2 " + rolesChanged);
        return rolesChanged;
      }
    }
  }
};

//function for add record
const addRecord = () => {
  //check form errors -
  let formErrors = checkErrors();
  if (formErrors == "") {
    //get user confirmation
    let userConfirm = window.confirm(
      "Are you sure to add following record..?\n" +
        "\nFirst Name : " +
        user.firstName +
        "\nUsername : " +
        user.username
    );

    if (userConfirm) {
      //pass data into back end
      let serverResponse = ajaxRequestBody("/user", "POST", user); // url,method,object

      //check back end response
      if (serverResponse == "OK") {
        alert("Save sucessfully..! " + serverResponse);
        //need to refresh table and form
        refreshAll();
      } else {
        alert("Save not sucessfully..! have some errors \n" + serverResponse);
      }
    }
  } else {
    alert("Error\n" + formErrors);
  }
};

//function for update record
const updateRecord = () => {
  let errors = checkErrors();
  if (errors == "") {
    let updates = checkUpdates();
    if (updates != "") {
      let userConfirm = confirm(
        "Are you sure you want to update following changes...?\n" + updates
      );
      if (userConfirm) {
        let updateServiceResponse = ajaxRequestBody("/user", "PUT", user);
        if (updateServiceResponse == "OK") {
          alert("Update sucessfully..! ");
          //need to refresh table and form
          refreshAll();
        } else {
          alert(
            "Update not sucessfully..! have some errors \n" +
              updateSeriveResponse
          );
        }
      }
    } else {
      alert("Nothing to Update...!");
    }
  } else {
    alert("Cannot update!!!\nForm has following errors \n" + errors);
  }
};

//function for create select Roles UI part
const createViewRolesUI = () => {
  divRoles.innerHTML = "";
  roles.forEach((role) => {
    const div = document.createElement("div");
    div.className = "form-check form-check-inline";
    const inputCHK = document.createElement("input");
    inputCHK.type = "checkbox";
    inputCHK.className = "form-check-input";
    inputCHK.id = "chk" + role.name;

    inputCHK.onchange = function () {
      if (this.checked) {
        //check this selected role already exist or not
        let roleExists = user.roles.find((item) => item.name === roles);
        if (!roleExists) {
          //if not exist add new role
          user.roles.push(role);
        }
      } else {
        //find the current role name in role list, if it exist then remove from role list
        user.roles = user.roles.filter((item) => item.name !== role.name);
      }
    };

    const label = document.createElement("label");
    label.className = "form-check-label fw-bold";
    label.for = inputCHK.id;
    label.innerText = role.name;

    div.appendChild(inputCHK);
    div.appendChild(label);

    divRoles.appendChild(div);
  });
};

//function for get selected roles
const setSelectedRoles = () => {
  user.roles.map((role) => {
    const inputCHK = document.getElementById("chk" + role.name);
    inputCHK.checked = true;
  });
};

// ********* PRINT OPERATIONS *********

//print function
const printViewRecord = () => {
  newTab = window.open();
  newTab.document.write(
    //  link bootstrap css
    "<head><title>User Details</title>" +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
      "<h2>User Details</h2>" +
      printTable.outerHTML
  );

  //triger print() after 1000 milsec time out
  setTimeout(function () {
    newTab.print();
  }, 1000);
};

//print all data table after 1000 milsec of new tab opening () - to refresh the new tab elements
const printFullTable = () => {
  const newTab = window.open();
  newTab.document.write(
    //  link bootstrap css
    "<head><title>Print Employee</title>" +
      '<script src="resources/js/jquery.js"></script>' +
      '<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" /></head>' +
      "<h2>Employee Details</h2>" +
      tableId.outerHTML +
      '<script>$(".modify-button").css("display","none")</script>'
  );

  setTimeout(function () {
    newTab.print();
  }, 1000);
};
