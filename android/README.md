# Project 4 Android App

- Login page, which should behave like the website login page. (without reCAPTCHA).
- Used HTTPS Connections to communicate with the backend server.
- Self-signed HTTPS certificates check is disabled by the NukeSSLCerts class, which is invoked inside NetworkManager.
- Sessions are maintained by the CookieHandler and CookieManager set in the NetworkManager class.
- Movie List Page
- Displays a ListView of the movies searched. When a customer clicks on an item, it should show the corresponding Single Movie Page in a new activity.
- Each item on the result list should contain the information of a movie: title, year, director, the first 3 genres (hyperlink is optional), the first 3 stars (hyperlink is optional), the same as Project 2.
- Pagination on the search result list. Previous and Next buttons are required, and the page size is limited to 10.
- Single Movie Page
- Single Movie Page should contain the movie title, year, director, all genres (hyperlink is optional), all stars (hyperlink is optional).
- Main Page
- A search box that has the same behavior as the full-text search requirement in task 1 (searching in movie title). Autocomplete is optional.