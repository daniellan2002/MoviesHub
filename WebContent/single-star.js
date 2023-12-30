/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#star_info");

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>Star Name: " + resultData[0]["star_name"] + "</p>" +
        "<p>Date Of Birth: " + resultData[0]["star_dob"] + "</p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>";
        rowHTML +=
            '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">'
            + resultData[i]["movie_title"] +     // display movie_title for the link text
            '</a>'
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

function logout() {
    $.ajax({
        url: "api/logout",
        type: "GET",
        success: function() {
            window.location.href = 'login.html';
        },
        error: function(xhr, status, error) {
            console.error("Error: " + error);
        }
    });
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

function goHome()
{

    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "POST",// Setting request method
        url: "api/single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) =>
        {
            buildURL(resultData);
            console.log(resultData)
        } // Setting callback function to handle data returned successfully by the SingleStarServlet
    });

}
function buildURL(resultData)
{
    console.log("Building the url");
    console.log(resultData);
    console.log(resultData[0]["title"]);
    let title = resultData[0]["title"];
    let year = resultData[0]["year"];
    let director = resultData[0]["director"];
    let genre = resultData[0]["genre"];
    let star = resultData[0]["star"];
    let sortBy = resultData[0]["sortBy"];
    let sortOrder = resultData[0]["sortOrder"];
    let page = resultData[0]["page"];
    let moviePerPage = resultData[0]["moviePerPage"];

    window.location.href="movielist.html?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star
        + "&genre=" + genre + "&sortBy=" + sortBy + "&sortOrder=" + sortOrder + "&page=" + page + "&moviePerPage=" + moviePerPage;

}