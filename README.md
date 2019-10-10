# Getting Started

### HTTP Status Codes
HTTP status codes are divided into five categories:

Informational 1xx: indicates that the request was received and understood.

Successful 2xx: indicates the action requested by the client was received, understood, accepted, and processed successfully.

Redirection 3xx: indicates the client must take additional action to complete the request.

Client Error 4xx: indicates the client seems to have erred.

Server Error 5xx: indicates the server has encountered an error during the process.

Following status code has been used in this example:

| ID		| Status Code	|	Description																							|
| ---: 	| ---:	| ---: |
| 200	|	Success	|	The request has succeeded |
| 201	|	Created	|	The request has been fulfilled and resulted in a new resource being created |
| 204	| No Content |	The request has fulfilled the request but does not need to return an entity body |
| 206	| Partial Content |	The server has fulfilled the partial GET request for the resource |
| 400	| Bad request |	The request could not be understood by the server due to malformed syntax |
| 404	| Not Found	| The server has not found anything matching the request URI |
| 405	| Method Not allowed |	The method specified in the request is not allowed for the resource identified by the request URI |
| 409	| Conflict	|The request could not be completed due to a conflict with the current state of resource |


### API Endpoints
HTTP method	URI	Description	Valid HTTP status codes


| HTTP method   | URI           		| Description 												| Valid HTTP status codes 	|
| ------------- |:--------------------:	| --------------------------------------------------------	|:-------------------------:	|
| POST      	| /rest/vi/books 		| 	Create a book 											|			201				|
| GET     		| /rest/vibooks/{id}    |   Read a book 											|			200				|
| PUT 			| /rest/vibooks/{id}    |   Update a book											|			200				|
| DELETE		| /rest/vibooks/{id} 	|	Delete a book											|			204				|
| PATCH			| /rest/vibooks/{id}	|	Update book description									|			200				|
| GET			| /rest/vibooks/{id}	|	Retrieve all books by pagination, sorting and ordering	|	200, 204, 206			|