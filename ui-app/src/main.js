import './style.css'
import 'pretty-print-json/css/pretty-print-json.css';
import { prettyPrintJson } from 'pretty-print-json';


// Initialize the EventSource, listening for server updates.
const eventSource = new EventSource('http://localhost:8084/stream');

// Listen for messages from the server.
eventSource.onmessage = function (event) {
    console.log(event.data);
    const newElement = document.createElement("pre");
    newElement.setAttribute('class', 'json-container');
    newElement.innerHTML = prettyPrintJson.toHtml(JSON.parse(event.data));
    document.getElementById('events').appendChild(newElement);
};

// Log connection error.
eventSource.onerror = function (error) {
    console.error("SSE error:", error);
    eventSource.close();
};
