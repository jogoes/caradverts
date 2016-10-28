# Car Advert REST Service

## Implementation Notes

This is basically a standard Play sbt project which can be built using the known sbt tasks.

Note, that for running the application you need to specify the application secret. 
One way to do this is to create e.g. a file named `production.conf` like this:

    include "application"
    
    play.crypto.secret="nWXNHEMfSmdVMWgkPefAfSo[E7V8EGu_dN1qPutkzI]K`4;<fy0F<P>1c<SLNH0C"

In order to run the application you can e.g. create a .zip using `sbt dist`, uncompress it, 
put the `production.conf` file into the uncompressed folder and run the application from
this folder:

    bin/caradverts -Dconfig.file=production.conf
    
One note regarding the repositories: this implementation currently contains 
three different repository implementations. There is the `TransientInMemoryCarAdvertRepository`
which is basically a very simple transient in-memory repository which was the first 
implementation just in order to get started.
The `JdbcCarAdvertRepository` is a pure JDBC-based implementation which I implemented in
order to provide a persistent storage. But since Jdbc has its issues (type safety etc.) and 
I wanted to try jOOQ in combination with Play anway I added another jOOQ-based implementation 
in `JooqCarAdvertRepository`.

jOOQ is based on generated code which can be created using the `generateJOOQ` sbt task. 
Note, that this requires the evolutions being applied to the database in order to generate
the correct code. In order to keep things simple, the latest generated code has been added
 to the repository.

The reason for a Jdbc-based implementation in combination with the H2-database was to keep 
dependencies to external components to a minimum and prevent e.g. connection or configuration 
issues in case you want to run the application.

The application is currently configured to create and search the database in the users 
home directory. In order to change this override the setting for the database you can add
 an entry as below to the production.conf file:

    db.default.url = "jdbc:h2:file:~/caradvert"

## REST API

### Responses

The successful execution of a request is indicated by a 2xx HTTP response.

In case of an error in the execution the server returns with a corresponding non-2xx HTTP response 
with the body containing additional details about the failed request. The response contains a 
JSON body containing an error code and a message similar to the following:

    {
      "code": 4001,
      "message": "Item with id '75fb4ade-f5c7-4da3-b88a-4d6b7d8c42a8' not found."
    }

### Requests

#### GET /caradverts[?sortby=\<field\>]

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

#### GET /caradverts/\<uuid\>

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
    
#### DELETE /caradverts/\<uuid\>

Deletes the car advert with the specified id.
 
In case of success a 204 response is returned.
A 404 response is returned in case an advert with the specified id doesn't exist.


#### PUT /caradverts

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

In case of success a 204 response is returned.
A 404 response is returned in case an advert with the specified id doesn't exist.

#### POST /caradverts

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

In case of success a 201 response is returned.
A 400 response is returned in case an advert with the specified id already exists.

### Possible Improvements

* for an update it's currently necessary to always send a full object description, 
ideally only part of the object should be necessary in order to support partial updates.

