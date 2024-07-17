// Function to show the custom alert box and return a promise
const showAlert = (msgType, message) => {
  return new Promise((resolve) => {
    // Set alert title and icon
    if (msgType === "success") {
      alertTitle.innerText = "Success!";
      alertTitle.style.color = "#32ba7c";
      alertImage.src = "resources/icons/success.png";
      alertButton.className = "";
      alertButton.classList.add(
        "btn",
        "btn-outline-success",
        "btn-success-alert"
      );
    } else if (msgType === "warning") {
      alertTitle.innerText = "Warning!";
      alertTitle.style.color = "#ffc107";
      alertImage.src = "resources/icons/warning.png";
      alertButton.className = "";
      alertButton.classList.add("btn", "btn-outline-warning");
    } else if (msgType === "error") {
      alertTitle.innerText = "Error!";
      alertTitle.style.color = "#f15249";
      alertImage.src = "resources/icons/error.png";
      alertButton.className = "";
      alertButton.classList.add("btn", "btn-outline-danger");
    }

    // Replace \n with <br> tags
    alertMessage.innerHTML = message.replace(/\n/g, "<br>");

    customAlertBox.classList.remove("hidden");

    var overlay = document.createElement("div");
    overlay.classList.add("modal-overlay");
    document.body.appendChild(overlay);

    // Set up the event listener for the OK button to resolve the promise
    alertButton.onclick = function () {
      hide(customAlertBox);
      resolve();
    };
  });
};

// Function to show the custom confirm box and return a promise
const showConfirm = (title, message) => {
  return new Promise((resolve) => {
    // Set message
    confirmTitle.innerHTML = `<b>${title.replace(/\n/g, "<br>")}</b>`;
    confirmMessage.innerHTML = message.replace(/\n/g, "<br>");

    customConfirmBox.classList.remove("hidden");

    // add overlay
    var overlay = document.createElement("div");
    overlay.classList.add("modal-overlay");
    document.body.appendChild(overlay);

    // event listeners for Yes and No buttons
    document.querySelector(".confirm-yes").onclick = function () {
      hide(customConfirmBox);
      resolve(true);
    };
    document.querySelector(".confirm-no").onclick = function () {
      hide(customConfirmBox);
      resolve(false);
    };
  });
};

// funtion for hide alert box/ confirmation box
const hide = (box) => {
  box.classList.add("hidden");
  var overlay = document.querySelector(".modal-overlay");
  if (overlay) {
    overlay.parentNode.removeChild(overlay);
  }
};
