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
    let singleMovieInfoElement = jQuery("#singleMovie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    singleMovieInfoElement.append("<p>Movie Name: " + resultData[0]["movie_title"] + "</p>" +
        "<p>Rating: " + resultData[0]["movie_rating"] + "</p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let singleMovie_table_body = jQuery("#singleMovie_table_body");

    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<th>" + resultData[0]["movie_year"] + "</th>";
    rowHTML += "<th>" + resultData[0]["movie_director"] + "</th>";
    let genreArr = resultData[0]["movie_genres"].split(",");
    let genres = "";
    for (let j = 0; j < genreArr.length; j++) {
        genres += '<a href="movielist.html?title=&year=&director=&star=&genre=' + genreArr[j]
            + '">' + genreArr[j] + '</a>';
        genres += "\n";
    }
    rowHTML += "<th>" + genres + "</th>";


    let starsIDArray = resultData[0]["stars_id"].split(",");
    let starsNameArray = resultData[0]["stars_name"].split(",");
    // Add a separate table cell for each movie star
    for (let j = 0; j < starsIDArray.length; j++) {
        rowHTML += "<th>";
        rowHTML +=
            '<a href="single-star.html?id=' + starsIDArray[j] + '">'
            + starsNameArray[j] +     // display movie_title for the link text
            '</a>'
        rowHTML += "</th>";
    }
        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
    singleMovie_table_body.append(rowHTML);

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

let item = getParameterByName('id');
let movieId = getParameterByName('id');
function addtoCart() {
    $.ajax({
        url: "api/index",
        type: "POST",
        success: function() {
            $("#modal").html("<p>Item added to cart successfully!</p>");
            $("#modal").dialog({
                modal: true,
                buttons: {
                    OK: function() {
                        $(this).dialog("close");
                    }
                }
            });
        },
        data: {
            item: item
        },
        error: function(xhr, status, error) {
            $("#modal").html("<p>Failed to add item to cart: " + error + "</p>");
            $("#modal").dialog({
                modal: true,
                buttons: {
                    OK: function() {
                        $(this).dialog("close");
                    }
                }
            });
            console.error("Error: " + error);
        }
    });
}

function goHome()
{

    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "POST",// Setting request method
        url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
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

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */


// Get id from URL
// let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});