# API Usage Guide

### General Protocol
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

**ND specifies that the action will have a NULL `data` payload**

### User actions

Handles all user actions. Destination: `/API/user/action`.

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

Recieve: `null`

#### info (GET)

Returns user info

Recieve:
+ username: username of user
+ more to come

#### logout (GET, ND)

Logs out of account, CHANGES authcode!
