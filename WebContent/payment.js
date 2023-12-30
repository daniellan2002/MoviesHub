let payment_form = $("#payment-form");
let errorMessage = $("#error-message");
let price_total =  $("#cart_total");
function handleResult(resultDataString) {
    console.log("handle login response");
    if (resultDataString === "True") {
        // Payment succeeded, do something
        console.log("land TRUE");
        window.location.href = 'confirm.html';

    } else {
        // Payment failed, do something else
        console.log("land FALSE");
        errorMessage.append("Payment failed. Please enter correct information.");
    }
}

function submitPaymentForm(formSubmitEvent) {
    console.log("submit payment form");
    let date = $("#expiration-date");
    console.log(date);
    if(isValidDate(date))
    {
        errorMessage.append("Wrong expiration date. Please enter in format YYYY-MM-DD")
        return;
    }
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: payment_form.serialize(),
            success: handleResult,
            error: function(xhr, status, error) {
                console.error("Error: " + error);
            }
        }
    );
}
payment_form.submit(submitPaymentForm);

function isValidDate(dateString) {
    // Regular expression to match the date format "YYYY-MM-DD"
    var dateFormat = /^\d{4}-\d{2}-\d{2}$/;

    // Test if the string matches the date format
    return dateFormat.test(dateString);

}

$.ajax(
    "api/payment", {
        method: "GET",
        // Serialize the login form to the data sent by POST request
        success: (resultData) => addTotalPrice(resultData),
        error: function(xhr, status, error) {
            console.error("Error: " + error);
        }
    }
);

function addTotalPrice(resultData){
    console.log(resultData);
    console.log("Get sum of cart");
    // let sum = resultData.totalPrice;

    price_total.append(resultData);
}

