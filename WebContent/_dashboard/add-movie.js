

console.log("Point 1");
/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    console.log(resultDataString);
    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        alert("Successfully added new movie: [" + resultDataJson["movieid"] + "] StarId: [" + resultDataJson["starid"] + "] GenreId: [" + resultDataJson["genreid"] + "]");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        // $("#login_error_message").text(JSON.stringify(resultDataJson["errorMessage"]));
        alert(resultDataJson["message"]);
    }
    document.getElementById("addMovie_form").reset();
}


// Wait for the DOM to be fully loaded
$(document).ready(function() {
    // Get the form element
    let addMovie_form = $("#addMovie_form");

    // Bind the submit action of the form to a handler function
    addMovie_form.submit(function(event) {
        // Prevent the default form submission
        event.preventDefault();

        // Handle the form submission
        $.ajax(
            "api/addmovie", {
                method: "GET",
                // Serialize the login form to the data sent by POST request
                data: addMovie_form.serialize(),
                success: handleLoginResult
            }
        );
    });
});


console.log("Point 2");
// Bind the submit action of the form to a handler function