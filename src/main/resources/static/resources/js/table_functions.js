// funtion for fill data into table
const fillDataIntoTable = (
  tableID,
  dataArray,
  displayProperty,
  viewButtonFunction,
  editButtonFunction,
  deleteButtonFunction,
  buttonVisibility = true,
  privilegeObj = null
) => {
  //generate table body
  const tableBody = tableID.children[1];
  tableBody.innerHTML = "";
  dataArray.forEach((item, ind) => {
    const tr = document.createElement("tr");

    const tdIndex = document.createElement("td");
    tdIndex.innerText = parseInt(ind) + 1;
    tr.appendChild(tdIndex);

    for (const itemOb of displayProperty) {
      const td = document.createElement("td");
      if (itemOb.datatype == "String") {
        if (dataArray[ind][itemOb.property] != null) {
          td.innerText = dataArray[ind][itemOb.property];
        } else {
          td.innerText = "-";
        }
      } else if (itemOb.datatype == "currency") {
        if (dataArray[ind][itemOb.property] == null) {
          td.innerText = "-";
        } else {
          td.innerHTML =
            "<b>Rs. </b>" +
            parseFloat(dataArray[ind][itemOb.property]).toFixed(2);
        }
      } else if (itemOb.datatype == "photoarray") {
        let img = document.createElement("img");
        img.style.width = "50px";
        img.style.height = "50px";

        if (dataArray[ind][itemOb.property] == null) {
          img.src = "resources/images/default-user-img.jpg";
        } else {
          img.src = atob(dataArray[ind][itemOb.property]);
        }

        td.appendChild(img);
      } else if (itemOb.datatype == "function") {
        td.innerHTML = itemOb.property(dataArray[ind]);
      }
      tr.appendChild(td);
    }

    const tdButton = document.createElement("td");
    tdButton.className = "table-buttons";
    tdButton.style.textAlign = "right";

    const buttonView = document.createElement("button");
    buttonView.type = "button";
    buttonView.id = "btnPrint";
    buttonView.className = "btn btn-outline-success btn-disable me-2";
    buttonView.innerHTML = '<i class="fa-solid fa-eye"></i>';

    buttonView.onclick = () => {
      console.log("Print Event" + item.id);
      viewButtonFunction(item, ind);
    };

    const buttonEdit = document.createElement("button");
    buttonEdit.id = "btnEdit";
    buttonEdit.type = "button";
    buttonEdit.className = "btn btn-outline-warning btn-disable me-2";
    buttonEdit.innerHTML = '<i class="fa-solid fa-edit"></i>';

    buttonEdit.onclick = () => {
      console.log("Edit Event" + item.id);
      editButtonFunction(item, ind);
    };

    const buttonDelete = document.createElement("button");
    buttonDelete.type = "button";
    buttonDelete.id = "btnDelete";
    buttonDelete.className = "btn btn-outline-danger btn-disable";
    buttonDelete.innerHTML = '<i class="fa-solid fa-trash"></i>';

    buttonDelete.onclick = () => {
      console.log("Delete Event" + item.id);
      deleteButtonFunction(item, ind);
    };

    if (buttonVisibility) {
      if (privilegeObj != null && privilegeObj.select) {
        tdButton.appendChild(buttonView);
      }

      if (privilegeObj != null && privilegeObj.update && privilegeObj.insert) {
        tdButton.appendChild(buttonEdit);
      }

      if (privilegeObj != null && privilegeObj.delete) {
        tdButton.appendChild(buttonDelete);
      }

      tr.appendChild(tdButton);
    }

    tableBody.appendChild(tr);
  });

  //set table button hover effect
  addTableButtonsHover();
};

//function for add effects to table buttons
const addTableButtonsHover = () => {
  //select all rows in table body
  const rows = document.querySelectorAll("tbody tr");

  //loop the rows and add event listeners to buttons on seleceted row
  //loop through each row
  for (let i = 0; i < rows.length; i++) {
    //add event listeners for each row
    rows[i].addEventListener("mouseenter", () => {
      //if selected row has buttons then select them
      const buttons = rows[i].querySelectorAll(".table-buttons button");
      //loop the buttons and remove the btn-disable class
      buttons.forEach((button) => {
        button.classList.remove("btn-disable");
      });
    });

    rows[i].addEventListener("mouseleave", () => {
      const buttons = rows[i].querySelectorAll(".table-buttons button");
      //loop the buttons and add the btn-disable class
      buttons.forEach((button) => {
        button.classList.add("btn-disable");
      });
    });
  }
};

// funtion for fill data into inner-table
const fillDataIntoInnerTable = (
  tableID,
  dataArray,
  displayProperty,
  editButtonFunction,
  deleteButtonFunction,
  buttonVisibility = true
) => {
  //generate table body
  const tableBody = tableID.children[1];
  tableBody.innerHTML = "";
  dataArray.forEach((item, ind) => {
    const tr = document.createElement("tr");

    const tdIndex = document.createElement("td");
    tdIndex.innerText = parseInt(ind) + 1;
    tr.appendChild(tdIndex);

    for (const itemOb of displayProperty) {
      const td = document.createElement("td");
      if (itemOb.datatype == "String") {
        if (dataArray[ind][itemOb.property] != null) {
          td.innerText = dataArray[ind][itemOb.property];
        } else {
          td.innerText = "-";
        }
      } else if (itemOb.datatype == "currency") {
        if (dataArray[ind][itemOb.property] == null) {
          td.innerText = "-";
        } else {
          td.innerHTML =
            "<b>Rs. </b>" +
            parseFloat(dataArray[ind][itemOb.property]).toFixed(2);
        }
      } else if (itemOb.datatype == "function") {
        td.innerHTML = itemOb.property(dataArray[ind]);
      }
      tr.appendChild(td);
    }

    const tdButton = document.createElement("td");
    tdButton.className = "table-buttons";
    tdButton.style.textAlign = "right";

    const buttonEdit = document.createElement("button");
    buttonEdit.id = "btnEdit";
    buttonEdit.type = "button";
    buttonEdit.className = "btn btn-outline-warning btn-disable btn-sm me-2";
    buttonEdit.innerHTML = '<i class="fa-solid fa-edit"></i>';

    buttonEdit.onclick = () => {
      console.log("Edit Event" + item.id);
      editButtonFunction(item, ind);
    };

    const buttonDelete = document.createElement("button");
    buttonDelete.type = "button";
    buttonDelete.id = "btnDelete";
    buttonDelete.className = "btn btn-outline-danger btn-disable btn-sm";
    buttonDelete.innerHTML = '<i class="fa-solid fa-trash"></i>';

    buttonDelete.onclick = () => {
      console.log("Delete Event" + item.id);
      deleteButtonFunction(item, ind);
    };

    if (buttonVisibility) {
      tdButton.appendChild(buttonEdit);

      tdButton.appendChild(buttonDelete);

      tr.appendChild(tdButton);
    }

    tableBody.appendChild(tr);
  });

  //set table button hover effect
  addTableButtonsHover();
};
