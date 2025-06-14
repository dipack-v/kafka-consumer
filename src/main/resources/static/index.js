// Initialize the EventSource, listening for server updates.
const eventSource = new EventSource('/stream');

// Listen for messages from the server.
eventSource.onmessage = function (event) {
    const newElement = document.createElement("li");
    newElement.textContent = event.data;
    document.getElementById("events").appendChild(newElement);
};

// Log connection error.
eventSource.onerror = function (error) {
    console.error("SSE error:", error);
    eventSource.close();
};