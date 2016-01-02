# API Usage Guide

## General Protocol
Server data will always be sent down in JSON format in the following protocol:

##### Success

```
{
  "status": "success",
  "data": {
    //return data will appear here
  },
  "message": null //optional success message
}
```

##### Failure

```
{
  "status": "error",
  "data": null,
  "message": "Error message" //Custom error message
}
```

**ND specifies that the action will have a NULL `data` payload back from the server**

## Testing

Basic server tests. Destination: `api/test/action`

#### get (GET, ND)

Get request test, server will always send back success

#### post (POST)

Send:
+ foo: test paramater, can be anything

Server will indicate success if `foo` parameter is set. Recieve:
+ fooback: server will echo foo back

## User actions

Handles all user actions. Destination: `/api/user/action`.

#### register (POST, ND)

Send:

+ username: user username
+ password: user password

#### login (POST)

Send:
+ username: user username
+ password: user password

Recieve:
+ authcode: authentication code

**After recieving the authentication code, all future API requests MUST have the authentication code attached as a GET parameter.**

Example: `/API/user/info?authcode=XxxxXxxxX`

#### verify (GET, ND)

Verifies authcode is valid or invalid, returns success/failure.

#### info (GET)

Returns user info

Recieve:
+ username: username of user
+ more to come

#### logout (GET, ND)

Logs out of account, CHANGES authcode!

## Picture Handling

Handles everything picture related. Destination: `api/picture/action`

#### upload (POST, ND)

Send:
picture: picture file in to be specified format
geoloc: current geographic location

#### fetch (GET)

Send (URL params):
+ amount: amount of images to recieve
+ geoloc: current geographic location

Recieve:
A list of image ids with there geoloc and created-by timestamp along with a SHA-256 hash of the image
```
{
    {
        "pid": xxx, //picture id
        "geoloc": //pic taken location
        "createtime": //created by time
        "psha": //SHA-256 hash of the picture
    }
}
```

#### {id} (GET)

Fetches actual image data

Recieve:
Actual picture data

#### {id} (DELETE, ND)

Deletes image
