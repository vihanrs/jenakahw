// funtion for fill data into table
const fillDataIntoTable = (
  tableID,
  dataArray,
  displayProperty,
  viewButtonFunction,
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
      } else if (itemOb.datatype == "function") {
        td.innerHTML = itemOb.property(dataArray[ind]);
      }
      tr.appendChild(td);
    }

    const tdButton = document.createElement("td");
    tdButton.className = "table-buttons";
    tdButton.style.textAlign = "right";

    const buttonPrint = document.createElement("button");
    buttonPrint.type = "button";
    buttonPrint.className = "btn btn-outline-success btn-disable";
    buttonPrint.innerHTML = '<i class="fa-solid fa-eye"></i>';

    buttonPrint.onclick = () => {
      console.log("Print Event" + item.id);
      viewButtonFunction(item, ind);
    };

    const buttonEdit = document.createElement("button");
    buttonEdit.type = "button";
    buttonEdit.className = "btn btn-outline-warning btn-disable ms-2 me-2";
    buttonEdit.innerHTML = '<i class="fa-solid fa-edit"></i>';

    buttonEdit.onclick = () => {
      console.log("Edit Event" + item.id);
      editButtonFunction(item, ind);
    };

    const buttonDelete = document.createElement("button");
    buttonDelete.type = "button";
    buttonDelete.className = "btn btn-outline-danger btn-disable";
    buttonDelete.innerHTML = '<i class="fa-solid fa-trash"></i>';

    buttonDelete.onclick = () => {
      console.log("Delete Event" + item.id);
      deleteButtonFunction(item, ind);
    };

    if (buttonVisibility) {
      tdButton.appendChild(buttonPrint);
      tdButton.appendChild(buttonEdit);
      tdButton.appendChild(buttonDelete);
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
