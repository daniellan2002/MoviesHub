
function get_genre(btn_id){
    let title = "";
    let year = "";
    let director = "";
    let star = "";

    console.log(typeof btn_id);
    window.location.href="movielist.html?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star + "&genre=" + btn_id;
}

function get_title(btn_id){
    let year = "";
    let director = "";
    let star = "";
    let genre = "";

    console.log(typeof btn_id);
    window.location.href="movielist.html?title=" + btn_id + "&year=" + year + "&director=" + director + "&star=" + star + "&genre=" + genre;
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
// jQuery.ajax({
//     dataType: "json", // Setting return data type
//     method: "GET", // Setting request method
//     url: "api/movielist?genre=" + get_genre(), // Setting request url, which is mapped by StarsServlet in Stars.java
//     success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
// });