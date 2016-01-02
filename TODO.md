#To Do:
* Finish designing rest of API
* Implement App Login
* Design activities
* Implement "Flow" from one screen to the next
* Dress code info activity

#Functionality:
* Log in and out
* Take your picture (or a pic from phone) and upload to server
  * Client will handle taking pics and making sure they match to specifications
  * Send location coordinates
  * Server time stamps
  * Clean up script
* Fetch other people's pictures from server
  * Who’s picture to fetch
    * Friends system
    * Geo-coordinates: x closest from current location
* School/FBLA group
  * App ->  location services ->  report its current location + how many pics it wants
* Server will give the pics
  * How do we fetch pictures
    * Problem: we want to fetch minimal pictures
    * Updating: someone close uploads a new picture, but we don’t want to re-download all the pictures -
    * Andrew’s proposal
      * by default, download only new, as needed (thing comes in from server to tell us when on periodic request). We add to database, and show. Periodically, to save space, we delete old records (records saved by date downloaded in DB on phone)
    * Yi’s proposal:
      * When you request pics, it gives you a list of ids
      * You can request to download each individual ids
      * Every Time you check, you request a new list of ids, match them up with your current list, and see which new ones you need downloaded
    * Jack’s proposal:
      * We store a date of last download on phone
      * We pull updates that have happened from the date stored on phone
      * What if you change location?
      * Billy is in New York, it’s 5:00pm, his five closest things that he fetches:
      * John (1:00), Ben (1:30), Steven (1:45), Annie (2:00), Margret (5:00)
      * Billy goes to Boston at 6:00pm
      * Tom in Boston took a picture and uploaded it at 3:00pm
      * Billy won’t get the photo because Billy requests everything uploaded past 6:00pm
    * Storing the photos
      * Store all the photos we download along with the id
      * Clean up:
        * x photos, and delete the oldest photos to always make room for new ones
* Evaluating pictures to specifications
  * Commenting-do we need it?
  * Showing approval
    * Upvote/downvote !!
    * Heart
    * Rating (x/5)
  * Yi’s proposal
    * Rating mode and a viewing mode
    * Rating mode: rate in the categories
    * Viewing mode: only like something
  * proposal v2
    * Ask user if outfit is in dress code
    * Yes: let them evaluate style/whatever and comment
    * No: only comment
  * Persistence file for saving settings, pictures

#Priority:
1. Login
2. Server side API for picture storing
3. Taking pictures
4. fetching pictures, storing cache on device
