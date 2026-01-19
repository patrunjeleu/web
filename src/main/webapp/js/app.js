// Navigation between sections
function showSection(sectionId) {
    const sections = document.querySelectorAll('.section');
    const buttons = document.querySelectorAll('.nav-btn');

    sections.forEach(section => {
        section.classList.remove('active');
    });

    buttons.forEach(button => {
        button.classList.remove('active');
    });

    document.getElementById(sectionId).classList.add('active');
    event.target.classList.add('active');
}

// Tab switching
function showTab(tabName) {
    const tabs = document.querySelectorAll('.tab-content');
    const buttons = document.querySelectorAll('.tab-btn');

    tabs.forEach(tab => {
        tab.classList.remove('active');
    });

    buttons.forEach(button => {
        button.classList.remove('active');
    });

    document.getElementById(tabName + 'Tab').classList.add('active');
    event.target.classList.add('active');
}

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    loadActivities();
    loadDestinationNames();

    // Form submissions
    document.getElementById('addDestinationForm').addEventListener('submit', addDestination);
    document.getElementById('addUserForm').addEventListener('submit', addUser);
    document.getElementById('uploadRDFForm').addEventListener('submit', uploadRDF);
    document.getElementById('addRDFDestForm').addEventListener('submit', addRDFDestination);
    document.getElementById('modifyRDFForm').addEventListener('submit', modifyRDFDestination);
});

// Load activities for dropdown
async function loadActivities() {
    try {
        const response = await fetch('/vacation-recommender/api/xml/activities');
        const activities = await response.json();

        const select = document.getElementById('activitySelect');
        activities.forEach(activity => {
            const option = document.createElement('option');
            option.value = activity;
            option.textContent = activity;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Error loading activities:', error);
    }
}

// Load destination names for dropdown
async function loadDestinationNames() {
    try {
        const response = await fetch('/vacation-recommender/api/xml/destinations');
        const destinations = await response.json();

        const select = document.getElementById('destNameSelect');
        destinations.forEach(dest => {
            const option = document.createElement('option');
            option.value = dest.name;
            option.textContent = dest.name;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Error loading destinations:', error);
    }
}

// PART 1: XML Operations

// Task 4: Add Destination
async function addDestination(e) {
    e.preventDefault();

    const formData = new FormData();
    formData.append('name', document.getElementById('destName').value);
    formData.append('description', document.getElementById('destDescription').value);
    formData.append('duration', document.getElementById('destDuration').value);
    formData.append('activity1', document.getElementById('destActivity1').value);
    formData.append('activity2', document.getElementById('destActivity2').value);
    formData.append('budget', document.getElementById('destBudget').value);

    try {
        const response = await fetch('/vacation-recommender/api/xml/addDestination', {
            method: 'POST',
            body: formData
        });

        const result = await response.json();
        const resultDiv = document.getElementById('addDestResult');

        if (result.success) {
            resultDiv.className = 'result success';
            resultDiv.textContent = '✓ Destination ajoutée avec succès!';
            e.target.reset();
            loadDestinationNames(); // Reload dropdown
        } else {
            resultDiv.className = 'result error';
            resultDiv.textContent = '✗ Erreur: ' + (result.error || 'Échec de l\'ajout');
        }
    } catch (error) {
        const resultDiv = document.getElementById('addDestResult');
        resultDiv.className = 'result error';
        resultDiv.textContent = '✗ Erreur: ' + error.message;
    }
}

// Task 5: Add User
async function addUser(e) {
    e.preventDefault();

    const formData = new FormData();
    formData.append('firstName', document.getElementById('userFirstName').value);
    formData.append('lastName', document.getElementById('userLastName').value);
    formData.append('availability', document.getElementById('userAvailability').value);
    formData.append('preferredActivity', document.getElementById('userActivity').value);
    formData.append('budget', document.getElementById('userBudget').value);

    try {
        const response = await fetch('/vacation-recommender/api/xml/addUser', {
            method: 'POST',
            body: formData
        });

        const result = await response.json();
        const resultDiv = document.getElementById('addUserResult');

        if (result.success) {
            resultDiv.className = 'result success';
            resultDiv.textContent = '✓ Utilisateur ajouté avec succès!';
            e.target.reset();
        } else {
            resultDiv.className = 'result error';
            resultDiv.textContent = '✗ Erreur: ' + (result.error || 'Échec de l\'ajout');
        }
    } catch (error) {
        const resultDiv = document.getElementById('addUserResult');
        resultDiv.className = 'result error';
        resultDiv.textContent = '✗ Erreur: ' + error.message;
    }
}

// Task 6: Filter by Budget
async function filterByBudget() {
    const budget = document.getElementById('budgetFilter').value;

    try {
        const response = await fetch(`/vacation-recommender/api/xml/byBudget?budget=${budget}`);
        const destinations = await response.json();

        displayDestinations(destinations, 'budgetResults');
    } catch (error) {
        console.error('Error filtering by budget:', error);
    }
}

// Task 7: Get Recommendations
async function getRecommendations() {
    try {
        const response = await fetch('/vacation-recommender/api/xml/recommended');
        const result = await response.json();

        const container = document.getElementById('recommendedResults');
        container.innerHTML = '';

        if (result.user) {
            const userInfo = document.createElement('div');
            userInfo.className = 'user-info';
            userInfo.innerHTML = `
                <strong>Utilisateur:</strong> ${result.user.firstName} ${result.user.lastName}<br>
                <strong>Budget:</strong> ${result.user.budget}€ |
                <strong>Disponibilité:</strong> ${result.user.availability} jours |
                <strong>Activité préférée:</strong> ${result.user.preferredActivity}
            `;
            container.appendChild(userInfo);
        }

        if (result.destinations && result.destinations.length > 0) {
            displayDestinations(result.destinations, 'recommendedResults', false);
        } else {
            container.innerHTML += '<p>Aucune destination recommandée trouvée.</p>';
        }
    } catch (error) {
        console.error('Error getting recommendations:', error);
    }
}

// Task 8: XSLT Transformation
async function showXSLTTransform() {
    try {
        const response = await fetch('/vacation-recommender/api/xml/transform');
        const html = await response.text();

        document.getElementById('xsltResult').innerHTML = html;
    } catch (error) {
        console.error('Error with XSLT transformation:', error);
    }
}

// Task 9: Get Destination Details
async function getDestinationDetails() {
    const name = document.getElementById('destNameSelect').value;

    if (!name) {
        alert('Veuillez sélectionner une destination');
        return;
    }

    try {
        const response = await fetch(`/vacation-recommender/api/xml/byName?name=${encodeURIComponent(name)}`);
        const destination = await response.json();

        const container = document.getElementById('destDetailsResult');
        container.innerHTML = `
            <div class="destination-card">
                <h4>${destination.name}</h4>
                <p><strong>Description:</strong> ${destination.description}</p>
                <p><strong>Durée:</strong> ${destination.duration} jours</p>
                <p><strong>Activités:</strong> ${destination.activity1}, ${destination.activity2}</p>
                <p class="price">Budget: ${destination.budget}€</p>
            </div>
        `;
    } catch (error) {
        console.error('Error getting destination details:', error);
    }
}

// Task 10: Filter by Activity
async function filterByActivity() {
    const activity = document.getElementById('activitySelect').value;

    if (!activity) {
        alert('Veuillez sélectionner une activité');
        return;
    }

    try {
        const response = await fetch(`/vacation-recommender/api/xml/byActivity?activity=${encodeURIComponent(activity)}`);
        const destinations = await response.json();

        displayDestinations(destinations, 'activityResults');
    } catch (error) {
        console.error('Error filtering by activity:', error);
    }
}

// Helper function to display destinations
function displayDestinations(destinations, containerId, clearFirst = true) {
    const container = document.getElementById(containerId);

    if (clearFirst) {
        container.innerHTML = '';
    }

    if (destinations.length === 0) {
        container.innerHTML += '<p>Aucune destination trouvée.</p>';
        return;
    }

    destinations.forEach(dest => {
        const card = document.createElement('div');
        card.className = 'destination-card';
        card.innerHTML = `
            <h4>${dest.name}</h4>
            <p>${dest.description}</p>
            <p><strong>Durée:</strong> ${dest.duration} jours</p>
            <p><strong>Activités:</strong> ${dest.activity1}, ${dest.activity2}</p>
            <p class="price">${dest.budget}€</p>
        `;
        container.appendChild(card);
    });
}

// PART 2: RDF Operations

// Task 2: Upload RDF File
async function uploadRDF(e) {
    e.preventDefault();

    const fileInput = document.getElementById('rdfFile');
    const file = fileInput.files[0];

    if (!file) {
        alert('Veuillez sélectionner un fichier');
        return;
    }

    const formData = new FormData();
    formData.append('rdfFile', file);

    try {
        const response = await fetch('/vacation-recommender/api/rdf/upload', {
            method: 'POST',
            body: formData
        });

        const result = await response.json();
        const resultDiv = document.getElementById('uploadResult');

        if (result.success) {
            resultDiv.className = 'result success';
            resultDiv.textContent = '✓ Fichier RDF téléchargé avec succès!';

            // Load and visualize the graph
            visualizeRDFGraph();
        } else {
            resultDiv.className = 'result error';
            resultDiv.textContent = '✗ Erreur: ' + (result.error || 'Échec du téléchargement');
        }
    } catch (error) {
        const resultDiv = document.getElementById('uploadResult');
        resultDiv.className = 'result error';
        resultDiv.textContent = '✗ Erreur: ' + error.message;
    }
}

// Visualize RDF Graph
async function visualizeRDFGraph() {
    try {
        const response = await fetch('/vacation-recommender/api/rdf/graph');
        const graphData = await response.json();

        drawGraph(graphData, 'rdfGraph');
    } catch (error) {
        console.error('Error visualizing graph:', error);
    }
}

// Draw graph using D3.js
function drawGraph(graphData, svgId, highlightNodes = null) {
    const svg = d3.select(`#${svgId}`);
    svg.selectAll("*").remove();

    const width = +svg.attr("width");
    const height = +svg.attr("height");

    const simulation = d3.forceSimulation(graphData.nodes)
        .force("link", d3.forceLink(graphData.edges).id(d => d.id).distance(150))
        .force("charge", d3.forceManyBody().strength(-300))
        .force("center", d3.forceCenter(width / 2, height / 2));

    const link = svg.append("g")
        .selectAll("line")
        .data(graphData.edges)
        .enter().append("line")
        .attr("class", "link");

    const linkLabel = svg.append("g")
        .selectAll("text")
        .data(graphData.edges)
        .enter().append("text")
        .attr("class", "link-label")
        .text(d => d.label);

    const node = svg.append("g")
        .selectAll("g")
        .data(graphData.nodes)
        .enter().append("g")
        .attr("class", d => {
            let className = "node " + d.type;
            if (highlightNodes && highlightNodes.includes(d.id)) {
                className += " highlight";
            }
            return className;
        })
        .call(d3.drag()
            .on("start", dragstarted)
            .on("drag", dragged)
            .on("end", dragended));

    node.append("circle")
        .attr("r", 10);

    node.append("text")
        .attr("dx", 12)
        .attr("dy", ".35em")
        .text(d => d.label);

    simulation.on("tick", () => {
        link
            .attr("x1", d => d.source.x)
            .attr("y1", d => d.source.y)
            .attr("x2", d => d.target.x)
            .attr("y2", d => d.target.y);

        linkLabel
            .attr("x", d => (d.source.x + d.target.x) / 2)
            .attr("y", d => (d.source.y + d.target.y) / 2);

        node
            .attr("transform", d => `translate(${d.x},${d.y})`);
    });

    function dragstarted(event, d) {
        if (!event.active) simulation.alphaTarget(0.3).restart();
        d.fx = d.x;
        d.fy = d.y;
    }

    function dragged(event, d) {
        d.fx = event.x;
        d.fy = event.y;
    }

    function dragended(event, d) {
        if (!event.active) simulation.alphaTarget(0);
        d.fx = null;
        d.fy = null;
    }
}

// Task 3: Add RDF Destination
async function addRDFDestination(e) {
    e.preventDefault();

    const formData = new FormData();
    formData.append('name', document.getElementById('rdfDestName').value);
    formData.append('description', document.getElementById('rdfDestDescription').value);
    formData.append('budget', document.getElementById('rdfDestBudget').value);
    formData.append('duration', document.getElementById('rdfDestDuration').value);
    formData.append('activity1', document.getElementById('rdfActivity1').value);
    formData.append('activity2', document.getElementById('rdfActivity2').value);

    try {
        const response = await fetch('/vacation-recommender/api/rdf/addDestination', {
            method: 'POST',
            body: formData
        });

        const result = await response.json();
        const resultDiv = document.getElementById('addRDFResult');

        if (result.success) {
            resultDiv.className = 'result success';
            resultDiv.textContent = '✓ ' + result.message;
            e.target.reset();
        } else {
            resultDiv.className = 'result error';
            resultDiv.textContent = '✗ Erreur: ' + (result.error || 'Échec de l\'ajout');
        }
    } catch (error) {
        const resultDiv = document.getElementById('addRDFResult');
        resultDiv.className = 'result error';
        resultDiv.textContent = '✗ Erreur: ' + error.message;
    }
}

// Task 3: Modify RDF Destination
async function modifyRDFDestination(e) {
    e.preventDefault();

    const formData = new FormData();
    formData.append('uri', document.getElementById('modifyURI').value);
    formData.append('property', document.getElementById('modifyProperty').value);
    formData.append('value', document.getElementById('modifyValue').value);

    try {
        const response = await fetch('/vacation-recommender/api/rdf/updateDestination', {
            method: 'POST',
            body: formData
        });

        const result = await response.json();
        const resultDiv = document.getElementById('modifyRDFResult');

        if (result.success) {
            resultDiv.className = 'result success';
            resultDiv.textContent = '✓ ' + result.message;
            e.target.reset();
        } else {
            resultDiv.className = 'result error';
            resultDiv.textContent = '✗ Erreur: ' + (result.error || 'Échec de la modification');
        }
    } catch (error) {
        const resultDiv = document.getElementById('modifyRDFResult');
        resultDiv.className = 'result error';
        resultDiv.textContent = '✗ Erreur: ' + error.message;
    }
}

// Task 4: Load RDF Destinations
async function loadRDFDestinations() {
    try {
        const response = await fetch('/vacation-recommender/api/rdf/destinations');
        const destinations = await response.json();

        const container = document.getElementById('rdfDestinationsList');
        container.innerHTML = '';

        if (destinations.length === 0) {
            container.innerHTML = '<p>Aucune destination trouvée dans RDF.</p>';
            return;
        }

        destinations.forEach(dest => {
            const card = document.createElement('div');
            card.className = 'destination-card';
            card.innerHTML = `
                <h4>${dest.name}</h4>
                ${dest.description ? `<p>${dest.description}</p>` : ''}
                ${dest.duration ? `<p><strong>Durée:</strong> ${dest.duration} jours</p>` : ''}
                ${dest.budget ? `<p class="price">Budget: ${dest.budget}€</p>` : ''}
                <p style="font-size: 0.8rem; color: #999;">URI: ${dest.uri}</p>
            `;
            container.appendChild(card);
        });
    } catch (error) {
        console.error('Error loading RDF destinations:', error);
    }
}

// Task 5: Query Requirements
async function queryRequirements() {
    const name = document.getElementById('reqDestName').value;

    if (!name) {
        alert('Veuillez entrer un nom de destination');
        return;
    }

    try {
        // Get text-based requirements
        const response = await fetch(`/vacation-recommender/api/rdf/requirements?name=${encodeURIComponent(name)}`);
        const requirements = await response.json();

        const textDiv = document.getElementById('requirementsText');

        if (Object.keys(requirements).length === 0) {
            textDiv.innerHTML = '<p class="result error">Destination non trouvée dans RDF.</p>';
            return;
        }

        textDiv.innerHTML = `
            <div class="result success">
                <h4>Exigences pour ${name}:</h4>
                <p><strong>Budget:</strong> ${requirements.budget}€</p>
                <p><strong>Durée:</strong> ${requirements.duration} jours</p>
                <p><strong>Activités:</strong> ${requirements.activities ? requirements.activities.length : 0} activité(s)</p>
            </div>
        `;

        // Get graph with highlighted nodes
        const graphResponse = await fetch(`/vacation-recommender/api/rdf/graph/highlight?name=${encodeURIComponent(name)}`);
        const graphData = await graphResponse.json();

        drawGraph(graphData, 'highlightGraph', graphData.highlight);
    } catch (error) {
        console.error('Error querying requirements:', error);
    }
}
