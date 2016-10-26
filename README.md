# Car Advert REST Service

##### GET /caradverts[?sortby=\<field\>]

Returns a list of existing car adverts. The optional sortby request parameter 
can be used to return the list sorted by the specified field. Possible field values are:
* id
* title
* fuel
* price
* isnew
* mileage
* firstregistration

Multiple car adverts are returned as a JSON array similar to the following:

    [
      {
        "id": "75fb4ade-f5c7-4da3-b88a-4d6b7d8c42a9",
        "title": "zzzz",
        "fuel": "GASOLINE",
        "price": 100,
        "isnew": false,
        "mileage": 123,
        "firstRegistration": "2015-10-11"
      },
      {
        "id": "75fb4ade-f5c7-4da3-b88a-4d6b7d8c42a8",
        "title": "advert1",
        "fuel": "GASOLINE",
        "price": 1234,
        "isnew": false,
        "mileage": 5678,
        "firstRegistration": "2016-10-11"
      }
    ]

##### GET /caradverts/\<uuid\>

Returns a car advert given its id. The advert is returned as JSON similar to the following:

    {
      "id": "75fb4ade-f5c7-4da3-b88a-4d6b7d8c42a9",
      "title": "zzzz",
      "fuel": "GASOLINE",
      "price": 100,
      "isnew": false,
      "mileage": 123,
      "firstRegistration": "2015-10-11"
    }
    
In case of success a 200 response is returned.
A 404 response is returned in case an advert with the specified id doesn't exist.
    
##### DELETE /caradverts/\<uuid\>

Deletes the car advert with the specified id.
 
In case of success a 200 response is returned.
A 404 response is returned in case an advert with the specified id doesn't exist.


##### PUT /caradverts

Updates the car advert with the given id. The JSON is expected to be sent in the body in a format similar to the following:
 
    {
      "id": "75fb4ade-f5c7-4da3-b88a-4d6b7d8c42a9",
      "title": "zzzz",
      "fuel": "GASOLINE",
      "price": 100,
      "isnew": false,
      "mileage": 123,
      "firstRegistration": "2015-10-11"
    }

In case of success a 200 response is returned.
A 404 response is returned in case an advert with the specified id doesn't exist.

##### POST /caradverts

Adds a new car advert. The specified id must not exist.
The JSON is expected to be sent in the body in a format similar to the following:
 
    {
      "id": "75fb4ade-f5c7-4da3-b88a-4d6b7d8c42a9",
      "title": "zzzz",
      "fuel": "GASOLINE",
      "price": 100,
      "isnew": false,
      "mileage": 123,
      "firstRegistration": "2015-10-11"
    }

A 400 response is returned in case an advert with the specified id already exists.

### TODOS

* for an update it's currently necessary to always send a full object description, 
ideally only part of the object should be necessary in order to support partial updates.
* ideally, responses are always JSON. In error cases this is currently not the case. 
An error response code is correctly returned, but the body of the response is not always JSON.

