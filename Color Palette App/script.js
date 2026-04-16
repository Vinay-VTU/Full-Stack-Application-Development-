const colorInput = document.getElementById("colorInput");
const displayText = document.getElementById("displayText");

function getColor() {
  const color = colorInput.value.trim();

  if (color === "") {
    alert("Please enter a color!");
    return null;
  }

  return color;
}

function changeBackground() {
  const color = getColor();
  if (!color) return;

  displayText.style.backgroundColor = color;
}

function changeText() {
  const color = getColor();
  if (!color) return;

  displayText.style.color = color;
}

function resetStyles() {
  displayText.style.backgroundColor = "";
  displayText.style.color = "";
  colorInput.value = "";
}