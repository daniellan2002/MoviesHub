

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
        alert("Successfully added new star: " + resultDataJson["star_id"]);
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        // $("#login_error_message").text(JSON.stringify(resultDataJson["errorMessage"]));
        alert(resultDataJson["errorMessage"]);
    }
    document.getElementById("addStar_form").reset();
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
// function submitAddStarForm(formSubmitEvent) {
//     console.log("submit add star form");
//     /**
//      * When users click the submit button, the browser will not direct
//      * users to the url defined in HTML form. Instead, it will call this
//      * event handler when the event is triggered.
//      */
//
//     console.log("Point 3");
//     formSubmitEvent.preventDefault();
//
//     $.ajax(
//         "api/addstar", {
//             method: "GET",
//             // Serialize the login form to the data sent by POST request
//             data: addStar_form.serialize(),
//             success: handleLoginResult
//         }
//     );
// }

// Wait for the DOM to be fully loaded
$(document).ready(function() {
    // Get the form element
    let addStar_form = $("#addStar_form");

    // Bind the submit action of the form to a handler function
    addStar_form.submit(function(event) {
        // Prevent the default form submission
        event.preventDefault();

        // Handle the form submission
        $.ajax(
            "api/addstar", {
                method: "GET",
                // Serialize the login form to the data sent by POST request
                data: addStar_form.serialize(),
                success: handleLoginResult
            }
        );
    });
});


console.log("Point 2");
// Bind the submit action of the form to a handler function