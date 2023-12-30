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
$(document).ready(function() {
    $('#search-form').submit(function(event) {
        event.preventDefault();
        var title = $('#title').val();
        var year = $('#year').val();
        var director = $('#director').val();
        var star = $('#star').val();
        var params = {};
        if (title !== '') {
            params.title = title;
        }
        if (year !== '') {
            params.year = year;
        }
        if (director !== '') {
            params.director = director;
        }
        if (star !== '') {
            params.star = star;
        }

        let genre = '';

        console.log(star);
        window.location.href="movielist.html?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star + "&genre=" + genre;
        // $.ajax({
        //     url: 'api/movielist',
        //     type: 'GET',
        //     data: params,
        //     success: function(data) {
        //         $('#movie-list').html(data);
        //     }
        // });
    });
});

