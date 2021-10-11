function createNewUrl () {
    let targetUrl = document.getElementById("urlInput").value;
    
    // Add protocol if not found
    if ((targetUrl.indexOf("http://") < 0) && (targetUrl.indexOf("https://") < 0)) {
        targetUrl = "http://" + targetUrl;
    }
    
    if (targetUrl != null && targetUrl.length > 0 && isValidHttpUrl(targetUrl)) {
        const myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");

        const raw = JSON.stringify({
            "targetUrl": targetUrl
        });

        const requestOptions = {
            method: 'POST',
            headers: myHeaders,
            body: raw,
            redirect: 'follow'
        };

        fetch("http://localhost:8080/api/createUrl", requestOptions)
            .then(response => response.json())
            .then(result => onSuccess(result))
            .catch(error => onError(error));
        
        function onSuccess(result) {
            document.getElementById("urlTitleLabel").innerHTML = "<a>Title of the URL is: </a>";
            document.getElementById("urlTitle").innerHTML = "<a>" + result.urlTitle + "</a>";
            document.getElementById("targetUrlLabel").innerHTML = "<a>Your Target URL is: </a>";
            document.getElementById("targetUrl").innerHTML = "<a href='" + result.targetUrl + "' target='_blank'>" + result.targetUrl + "</a>";
            document.getElementById("shortenUrlLabel").innerHTML = "<a>Your Short URL is: </a>";
            document.getElementById("shortenUrl").innerHTML = "<a href='" + result.shortUrl + "' target='_blank'>" + result.shortUrl + "</a>";
            document.getElementById("shortenCodeLabel").innerHTML = "<a>Your Short Code is: </a>";
            document.getElementById("shortenCode").innerHTML = "<a>" + result.urlCode + "</a>";
        }
        
        function onError(error) {
            console.log(error);
        }
    } else {
        alert('URL entered is empty or incorrect. Please make sure it contains HTTP or HTTPs Protocols.');
    }

    function isValidHttpUrl(string) {
        let url;

        try {
            url = new URL(string);
        } catch (_) {
            return false;
        }

        return url.protocol === "http:" || url.protocol === "https:";
    }
}

function generateUsageReport() {
    var shortCode = document.getElementById("shortCodeInput").value;
    if (shortCode != null && shortCode.length > 0) {
        if (shortCode.indexOf("http://localhost:8080/r/") >= 0) {
            shortCode = shortCode.substring("http://localhost:8080/r/".length, shortCode.length);
        }
        var requestOptions = {
            method: 'GET',
            redirect: 'follow'
        };

        fetch("http://localhost:8080/report/short-code/?shortCode=" + shortCode, requestOptions)
            .then(response => response.json())
            .then(result => onSuccess(result))
            .catch(error => onError(error));

        function onSuccess(result) {
            if (result.numberOfClicks > 0) {
                generateReportTable(result);
            } else {
                document.getElementById('reportTable').innerHTML = "No access history found for the entered Short Code.";
            }
            
        }

        function onError(error) {
            console.log(error);
        }
        
        function generateReportTable(result) {
            let table = document.createElement('table');
            let tbody = document.createElement('tbody');
            
            table.appendChild(tbody);
            document.getElementById('reportTable').innerHTML = "";
            // Adding the entire table to the reportTable div
            document.getElementById('reportTable').appendChild(table);
            let shortCode = document.getElementById("shortCodeInput").value;
            if (shortCode.indexOf("http://localhost:8080/r/") >= 0) {
                shortCode = shortCode.substring("http://localhost:8080/r/".length, shortCode.length);
            }
            table.appendChild(generateHeader("Usage Report for: " + shortCode));
            table.appendChild(generateRow("Number of Clicks:", result.numberOfClicks,));
            table.appendChild(generateRow("Redirect to URL:", result.targetUrl,));
            
            table.appendChild(generateHeader("Access History"));
            for (let i = 0; result.urlEvents && i < result.urlEvents.length; i++) {
                table.appendChild(generateHistoryRow((i+1) + ".","Accessed IP: ", result.urlEvents[i].originIp));
                table.appendChild(generateHistoryRow("","Origin Location: ", result.urlEvents[i].originGeolocation));
                table.appendChild(generateHistoryRow("","Accessed Timestamp: ", result.urlEvents[i].timestamp));
            }
        }

        function generateHeader(headerValue) {
            // Insert Header into Tables
            let row = document.createElement('tr');
            let data = document.createElement('td');
            data.innerHTML = headerValue;
            data.colSpan = 4;
            row.appendChild(data);
            return row;
        }

        function generateRow(colValue, dataValue) {
            // Insert Header into Tables
            let row = document.createElement('tr');
            let col = document.createElement('td');
            col.innerHTML = colValue;


            let data = document.createElement('td');
            data.innerHTML = dataValue;
            data.colSpan = 3;
            row.appendChild(col);
            row.appendChild(data);
            return row;
        }

        function generateHistoryRow(counter, colValue, dataValue) {
            // Insert Header into Tables
            let row = document.createElement('tr');
            let index = document.createElement('td');
            index.innerHTML = counter

            let col = document.createElement('td');
            col.innerHTML = colValue;

            let data = document.createElement('td');
            data.innerHTML = dataValue;
            data.colSpan = 2;
            row.appendChild(index);
            row.appendChild(col);
            row.appendChild(data);
            return row;
        }
    } else {
        alert('Short Code cannot be empty.');
    }
}
