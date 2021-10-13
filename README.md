# URL Shortener 
 ## Requirements
 * Your application is deployed with a web interface and a form field that accepts a **Target URL**.
 * When the **Target URL** is shortened, the user is returned with a **Short URL**, the **original Target URL** and the **Title tag** of the Target URL.
 * A Short URL can be publicly shared and accessed.
 * A Short URL path can be in any URI pattern, but should not exceed **a maximum of 15 characters**
 * Multiple Short URLs can share the same Target URL.
 * You need to produce a simple **usage report** for the application. This report should track the number of **clicks**, originating **geolocation** and **timestamp** of each visit to a **Short URL**.
 
## APIs
The system APIs will be REST APIs
* `/createUrl(targetUrl)` - Create a new URL in database and return with short URL that lasts for 30 days, URL title etc
* `/r/{shortCode}` - Redirect user to target URL by short code and add new access event history
* `/report/short-code` - Generate usage report for the short code entered

## Algorithms and Logics
The system's algorithm to generate Short URL was inspired by [JNanold's](https://github.com/aventrix/jnanoid) approach of using Java's [SecureRandom](https://docs.oracle.com/javase/7/docs/api/java/security/SecureRandom.html) to generate strong random IDs with a proper distribution of characters. The number of random characters is 10, forming a maximum number of 62^10 = 839,299,365,868,340,224 combinations.
> Regex used is '[A-Z], [a-z], [0-9]'
 
After Short URL is generated, the project will return the Short URL, Target URL Title, Short Code which can be used to view the usage report afterwards. 

## Database
Since this system will be using database very often to save / retrieve URLs, the rows of data might go up to millions or billions. Thus, **MongoDB**, a NoSQL Database will be used to ensure scalability of the system.

Note: In rare cases where you are unable to connect to the remote database, check if you have any VPN / proxy connection established.


## How to deploy
1. Open cmd and change directory to your desired location
2. Enter 'git clone https://github.com/raymondchua98/URLShortener.git'
3. Run 'mvnw spring-boot:run'
4. Project will be built in your local which is usually localhost:8080
5. Enjoy

## Dependencies used
The dependencies files are in pom.xml
1. junit-platform-launcher
2. junit-jupiter-api
3. junit-jupiter-engine
4. spring-boot-starter-test
5. spring-boot-starter-web
6. spring-boot-starter-data-mongodb

## External Service used
1. IPGeolocation - To capture geo location of originating IP of short url access event. For free version, it allows up to 1000 requests per day.

## Possible enhancements
Whenever a user accessed a Short URL, our server will look up in the Cache storage.
If shortened URL is found in Cache storage, we will return the data directly instead of searching through database. Else, we will return a message to indicate URL is not found in Cache storage and perform Least Recently Used (LRU) approach to clear off a cache block. 
