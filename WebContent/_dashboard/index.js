

/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */


/**
 * Submit form content with POST method
 * @param cartEvent
 */







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

