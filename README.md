# s23-122b-kickin-Daniel Lan

### New Repo Name After Split: stadia99-cs122B

# Project 2
We implemented the substring matching design using LIKE and %

E.g.

sql += "title LIKE ? ";
if (title.length() == 1){
params.add(title + "%");
}
else{
params.add("%" + title + "%");
}



# Project 3
 Optimization strategies:
 1. Create hash table for seen objects. Program don't need to send 40000+ request to sql server to look up if the element already exists in the database. 
 2. Store and track the max ID number on local, so the program don't need to request database each time to fetch the MAX id of the table.
 Using the above techiniques, we're able to cut down running time for 5 minutes to 14 seconds.
 
 Inconsistency_report:
 1. For main243.xml: if the movie title already exists in the database, the new record will not be added and a log will added to the report.
 2. For actor6.xml: If the star name already exists in the database, the new record will not be added and a log will added to report.
 3. For casts.xml: If the star name or movie title not in our database database, the new record will not be added and a log will added to report.



Daniel Lan:
Build Movie List, Single Movie page, Single Movie page, Search page, Browse Movies Page, Substring-Matching-design. Completed the Java servlet and HTML and JavaScript files. Debugged and fixed mysql connection errors. Added recaptcha, added HTTPS, built dashboard table and stored procedures. 

# Project 4:

### Fulltext Search

- ✅ Have a main search box on the Main Page (or a separate search page) of the Fabflix website. You can reuse the search box if you already have one from Project 2.
- ✅ Implement full-text search with the query on the movie title.
- ✅ If the customers click the "Search" button, the site should jump to the Movie List Page that displays the full-text search results.
- ✅ make a fulltext index on each searchable column

### Autocomplete

- ✅The Autocomplete is required to perform full-text search on movie title field.
- ✅ The Autocomplete suggestion list should not have more than 10 entries.
- ✅ The Autocomplete should support keyboard navigation using ↑ ↓ arrow keys. When a suggestion entry is selected, the entry should be highlighted, the text (query) in the search box should be changed to the entry's content (say, movie title).
- ✅ Clicking on any of the suggestion entries, or pressing "Enter" Key if an entry is selected during keyboard navigation, it should jump to the corresponding Single Movie Page directly.
- ✅ If the customer just presses "Enter" Key or clicks the search button without selecting any suggestion entry, it should perform the same full-text search action as stated above in the "full-text Search" requirement.
- ✅ When the customer types only one or two characters, it should not perform any Autocomplete search because the results may not be helpful yet. You should only perform the Autocomplete search when the customer types in at least 3 (>= 3) characters.
- ✅ When the customer types in the query, it should not perform the Autocomplete search on every keystroke because the customer is still typing. Moreover, you don't want to send too many requests to the backend at the same time. Set a small delay time (300ms) so that the frontend only performs the Autocomplete search after the customer stops typing for that delay.
- ✅ If the Autocomplete query has been sent to backend server before, it is not ideal to send the duplicate query to the backend server again (for example, when the customers delete some of the characters from the query).
    - Cache the suggestion list of each query (sent to the backend server) in the frontend, you can use LocalStorage or SessionStorage.
    - Whenever Autocomplete search is triggered, check if the query and its suggestion list are in the cache. If not, send the query to the backend server to get a new suggestion list.
- ✅ The Autocomplete search needs to be very fast. Note that the total running time the customer feels is "delay time + query time". Make sure that each Autocomplete search finishes within 2s.
- ✅ To verify that the implementation satisfies the requirements, please print logs to the Javascript console using "console.log()".
    - The logs are outputting to DevTools "Console" panel.
- ✅ Print and only print log for the following cases:The Autocomplete search is initiated (after the delay);Whether the Autocomplete search is using cached results or sending an ajax request to the server;The used suggestion list (either from cache or server response).


### Android
- ✅ Login page, which should behave like the website login page. (without reCAPTCHA).
- ✅ Used HTTPS Connections to communicate with the backend server.
- ✅ Self-signed HTTPS certificates check is disabled by the NukeSSLCerts class, which is invoked inside NetworkManager.
- ✅ Sessions are maintained by the CookieHandler and CookieManager set in the NetworkManager class.
- ✅ Movie List Page
- ✅ Displays a ListView of the movies searched. When a customer clicks on an item, it should show the corresponding Single Movie Page in a new activity.
- ✅ Each item on the result list should contain the information of a movie: title, year, director, the first 3 genres (hyperlink is optional), the first 3 stars (hyperlink is optional), the same as Project 2.
- ✅ Pagination on the search result list. Previous and Next buttons are required, and the page size is limited to 10.
- ✅ Single Movie Page
- ✅ Single Movie Page should contain the movie title, year, director, all genres (hyperlink is optional), all stars (hyperlink is optional).
- ✅ Main Page
- ✅ A search box that has the same behavior as the full-text search requirement in task 1 (searching in movie title). Autocomplete is optional.


# Project 5

- # General
    - #### Team#:
    
    - #### Name: Daniel Lan
    
    - #### Project 5 Video Demo Link: https://drive.google.com/file/d/1cbmuqDrIZxgCPy3lk1ofUVnCFzojh3ZG/view?usp=sharing

    - #### Instruction of deployment: deployed on AWS
1. Master and Slave Instances:

AWS: Launch two EC2 instances, one serving as the master instance and the other as the slave instance.

2. Apache Load Balancers:

Scale up Fabflix to handle massive traffic using a MySQL and Tomcat cluster. Since the user would have only one entry point to Fabflix, we need a load balancer to balance the traffic from one entry point to multiple Fabflix instances.

3. Deployment Architecture:

AWS: Associate the master and slave instances by internally wiring mysql writes to master and reads to slave. 

GCP: Configure the Apache2 webserver to use its proxy_balancer  module for sharing (i.e., redirecting) requests to the backend instances.

- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
  - WebContent/META-INF/context.xml
- src/MovieListServlet.java
- src/LoginServlet.java
- src/ConfirmServlet.java
- src/AddStarServlet.java
- src/AddMovieServlet.java
- src/SingleStarServlet.java
- src/SingleMovieServlet.java
- src/Payment.java
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
  The connection pooling is used in the fablix code by first being configured in context.xml, then at the application startup or when the database connection pool is first required, a set of initial connections to the database is created. These connections are added to the connection pool.
  When a request is made to the database, instead of establishing a new connection, the application retrieves an available connection from the connection pool, these are handled by the two data sources, /jdbc/master and /jdbc/slave.
  After a connection is obtained from the pool, it is used to perform the required database operations. Once the operations are completed, the connection is returned to the pool for reuse rather than being closed.
    - #### Explain how Connection Pooling works with two backend SQL.
  Connection pooling works by having the two mysql data sources in the backend located in two different AWS instances that recycles a pool of connections instead of having to create a new connection.
  To distribute the load evenly across the two backend databases, a load balancing mechanism is employed by wiring read requests to either slave or master, but write requests to master only. This mechanism can be implemented at the application level or through specialized load balancing tools. 
The data source would just reach into the connection pool to use a connection, and after it’s finished, it would put the connection resource back into the pool again, saving the time to have to start and destroy a connection resource.

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
- src/MovieListServlet.java
- src/LoginServlet.java
- src/ConfirmServlet.java
- src/AddStarServlet.java
- src/AddMovieServlet.java
- src/SingleStarServlet.java
- src/SingleMovieServlet.java
- src/Payment.java
    - #### How read/write requests were routed to Master/Slave SQL?
  - Read and write requests are routed to the master and slave sql by each servlet that uses either only a read request, in which it would be routed to slave, or read and write request, in which it would be routed to the master’s sql.
    Write requests, which modify data in the database, are typically directed to the master SQL database.
    The application or database access layer is configured to send all write requests exclusively to the master database.
    This ensures that changes are made to a single authoritative source of data, maintaining data consistency.
    The master database handles these write requests and performs the necessary operations to update the data.
  - Read requests, which retrieve data from the database, can be routed to both the master and slave SQL databases.
    The application or database access layer can be configured to distribute read requests between the master and slave databases.
    To ensure data consistency between the master and slave databases, data replication mechanisms are typically employed.
    Changes made to the master database are replicated to the slave database(s) to keep the data synchronized. Only from master to slave and not slave to master.
- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
shell> python log_processing.py
(Change file name within)

- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis**                                    |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|-------------------------------------------------|
| Case 1: HTTP/1 thread                          |  JMeter_Screen_Shots_Final/S1_case1.png  | 410                      | 266.624                                |266.316                        | These values are relatively lower because only a single user is accessing the system, resulting in less concurrent load on the server.             |
| Case 2: HTTP/10 threads                        | JMeter_Screen_Shots_Final/S1_case2.png  | 1359                        | 1263.207                             | 1262.844                  | The average query time increases to 1359 ms, and the average search servlet time is 1263.207 ms. With multiple threads accessing the system simultaneously, the server has to handle a higher load, leading to increased response times compared to Case 1.         |
| Case 3: HTTPS/10 threads                       | JMeter_Screen_Shots_Final/S1_case3.png  | 1432                       | 1369.418                             | 1369.148                       | The average query time further increases to 1432 ms, and the average search servlet time is 1369.418 ms. The added overhead of HTTPS encryption contributes to slightly higher response times compared to Case 2.           |
| Case 4: HTTP/10 threads/No connection pooling  | JMeter_Screen_Shots_Final/S1_case4.png  | 3174                         | 2606.922                                  | 2606.655                        | n this case, the average query time significantly increases to 3174 ms, and the average search servlet time is 2606.922 ms. Disabling connection pooling results in establishing a new connection for each request, causing additional overhead and significantly higher response times. |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis**                        |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|-------------------------------------|
| Case 1: HTTP/1 thread                          | JMeter_Screen_Shots_Final/S2_case1.png  | 392                         | 642.19                                  | 647.306                        | These values indicate lower response times due to a single user accessing the system. |
| Case 2: HTTP/10 threads                        | JMeter_Screen_Shots_Final/S2_case2.png  | 944                         | 943.426                                  | 943.090                        | The average query time increases to 944 ms, and the average search servlet time is 943.426 ms. With multiple threads and users, the system experiences a higher load, resulting in increased response times compared to Case 1.                            |
| Case 3: HTTP/10 threads/No connection pooling  | JMeter_Screen_Shots_Final/S2_case3.png | 1196                         | 1002.307                                  | 1001.965                        | Disabling connection pooling leads to a further increase in response times. The average query time is 1196 ms, and the average search servlet time is 1002.307 ms, indicating higher overhead and increased response times compared to Case 2.                             |



Demo URL: https://drive.google.com/file/d/1cbmuqDrIZxgCPy3lk1ofUVnCFzojh3ZG/view?usp=sharing

