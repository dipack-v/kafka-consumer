import './style.css'
import JSONFormatter from "json-formatter-js";


// Initialize the EventSource, listening for server updates.
const eventSource = new EventSource('http://localhost:8084/stream');

// Listen for messages from the server.
eventSource.onmessage = function (event) {
    console.log(event.data);
    const formatter = new JSONFormatter(JSON.parse(event.data));
    document.querySelector("#events").appendChild(formatter.render());
};

// Log connection error.
eventSource.onerror = function (error) {
    console.error("SSE error:", error);
    eventSource.close();
};
