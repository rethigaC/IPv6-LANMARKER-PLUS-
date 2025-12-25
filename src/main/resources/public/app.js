// This script runs when the DOM is loaded
document.addEventListener("DOMContentLoaded", () => {
    
    // --- 1. Initialize the Map ---
    const map = L.map('map').setView([20, 0], 3); 
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    // Create a cluster group for map markers
    const markers = L.markerClusterGroup();
    map.addLayer(markers);

    // --- 2. Update Timestamp ---
    const timestampEl = document.getElementById('timestamp');
    function updateTime() {
        timestampEl.textContent = new Date().toLocaleString('en-US');
    }
    updateTime();
    setInterval(updateTime, 1000); // Update time every second

    // --- 3. Fetch and Populate Data ---
    
    // Helper function to create the list items
    function populateList(elementId, data) {
        const listEl = document.getElementById(elementId);
        listEl.innerHTML = ''; // Clear old data
        
        if (!data || data.length === 0) {
            listEl.innerHTML = '<li>No data available.</li>';
            return;
        }

        data.forEach(item => {
            const li = document.createElement('li');
            li.innerHTML = `<span>${item.name || 'Unknown'}</span> <span>${item.count.toLocaleString()}</span>`;
            listEl.appendChild(li);
        });
    }

    // Function to fetch all dashboard data
    async function updateDashboard() {
        try {
            // Fetch Top Countries
            const countryRes = await fetch('/api/stats/top-countries');
            const countryData = await countryRes.json();
            populateList('ipv6-top-countries', countryData);

            // Fetch Top AS
            const asRes = await fetch('/api/stats/top-as');
            const asData = await asRes.json();
            populateList('ipv6-top-as', asData);

            // Fetch map points (from our old API)
            const mapRes = await fetch('/api/landmarks');
            const mapData = await mapRes.json();
            
            // Clear old map points and add new ones
            markers.clearLayers();
            mapData.forEach(landmark => {
                const marker = L.marker([landmark.lat, landmark.lng]);
                marker.bindPopup(
                    `<b>IP:</b> ${landmark.ip}<br>` +
                    `<b>City:</b> ${landmark.city || 'Unknown'}<br>` +
                    `<b>Last Updated:</b> ${new Date(landmark.updated).toLocaleString()}`
                );
                markers.addLayer(marker);
            });

        } catch (error) {
            console.error("Error updating dashboard:", error);
        }
    }

    // --- 4. Run the Dashboard ---
    
    // Update immediately on load
    updateDashboard();
    
    // Set to auto-refresh every 30 seconds
    setInterval(updateDashboard, 30000); 
});