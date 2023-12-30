/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

// function handleSessionData(resultDataString) {
//     console.log(resultDataString);
//
//     let resultDataJson = JSON.parse(resultDataString);
//
//     console.log("handle session response");
//     console.log(resultDataJson);
//     console.log(resultDataJson["sessionID"]);
//
//     // show the session information
//     $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
//     $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);
//
//     handleCartArray(resultDataJson["previousItems"]);
// // }
// function handleStarResult(resultData, item_number) {
//     console.log("handleStarResult: populating star table from resultData");
//     console.log("movie per page: " + item_number);
//
//     let starTableBodyElement = jQuery("#star_table_body");
//
//     starTableBodyElement.empty();
//     // Iterate through resultData, no more than 10 entries
//
//     for (let i = 0; i < Math.min(item_number, resultData.length); i++) {
//     //for (let i = startIndex; i < endIndex && i < resultData.length; i++){
//         // Concatenate the html tags with resultData jsonObject
//         let rowHTML = "";
//         rowHTML += "<tr>";
//         //rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
//         rowHTML += "<th>";
//         rowHTML +=
//             '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">'
//             + resultData[i]["movie_title"] +     // display movie_title for the link text
//             '</a>'
//         rowHTML += "</th>";
//
//         rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
//         rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
//
//         let genreArr = resultData[i]["movie_genres"].split(",");
//         let genres = "";
//         for (let j = 0; j < Math.min(3, genreArr.length); j++) {
//             genres += '<a href="movielist.html?title=&year=&director=&star=&genre=' + genreArr[j]
//                 + '">' + genreArr[j] + '</a>';
//             genres += "\n";
//             //rowHTML += '<a href="movielist.html?title=&year=&director=&star=&genre=' + genreArr[j]
//                 //+ '">' + genreArr[j] + '</a>';
//         }
//         rowHTML += "<th>" + genres + "</th>";
//         rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
//
//         let starsIDArray = resultData[i]["stars_id"].split(",");
//         let starsNameArray = resultData[i]["stars_name"].split(",");
//         // Add a separate table cell for each movie star
//         for (let j = 0; j < Math.min(3, starsIDArray.length); j++) {
//             rowHTML += "<th>";
//             rowHTML +=
//                 '<a href="single-star.html?id=' + starsIDArray[j] + '">'
//                 + starsNameArray[j] +     // display movie_title for the link text
//                 '</a>'
//             rowHTML += "</th>";
//         }
//
//         rowHTML += "</tr>";
//
//         // Append the row created to the table body, which will refresh the page
//         starTableBodyElement.append(rowHTML);
//     }
// }

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
 * Once this .js is loaded, following scripts will be executed by the browser
 */

function nextPage()
{
    if(page != maxPage)
    {
        page += 1;
    }


    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movielist?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star
            + "&genre=" + genre + "&sortBy=" + sortBy + "&sortOrder=" + sortOrder + "&page=" + page + "&moviePerPage=" + moviePerPage,
        success: (resultData) => {
            returnMovieLength = resultData.length;
            if(returnMovieLength < moviePerPage)
            {
                maxPage = page;
            }
            makePage(resultData)
        },
        error: function(xhr, status, error) {
            console.error("calling nextPage failed: " + error);
        }
    });
}
//
// function giveMePage(resultData, startIndex, endIndex)
// {
//     console.log(resultData.length);
//     returnMovieLength = resultData.length;
//     if(endIndex > returnMovieLength)
//     {
//         console.log("this is the last page");
//     }
//     for (let i = startIndex; i < endIndex && i < resultData.length; i++){
//         console.log("get " + i);
//     }
//     console.log("entering pagination " + page);
//
//     let starTableBodyElement = jQuery("#star_table_body");
//
//     starTableBodyElement.empty();
//
//     for (let i = startIndex; i < endIndex && i < resultData.length; i++){
//         // Concatenate the html tags with resultData jsonObject
//         let rowHTML = "";
//         rowHTML += "<tr>";
//         //rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
//         rowHTML += "<th>";
//         rowHTML +=
//             '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">'
//             + resultData[i]["movie_title"] +     // display movie_title for the link text
//             '</a>'
//         rowHTML += "</th>";
//
//         rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
//         rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
//
//         let genreArr = resultData[i]["movie_genres"].split(",");
//         let genres = "";
//         for (let j = 0; j < Math.min(3, genreArr.length); j++) {
//             genres += '<a href="movielist.html?title=&year=&director=&star=&genre=' + genreArr[j]
//                 + '">' + genreArr[j] + '</a>';
//             genres += "\n";
//             //rowHTML += '<a href="movielist.html?title=&year=&director=&star=&genre=' + genreArr[j]
//             //+ '">' + genreArr[j] + '</a>';
//         }
//         rowHTML += "<th>" + genres + "</th>";
//         rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
//
//         let starsIDArray = resultData[i]["stars_id"].split(",");
//         let starsNameArray = resultData[i]["stars_name"].split(",");
//         // Add a separate table cell for each movie star
//         for (let j = 0; j < Math.min(3, starsIDArray.length); j++) {
//             rowHTML += "<th>";
//             rowHTML +=
//                 '<a href="single-star.html?id=' + starsIDArray[j] + '">'
//                 + starsNameArray[j] +     // display movie_title for the link text
//                 '</a>'
//             rowHTML += "</th>";
//         }
//
//         rowHTML += "</tr>";
//
//         // Append the row created to the table body, which will refresh the page
//         starTableBodyElement.append(rowHTML);
//     }
// }
function prevPage()
{
    page = page - 1;
    if(page === 0){
        page = 1;
    }
    const startIndex = (page - 1) * moviePerPage;
    const endIndex = startIndex + moviePerPage;
    console.log("I want to get item #" + startIndex + " to item# " + endIndex);
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movielist?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star
            + "&genre=" + genre + "&sortBy=" + sortBy + "&sortOrder=" + sortOrder + "&page=" + page + "&moviePerPage=" + moviePerPage,
        //success: (resultData) => giveMePage(resultData, startIndex, endIndex),
        success: (resultData) => makePage(resultData),
        error: function(xhr, status, error) {
            console.error("calling prevPage failed: " + error);
        }
    });
    console.log("what's the page number now?" + page)
}


let sortSelect = document.querySelector('#sort-select');
// let [sortBy, sortOrder] = sortSelect.value.split('-');
// let title = getParameterByName('title');
// let year = getParameterByName('year');
// let director = getParameterByName('director');
// let star = getParameterByName('star');
// let genre = getParameterByName('genre');
//const urlParams = new URLSearchParams(window.location.search);
let urlParams = new URLSearchParams(window.location.search);
let title = urlParams.get("title");
let year = urlParams.get("year");
let director = urlParams.get("director");
let star = urlParams.get("star");
let genre = urlParams.get("genre");
let sortBy = urlParams.get("sortBy");
let sortOrder = urlParams.get("sortOrder");
let page = parseInt(urlParams.get("page")) || 1;
let moviePerPage = parseInt(urlParams.get("moviePerPage")) || 10;
let returnMovieLength = 0;
let maxPage = 100000;
var cachedResult = null;
console.log(star);
// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movielist?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star
        + "&genre=" + genre + "&sortBy=" + sortBy + "&sortOrder=" + sortOrder + "&page=" + page + "&moviePerPage=" + moviePerPage,
    success: (resultData) => {
        returnMovieLength = resultData.length;
        if(returnMovieLength < moviePerPage)
        {
            maxPage = page;
        }
        makePage(resultData)
        // const startIndex = (page - 1) * moviePerPage;
        // const endIndex = startIndex + moviePerPage;
        //giveMePage(resultData, startIndex, endIndex);
    },
    error: function(xhr, status, error) {
        console.error("GET movielist failed: " + error);
    }
});
function makePage(resultData)
{
    console.log("makePage from resultData");
    let starTableBodyElement = jQuery("#star_table_body");

    starTableBodyElement.empty();
    // Iterate through resultData, no more than 10 entries

    for (let i = 0; i < Math.min(resultData.length); i++) {
        //for (let i = startIndex; i < endIndex && i < resultData.length; i++){
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        //rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
        rowHTML += "<th>";
        rowHTML +=
            '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">'
            + resultData[i]["movie_title"] +     // display movie_title for the link text
            '</a>'
        rowHTML += "</th>";

        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";

        let genreArr = resultData[i]["movie_genres"].split(",");
        let genres = "";
        for (let j = 0; j < Math.min(3, genreArr.length); j++) {
            genres += '<a href="movielist.html?title=&year=&director=&star=&genre=' + genreArr[j]
                + '">' + genreArr[j] + '</a>';
            genres += "\n";
            //rowHTML += '<a href="movielist.html?title=&year=&director=&star=&genre=' + genreArr[j]
            //+ '">' + genreArr[j] + '</a>';
        }
        rowHTML += "<th>" + genres + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";

        let starsIDArray = resultData[i]["stars_id"].split(",");
        let starsNameArray = resultData[i]["stars_name"].split(",");
        // Add a separate table cell for each movie star
        for (let j = 0; j < Math.min(3, starsIDArray.length); j++) {
            rowHTML += "<th>";
            rowHTML +=
                '<a href="single-star.html?id=' + starsIDArray[j] + '">'
                + starsNameArray[j] +     // display movie_title for the link text
                '</a>'
            rowHTML += "</th>";
        }

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }

    document.getElementById("search_form").reset();
}
sortSelect.addEventListener('change', () => {
    const sortSelect = document.querySelector('#sort-select');
    const [sortByD, sortOrderD] = sortSelect.value.split('-');
    sortBy = sortByD;
    sortOrder = sortOrderD;
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/movielist?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star
            + "&genre=" + genre + "&sortBy=" + sortBy + "&sortOrder=" + sortOrder + "&page=" + page + "&moviePerPage=" + moviePerPage,
        //success: (resultData) => handleStarResult(resultData, moviePerPage) // Setting callback function to handle data returned successfully by the StarsServlet
        success: (resultData) => {
            returnMovieLength = resultData.length;
            if(returnMovieLength < moviePerPage)
            {
                maxPage = page;
            }
            makePage(resultData)
        },
        error: function(xhr, status, error) {
            console.error("sortSelect failed: " + error);
        }
    });

});


const movieListSelect = document.getElementById("movieListSelect");
//let moviePerPage = 10;
movieListSelect.addEventListener("change", function() {
    console.log("1: " + moviePerPage);
    moviePerPage = movieListSelect.value;
    console.log("2: " + moviePerPage);
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movielist?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star
            + "&genre=" + genre + "&sortBy=" + sortBy + "&sortOrder=" + sortOrder + "&page=" + page + "&moviePerPage=" + moviePerPage,
        //success: (resultData) => handleStarResult(resultData, moviePerPage)
        success: (resultData) => {
            returnMovieLength = resultData.length;
            if(returnMovieLength < moviePerPage)
            {
                maxPage = page;
            }
            makePage(resultData)
        },
        error: function(xhr, status, error) {
            console.error("movieListSelect failed: " + error);
        }
    });
});



// $('#search_form') is to find element by the ID "search_form"
$('#title').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
    minChars: 3
});




$(document).ready(function() {
    // Get the form element
    let search_form = $("#search_form");

    // Bind the submit action of the form to a handler function
    search_form.submit(function(event) {
        // Prevent the default form submission
        event.preventDefault();

        let title = search_form.find("#title").val();

        console.log(title);
        // Handle the form submission
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/movielist?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star
                + "&genre=" + genre + "&sortBy=" + sortBy + "&sortOrder=" + sortOrder + "&page=" + page + "&moviePerPage=" + moviePerPage,
            //success: (resultData) => handleStarResult(resultData, moviePerPage)
            data: search_form.serialize(),
            success: (resultData) => {
                returnMovieLength = resultData.length;
                if(returnMovieLength < moviePerPage)
                {
                    maxPage = page;
                }
                makePage(resultData)
            },
            error: function(xhr, status, error) {
                console.error("movieListSelect failed: " + error);
            }
        });
    });
});







// Autocomplete Section


function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")
    console.log("sending AJAX request to backend Java Servlet")

    // TODO: if you want to check past query results first, you can do it here

    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "api/movielist?title=" + query + "&year=" + year + "&director=" + director + "&star=" + star
            + "&genre=" + genre + "&sortBy=" + sortBy + "&sortOrder=" + sortOrder + "&page=" + page + "&moviePerPage=" + moviePerPage,
        "success": function(data) {
            // pass the data, query, and doneCallback function into the success handler
            handleLookupAjaxSuccess(data, query, doneCallback)
        },
        "error": function(errorData) {
            console.log("lookup ajax error")
            console.log(errorData)
        }
    })
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")
    // console.log(typeof data);
    // console.log(data);

    var newData = [];

    // Iterate over each entry in the original data object
    for (var i = 0; i < data.length; i++) {
        var entry = data[i];

        // Create a new object with 'data' and 'value' fields
        var newObj = {
            data: entry.movie_id,
            value: entry.movie_title
        };

        // Push the new object to the 'newData' array
        newData.push(newObj);
    }

    // var finalData = JSON.parse(newData);
    console.log(newData);
    cachedResult = data;





    // TODO: if you want to cache the result into a global variable you can do it here

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: newData } );
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion
    console.log(suggestion);
    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["heroID"])

    var movieIdToMatch = suggestion["data"];

    console.log("Cached Result -------");
    console.log(cachedResult);

    console.log("MovieIdType");
    console.log(typeof movieIdToMatch);

    var finalObject = null;

    for (var i = 0; i < cachedResult.length; i++) {
        var entry = cachedResult[i];
        console.log(typeof entry);
        if (entry.movie_id === movieIdToMatch){
            finalObject = entry;
        }
    }
// Find the object where movie_id matches
    console.log(finalObject);

    makePage2(finalObject);

}

function makePage2(resultData)
{
    console.log("makePage from resultData");
    let starTableBodyElement = jQuery("#star_table_body");

    console.log("Point 1");
    starTableBodyElement.empty();
    // Iterate through resultData, no more than 10 entries

    //for (let i = startIndex; i < endIndex && i < resultData.length; i++){
    // Concatenate the html tags with resultData jsonObject
    let rowHTML = "";
    rowHTML += "<tr>";
    //rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
    rowHTML += "<th>";
    rowHTML +=
        '<a href="single-movie.html?id=' + resultData["movie_id"] + '">'
        + resultData["movie_title"] +     // display movie_title for the link text
        '</a>'
    rowHTML += "</th>";

    console.log("Point 2");
    console.log(resultData["movie_id"]);

    rowHTML += "<th>" + resultData["movie_year"] + "</th>";
    rowHTML += "<th>" + resultData["movie_director"] + "</th>";


    console.log("Point 3");
    console.log(resultData["movie_year"]);
    console.log(resultData["movie_director"]);

    let genreArr = resultData["movie_genres"].split(",");
    let genres = "";
    for (let j = 0; j < Math.min(3, genreArr.length); j++) {
        genres += '<a href="movielist.html?title=&year=&director=&star=&genre=' + genreArr[j]
            + '">' + genreArr[j] + '</a>';
        genres += "\n";
        //rowHTML += '<a href="movielist.html?title=&year=&director=&star=&genre=' + genreArr[j]
        //+ '">' + genreArr[j] + '</a>';
    }
    rowHTML += "<th>" + genres + "</th>";
    rowHTML += "<th>" + resultData["movie_rating"] + "</th>";

    let starsIDArray = resultData["stars_id"].split(",");
    let starsNameArray = resultData["stars_name"].split(",");
    // Add a separate table cell for each movie star
    for (let j = 0; j < Math.min(3, starsIDArray.length); j++) {
        rowHTML += "<th>";
        rowHTML +=
            '<a href="single-star.html?id=' + starsIDArray[j] + '">'
            + starsNameArray[j] +     // display movie_title for the link text
            '</a>'
        rowHTML += "</th>";
    }

    console.log("Point 4");
    console.log(resultData["movie_rating"]);
    console.log(resultData["stars_id"]);
    console.log(resultData["stars_name"]);

    rowHTML += "</tr>";

    // Append the row created to the table body, which will refresh the page
    starTableBodyElement.append(rowHTML);

    document.getElementById("search_form").reset();
}
/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */



