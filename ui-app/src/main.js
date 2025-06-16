import './style.css'
import 'pretty-print-json/css/pretty-print-json.css';
import { prettyPrintJson } from 'pretty-print-json';

const connectButton = document.getElementById('connect');
const eventElm = document.getElementById('events')
const dropdown = document.getElementById('topics');
let eventSource;

connectButton.disabled = true;

async function fetchTopics() {
    try {
        const response = await fetch('http://localhost:8084/topics');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        populateDropdown(data);
    } catch (error) {
        console.error('Fetch error:', error);
    }
}

function populateDropdown(data) {

    dropdown.innerHTML = ''; // Clear previous options

    data.forEach(item => {
        const option = document.createElement('option');
        option.value = item.value;
        option.textContent = item.label;
        dropdown.appendChild(option);
        connectButton.disabled = false;
    });
}

fetchTopics();

connectButton.addEventListener('click', () => {
    removeChildren();
    if(eventSource){
        eventSource.close();
    }

    // Initialize the EventSource, listening for server updates.
    eventSource = new EventSource('http://localhost:8084/stream/'+ dropdown.value);

    // Listen for messages from the server.
    eventSource.onmessage = function (event) {
        console.log(event.data);
        const newElement = document.createElement("pre");
        newElement.setAttribute('class', 'json-container pre-scrollable bg-light p-3 rounded');
        newElement.innerHTML = prettyPrintJson.toHtml(JSON.parse(event.data));
        eventElm.appendChild(newElement);
    };

    // Log connection error.
    eventSource.onerror = function (error) {
        console.error("SSE error:", error);
        eventSource.close();
    };
});

function removeChildren() {
    let e = document.querySelector("#events");

    //e.firstElementChild can be used.
    let child = e.lastElementChild;
    while (child) {
        e.removeChild(child);
        child = e.lastElementChild;
    }
}


