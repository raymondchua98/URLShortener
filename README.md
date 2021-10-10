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
* `createUrl(targetUrl)` - When user passed in a **Target URL**, this function will return a **Short URL**

## Algorithms and Logics
To create a short URL, the algorithm applied will be using **Base62** encoding, generating a combination of keys from [A-Z, a-z, 0-9] where the maximum length will be 15 characters. The maximum number of combinations will be 62^15 = ~768,909,704,948,766,668,552,634,368

After a **Short URL** is generated, unlike SQL which we can add UNIQUE index to the column, we will need to purposely retrieve and validate whether the ShortURL had already been assigned to any other URLs which could be a serious problem as system scales. 

To solve it, instead of directly converting the URL which could result in duplicated shortened URL, we can make some modification to the URL. For instance, we can append with the **timestamp of creation** to ensure that the URL can be as unique as possible and reduce the risks of duplications.

To ensure scalability, we can have a counter to keep track of the number of URLs generated. Then, we will concat the counter with the Target URL before we shorten the URL.

> Shortened URL generated will be using a combination of Target URL + Date.now()

> The static Date.now() method returns the number of milliseconds elapsed since January 1, 1970 00:00:00 UTC.

## Database
Since this system will be using database very often to save / retrieve URLs, the rows of data might go up to millions or billions. Thus, **NoSQL Database** will be used to ensure scalability of the system.

## Cache
To reduce the need of reading, we can apply Cache to store the created URL. However, cache's size shall be around 20% of the total traffic in order to improve speed and reduce cost. To clear cache when the cache storage is full, we can apply Least Recently Used (LRU) policy to keep track of only the URLs that are often accessed by users. Whenever a cache miss occurs, we will create a new entry in cache storage and kicks out the LRU cache. 

## Redirection Logic
Whenever a user accessed a Short URL, our server will look up in the storage.

If shortened URL is found in Cache, we will return the data to server. Else, we will return a message to indicate URL is not found in cache. 

Next, if URL is found in Cache server, we will proceed to verify the shortened URL. If valid, we will pass HTTP 301 to redirect. Else, we will pass HTTP 401 to indicate error.

However, if no URL is found in the Cache, we will look up in the Database storage and update the Cache storage. If found, we will validate the data and redirect accordingly. Else, we will redirect user to homepage and indicate shortened URL is not found.

Upon any successful redirection, we will increase the counter of clicks to keep tracks of how many visits to the Short URL.


