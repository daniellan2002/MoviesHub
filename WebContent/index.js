let cart = $("#cart");


/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    // show the session information 
    $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);
    console.log("Is it? 1");
    // show cart information
    handleCartArray(resultDataJson["previousItems"]);
    console.log("Is it? 2");
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    console.log(resultArray);
    console.log("Handle cart array");
    let item_list = $("#item_list");
    // change it to html list
    let res = "<ul>";
    for (let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point
        res += "<li>" + resultArray[i] + "</li>";
    }
    res += "</ul>";

    // clear the old array and show the new array in the frontend
    item_list.html("");
    item_list.append(res);
}

/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleCartInfo(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.ajax("api/index", {
        method: "POST",
        data: cart.serialize(),
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            console.log("Is it?");
            console.log(resultDataString);
            handleCartArray(resultDataJson["movies"]);
        }
    });

    // clear input form
    cart[0].reset();
}

function fillTable(resultArray){
    console.log("Handel fillTable");
    let cart_list_Element = jQuery("#cart_list");
    let cart_total_Element = jQuery("#cart_total");
    // Create a new table row for each item in the cart
    let total_price = 0;
    for (let i = 0; i < resultArray.length; i++) {
        let row = "";
        row += "<tr>";
        row += "<th>" + resultArray[i]["movie_title"] + "</th>";
        row += "<th>" + resultArray[i]["unit_price"] + "</th>";
        row += "<th><input type='number' id='quantity_" + resultArray[i]["movie_id"] + "' name='quantity' value='" + resultArray[i]["quantity"] + "' min='1'></th>";
        row += "<th>" + resultArray[i]["price"] + "</th>";
        row += "<th><button onclick='deleteItem(\"" + resultArray[i]["movie_id"] + "\")'>Delete</button></th>";
        console.log(row);
        row += "</tr>";
        cart_list_Element.append(row);
        modifyQuantity(resultArray[i]);

        total_price += parseFloat(resultArray[i]["price"]);

    }
    cart_total_Element.text(total_price);
}
function deleteItem(item_id){
    console.log("I'm to be deleted: " + item_id);
    // Add event listener to delete button
    jQuery.ajax({
        url: "api/cart?id=" + item_id,
        method: "DELETE",
        success: function(response) {
            console.log("Delete success");
            window.location.href = 'index.html';
            // You can add code here to show a success message to the user
            // and regenerate the table with updated cart data
        },
        error: function(xhr, status, error) {
            console.error("Error: ", xhr, status, error);
            // You can add code here to show an error message
        }
    });
}
function modifyQuantity(resultElement)
{
    let inputElement = jQuery("#quantity_" + resultElement["movie_id"]);
    inputElement.on("change", function() {
        let newQuantity = inputElement.val();
        let itemId = resultElement["movie_id"];
        console.log("?????? " + itemId);
        console.log(newQuantity);

        // Send AJAX request to update quantity for item with itemId
        jQuery.ajax({
            url: "api/cart",
            method: "POST",
            data: {
                item_id: itemId,
                quantity: newQuantity
            },
            success: function(response) {
                console.log(response);
                window.location.href = 'index.html';
                // You can add code here to show a success message to the user
            },
            error: function(xhr, status, error) {
                console.error("Error: " + error);
                // You can add code here to show an error message
            }
        });
    })
}

$.ajax("api/index", {
    method: "POST",
    data: cart.serialize(),
    success: resultDataString => {
        let resultDataJson = JSON.parse(resultDataString);
        console.log("New1 ?");
        console.log(resultDataString);
        fillTable(resultDataJson);
    }
});



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


$.ajax("api/index", {
    method: "GET",
    success: handleSessionData
});


// Bind the submit action of the form to a event handler function
cart.submit(handleCartInfo);
